/* REST.java
    api for using REST over HTTP protocol

    TODO: fill out methods

 */


package drms.server.api;

import drms.server.entity.Connection;
import java.io.IOException;
import drms.server.entity.Connection;

public class REST extends HTTP {

    public REST() {
        super();
    }

    /* processMessage(c,data):
        process incoming packets as HTTP requests
     */
    @Override
    public void processMessage(Connection c, String data) throws IOException {
        processHTTPRequest(c,data);
    }


    @Override
    public void processHTTPRequest(Connection c, String data) {
        System.out.println("[REST] processHTTPRequest not supported yet.");
    }

    public int getPort() {
        return 8080;
    }
    public int getID() { return API.REST; }
    public String getName() {return "REST";}

}