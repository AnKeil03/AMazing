package drms;

import drms.server.Server;
import drms.server.DatabaseManager;
import drms.server.command.*;
import drms.server.api.*;
import java.sql.*;
import java.util.Scanner;

public class Main {

    public static Server server;
    public static Server RESTserver;

    public static void main(String[] args) throws SQLException {

        DatabaseManager m = null;

        Scanner s = new Scanner(System.in);
        System.out.print("MySQL server currently running? (Y/N): ");
        if (s.nextLine().equals("Y"))
            m=new DatabaseManager();

        //DatabaseManager m = new DatabaseManager();
        CommandHandler.initCommands();

        API useAPI = null;
        if (args.length==0)
            useAPI = new WebSocket();
        else {
            if (args[0].equalsIgnoreCase("rest"))
                useAPI = new REST();
        }

        server = new Server(useAPI);
        new Thread(server).start();


       /* RESTserver = new Server(8080,API.REST);
        new Thread(RESTserver.start());
        */
    }

}
