package drms.server.entity;

import java.net.Socket;

public class Connection {

    private Socket socket; //socket associated with connection for data sending
    private int connectionID; //set to -1 until server assigns an id
    private int port;
    private int state;

    public static final int AWAITING_REGISTRATION = -1;
    public static final int HANDSHAKE_INCOMPLETE = 0;
    public static final int ACTIVE = 1;

    public Connection(Socket s) {
        socket=s;
        connectionID=-1;
        port = s.getPort();
        state=AWAITING_REGISTRATION;
    }

    public void setConnectionID(int id) {connectionID=id;}
    public void setState(int s) {state=s;}
    public Socket getSocket() {return socket;}
    public int getConnectionID() {return connectionID;}
    public boolean isActive(){return (state==ACTIVE);}
    public int getPort(){return port;}
    public int getState(){return state;}

}