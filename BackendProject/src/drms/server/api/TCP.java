/* TCP.java
    standard messaging protocol for TCP sockets
    default API shell if no others are chosen
 */


package drms.server.api;

import drms.Main;
import drms.server.entity.Connection;
import java.io.IOException;

public class TCP extends API {

    private int portNum;

    public TCP(int port) {
        super();
        portNum=port;
    }

    /* processMessage(c,data):
        currently functions as a simple echo server
     */
    public void processMessage(Connection c, String data) throws IOException {
        System.out.println("[TCP] message received: "+data);
        System.out.println("Echoing data back to client...");
        sendMessage(c,data);
    }


    public int getPort() { return portNum; }
    public int getID() { return API.TCP;}
    public String getName() { return "TCP";}

}