package drms;

import drms.server.Server;

public class Main {

    public static Server server;
   // public static Game game;

    public static void main(String[] args) {

        server = new Server();
        new Thread(server).start();

       /* game = new Game();
        new Thread(game).start();*/
    }

}
