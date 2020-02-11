package drms;

import drms.server.Server;
import drms.server.DatabaseManager;
import drms.server.command.*;
import drms.server.api.*;
import java.sql.*;
import java.util.Scanner;

public class Main {

    public static Server server;

    public static void main(String[] args) throws SQLException {

        DatabaseManager m = null;

        Scanner s = new Scanner(System.in);
        System.out.println("MySQL server currently running? (Y/N)");
        System.out.print(">> ");
        if (s.nextLine().equalsIgnoreCase("Y"))
            m=new DatabaseManager();

        //DatabaseManager m = new DatabaseManager();
        CommandHandler.initCommands();

        System.out.println("Enter protocol API to use {"+API.WEBSOCKET+"=websocket, "+API.REST+"=REST, "+API.HTTP+"=HTTP}");
        System.out.print(">> ");
        int api = s.nextInt();
        API useAPI = null;
        switch (api) {
            case API.WEBSOCKET:
                useAPI = new WebSocket();
                break;
            case API.REST:
                useAPI = new REST();
                break;
            case API.HTTP:
                useAPI = new HTTP();
                break;
            default:
                System.out.println("Enter port number for TCP server");
                System.out.print(">> ");
                useAPI = new TCP(s.nextInt());
                break;

        }

        server = new Server(useAPI);
        new Thread(server).start();


       /* RESTserver = new Server(8080,API.REST);
        new Thread(RESTserver.start());
        */
    }

}
