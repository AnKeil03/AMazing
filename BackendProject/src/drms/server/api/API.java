package drms.server.api;

import drms.server.entity.Connection;
import drms.server.Server;
import java.io.IOException;

public abstract class API {

    private int id;

    public API(int id) {
        this.id=id;
    }

    public abstract void receiveMessage(Connection c, String data) throws IOException;
    public abstract void sendMessage(Connection c, String data) throws IOException;
    public abstract int getPort();

    public int getID() {return id;}

    public static final int WEBSOCKET = 1;
    public static final int REST = 2;


}