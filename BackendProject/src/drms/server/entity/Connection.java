package drms.server.entity;

import java.net.*;
import drms.server.api.*;

public class Connection {

    private Socket socket; //socket associated with connection for data sending
    private int connectionID; //set to -1 until server assigns an id
    private int port;
    private int state;


    public Connection(Socket s) {
        socket=s;
        connectionID=-1;
        port = s.getPort();
        state=WebSocket.AWAITING_REGISTRATION;
    }



    public String toString() {
        String ip=(((InetSocketAddress) socket.getRemoteSocketAddress()).getAddress()).toString().replace(":",".");
        return "[Connection<id="+connectionID+",port="+port+">] state: "+getStateString()
                +", ip="+ip;
    }

    private String getStateString() {
        switch (state) {
            case WebSocket.AWAITING_REGISTRATION:
                return "Awaiting registration from server";
            case WebSocket.HANDSHAKE_INCOMPLETE:
                return "Awaiting WebSocket handshake completion";
            case WebSocket.ACTIVE:
                return "Ready for data transfer";
        }
        return "Invalid state";
    }

    public void setConnectionID(int id) {connectionID=id;}
    public void setState(int s) {state=s;}

    public Socket getSocket() {return socket;}
    public int getConnectionID() {return connectionID;}
    public boolean isActive(){return (state==WebSocket.ACTIVE);}
    public int getPort(){return port;}
    public int getState(){return state;}

}