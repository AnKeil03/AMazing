package drms.server;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;
import java.util.LinkedList;
import drms.server.entity.Connection;
import drms.server.command.CommandHandler;


public class Server implements Runnable {


    public static final int PORT = 80; //HTTP Port
    public static final int MAX_SIM_CONNECTS = 10; //max number of connections to listen for at once
    public static final int MAX_CONNECTIONS = 100; //max number of connected clients at one time

    private int numConnections,nextConnectionID; //connection management stuff
    private LinkedList<Integer> recycledIDs;

    private ServerSocketChannel socketChannel;
    private ByteBuffer buffer;
    private Selector selector;

    private Connection connections[];


    public Server() {

        socketChannel = null;
        buffer = ByteBuffer.allocate(1024);
        selector = null;

        numConnections = 0;
        nextConnectionID=0;
        recycledIDs = new LinkedList<>();
        connections = new Connection[MAX_CONNECTIONS];
        for (int i=0; i<MAX_CONNECTIONS;i++)
            connections[i]=null;

        CommandHandler.initCommands();

    }

    /* run():
        called when this thread is started
    */

    public void run() {

        boolean killServer = false;
        int xx=0;

        try {
            // welcomeSocket = new ServerSocket(PORT);
            socketChannel = ServerSocketChannel.open();
            socketChannel.socket().bind(new InetSocketAddress(PORT));
            socketChannel.configureBlocking(false);

            System.out.println("Starting server on port "+PORT+"...");

            selector = Selector.open();

            socketChannel.register(selector, SelectionKey.OP_ACCEPT);
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String build = "";
            while (!killServer) {


                int numConnects = selector.select(MAX_SIM_CONNECTS); //accept MAX_SIM_CONNECTS incoming connections at a time

                //text input from terminal for server admin commands
                char ch = 0;
                if (br.ready()) {
                    try {
                        ch = (char)br.read(); //read input one char at a time
                        build+=ch;
                    } catch (IOException e) {
                        System.out.println("Error reading from Input device");
                    }
                }
                if (!br.ready() && build.length()>0) {
                    build = build.substring(0, build.length()-1); //remove \n at end
                    //System.out.println("typed: "+build);
                    CommandHandler.processCommand(build);
                    build="";
                }
                if (numConnects != 0) {
                    Set keys = selector.selectedKeys(); //current sockets
                    Iterator it = keys.iterator();
                    while (it.hasNext()) {
                        SelectionKey key = (SelectionKey)it.next(); //key to deal with

                        if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) { //incoming connection

                            Socket s = socketChannel.socket().accept();

                            SocketChannel sc = s.getChannel();
                            sc.configureBlocking(false);

                            // Register it with the selector for reading
                            sc.register(selector, SelectionKey.OP_READ);

                            registerConnection(s); //register the connection with connection manager

                        } else if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) { //incoming data
                            SocketChannel sc = (SocketChannel) key.channel();

                            try {

                                sc = (SocketChannel) key.channel(); //clear the buffer
                                buffer.clear();
                                buffer.put(new byte[1024]);
                                buffer.clear();
                                sc.read(buffer);
                                buffer.flip();

                                //read data from buffer
                                if (buffer.limit() != 0) {
                                    String data = new String(buffer.array(), Charset.forName("UTF-8"));
                                    System.out.println("Received message: <" + data + ">");
                                    Connection cur = getConnection(sc.socket());
                                    if (!cur.isActive()) { //handshake; this condition needs to be changed to recognize individual clients
                                        if (cur.getState()==Connection.HANDSHAKE_INCOMPLETE) {
                                            String R = WebManager.webSocketHandshakeRequest(data);
                                            if (!R.equals(WebManager.BAD_REQUEST)) {
                                                messageToClient(cur, R);
                                                cur.setState(Connection.ACTIVE);
                                            }
                                        } else {
                                            dropConnection(cur); //drop connections that dont do websocket
                                        }
                                    } else { //receiving messages from client after handshake
                                        byte[] msgBytes = new byte[buffer.remaining()];
                                        buffer.get(msgBytes); //copy bytes from buffer to byte array
                                        System.out.println("Received web message: <" + (new String(WebManager.decodeFrame(msgBytes))) + ">");

                                        String send = "testing"; //testing a reply
                                        System.out.println("Replying: "+send);
                                        messageToClient(sc.socket(), WebManager.encodeFrame(send.getBytes()));
                                        //killServer = true; //remove this once connections are kept track of
                                    }

                                }
                                // remove dead connections from selector and close
                                if (buffer.limit() == 0) {
                                    key.cancel();
                                    Socket s = null;
                                    try {
                                        s = sc.socket();
                                        s.close();
                                    } catch (IOException ie) {
                                        System.err.println("Error closing socket " + s + ": " + ie);
                                    }
                                }

                            } catch (IOException ie) {
                                System.out.println(ie);
                                System.out.println("Closed " + sc);
                            }
                        }
                    }
                    keys.clear();
                }
            }
            //end of server loop; shut down stuff

            System.out.println("Shutting down server...");
            socketChannel.close();

        } catch (IOException ex) {
            System.out.println("Server has encountered IOException.");
        }

    }

    //Client messaging methods

    /* messageToClient(s,<String> mes):
        send message to a client thru their socket connection in form of string
     */
    private void messageToClient(Socket s, String mes) throws IOException {
        DataOutputStream outStream = new DataOutputStream(s.getChannel().socket().getOutputStream());

        ByteBuffer bytebuf = ByteBuffer.wrap(mes.getBytes());
        try {
            s.getChannel().write(bytebuf);
        } catch (IOException ex) {
            System.out.println("Error sending message");
        }
    }
    /* messageToClient(s,<byte[]> mes):
        send message to a client thru their socket connection in form of byte array
     */
    private void messageToClient(Socket s, byte[] mes) throws IOException {
        DataOutputStream outStream = new DataOutputStream(s.getChannel().socket().getOutputStream());

        ByteBuffer bytebuf = ByteBuffer.wrap(mes);
        try {
            s.getChannel().write(bytebuf);
        } catch (IOException ex) {
            System.out.println("Error sending message");
        }
    }
    /* messageToClient(c,mes):
        send message to a client thru their connection object.
        only takes string arguments.
     */
    public void messageToClient(Connection c, String msg) throws IOException {
        messageToClient(c.getSocket(),msg);
    }




    // Connection methods

    /* registerConnection(s):
        create a connection object attached to socket connection s
        should probably change id system to something based on
        hashing client ports
     */
    private void registerConnection(Socket s) throws IOException {
        System.out.println("Attempting to register connection for "+s.toString());
        Connection c = new Connection(s);
        numConnections++;
        if (nextConnectionID<MAX_CONNECTIONS) {
            connections[nextConnectionID] = c;
            c.setConnectionID(nextConnectionID++);
            c.setState(Connection.HANDSHAKE_INCOMPLETE);
        }
        else if (recycledIDs.size() > 0) { //all ids will be recycled at this point
            int next = recycledIDs.removeFirst();
            while (connections[next]!=null) {
                if (recycledIDs.size()==0) {
                    System.out.println("The server is full. Connection rejected.");
                    dropConnection(c);
                    return;
                }
                else
                    next = recycledIDs.removeFirst();
            }
            connections[next]=c;
            c.setConnectionID(next);
            c.setState(Connection.HANDSHAKE_INCOMPLETE);
        }
        else {
            System.out.println("The server is full. Connection rejected.");
            dropConnection(c);
            return;
        }
    }

    /* disconnect(s):
        disconnect a tcp socket connection.
        most basic layer of ending a connection.
     */
    public void disconnect(Socket s) throws IOException {
        try {
            System.out.println("Disconnecting "+s.toString());
            s.close();
        } catch (NullPointerException e) {
            System.out.println("Error disconnecting client "+s.toString());
        }
    }

    /* dropConnection(c):
        unregister this connection and disconnect its socket
        also do user-related stuff when it exists
     */
    private void dropConnection(Connection c) throws IOException {
        int id = c.getConnectionID();
        disconnect(c.getSocket());
        connections[id]=null;
        recycledIDs.addLast(id); //reuse their connection id so we dont run out of space
    }

    /* getConnection(s):
        returns the connection object associated with a socket.
        based on port socket is connected to
     */
    private Connection getConnection(Socket s) {
        for (Connection c: connections)
            if (c.getPort()==s.getPort())
                return c;

        return null;
    }

    public void printConnectionsInfo() {
        int count[]=new int[3];
        int manCount=0;
        int nullCount=0;

        for (Connection c: connections) {
            if (c==null) {
                nullCount++;
                continue;
            }
            System.out.println(c.toString());
            count[c.getState()+1]++;
            manCount++;
        }
        System.out.println("Connection Statistics:");
        System.out.println("\tConnections awaiting registration: "+count[0]);
        System.out.println("\tConnections awaiting handshake: "+count[1]);
        System.out.println("\tConnections active: "+count[2]);
        System.out.println("\tTotal connections (counted manually): "+manCount);
        System.out.println("\tTotal null connections: "+nullCount);
        System.out.println("\tTotal connections (stored in server): "+numConnections);

    }








}