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


public class Server implements Runnable {


    public static final int PORT = 80; //HTTP Port

    private int numConnections;

    private ServerSocketChannel socketChannel;
    private ByteBuffer buffer;
    private Selector selector;


    public Server() {

        socketChannel = null;
        buffer = ByteBuffer.allocate(1024);
        selector = null;

        numConnections = 0;

    }

    /* run():
        called when this thread is started

    */

    public void run() {



        boolean killServer = false;

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


                int numConnects = selector.select(1); //accept one incoming connection at a time

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
                    System.out.println("typed: "+build);

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

                        } else if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) { //incoming data
                            SocketChannel sc = (SocketChannel)key.channel();

                            try {


                                sc = (SocketChannel)key.channel(); //clear the buffer

                                buffer.clear();
                                buffer.put(new byte[1024]);
                                buffer.clear();
                                sc.read(buffer);
                                buffer.flip();

                                //read data from buffer
                                if (buffer.limit()!=0) {
                                    String data = new String( buffer.array(), Charset.forName("UTF-8") );
                                    System.out.println("Received message: <"+data+">");
                                    String R = WebManager.webSocketHandshakeRequest(data);
                                    System.out.println("Sent reply: <"+R+">");
                                    messageToClient(sc.socket(),R);
                                   // WebManager.webSocketHandshakeRequest(data);
                                    /*System.out.println("Sending test response");
                                    messageToClient(sc.socket(),"hello i am server");*/

                                }



                                // remove dead connections from selector and close
                                if (buffer.limit()==0) {

                                    key.cancel();

                                    Socket s = null;
                                    try {
                                        s = sc.socket();
                                        s.close();
                                    } catch( IOException ie ) {
                                        System.err.println( "Error closing socket "+s+": "+ie );
                                    }
                                }

                            } catch( IOException ie ) {
                                System.out.println(ie);

                                System.out.println( "Closed "+sc );
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


    public void messageToClient(Socket c, String mes) throws IOException {
        DataOutputStream outStream = new DataOutputStream(c.getChannel().socket().getOutputStream());

        ByteBuffer bytebuf = ByteBuffer.wrap(mes.getBytes());
        try {
            c.getChannel().write(bytebuf);
        } catch (IOException ex) {
            System.out.println("error sending message");
        }
    }


    public void disconnect(Socket c) throws IOException {
        try {
            System.out.println("Disconnecting "+c.toString());
            c.close();
        } catch (NullPointerException e) {
            System.out.println("Error disconnecting client "+c.toString());
        }
    }



}