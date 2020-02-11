/* REST.java
    api for using REST over HTTP protocol

    TODO: fill out methods

 */


package drms.server.api;

import drms.server.entity.Connection;
import java.io.IOException;

public class REST extends API {

    public REST() {
        super(API.REST);
    }

    public void receiveMessage(Connection c, String data) {

    }

    public void sendMessage(Connection c, String data) throws IOException{

    }

    public int getPort() {
        return 8080;
    }

}