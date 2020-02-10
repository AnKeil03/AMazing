package drms;

import drms.server.Server;
import drms.server.DatabaseManager;
import java.sql.*;

public class Main {

    public static Server server;

    public static void main(String[] args) throws SQLException {

        DatabaseManager m = new DatabaseManager();

        server = new Server();
        new Thread(server).start();
    }

}
