package drms.server.api;

import drms.server.entity.Connection;
import drms.server.Server;
import java.io.IOException;
import drms.Main;

public abstract class API {

    // API-specific methods:


    /* processMessage(c,data):
        HANDLES LOGIC:
        performs processing logic on the decoded plaintext from an incoming message.
        most application functionality to be performed on top of the protocol in use
        should be written inside of this method.
        must be overridden in API subclass to provide implementation.
     */
    public abstract void processMessage(Connection c, String data) throws IOException;

    /* decodeMessage(c,data):
        returns packet data converted into plaintext string, or null if data is in improper format.
        each protocol may have its own way of encoding messages.
        this method is defined per API to decode packets according to protocol.
        if no decoding is necessary, do not override this method in API subclass
     */
    protected String decodeMessage(Connection c, String data) {return data;}

    /* encodeMessage(c,data):
        encodes data into proper format to be sent to clients by the current protocol.
        if no encoding is necessary, do not override this method in API subclass
     */
    protected String encodeMessage(Connection c, String data) {return data;}



    //API properties
    public abstract int getPort();
    public abstract int getID();
    public abstract String getName();



    //NON-ABSTRACT METHODS: generic to all APIs. server integration

    /* processPacket(c,data):
        method called by server on any API when receiving data thru TCP socket.
        attempts to retrieve plaintext message in case packet is encoded,
        then processes the plaintext message according to specific API
     */
    public void processPacket(Connection c, String data) throws IOException {
        String send = decodeMessage(c,data);
        processMessage(c,send);
    }

    /* sendMessage(c,data,encode):
        method called by server on any API to send data thru TCP socket.
        if encode flag is set to true,
            attempts to encode message according to specific API,
            then sends encoded message across TCP connection.
        else sends raw data.
        if encode flag is ommitted, it will set to true by default
     */
    public void sendMessage(Connection c, String data, boolean encode) throws IOException {
        if (encode) {
            String send = encodeMessage(c, data);
            Main.server.messageToClient(c, send);
            System.out.println("sent <"+send+">");
        }
        else {
            Main.server.messageToClient(c, data);
            System.out.println("sent <"+data+">");
        }
    }
    public void sendMessage(Connection c, String data) throws IOException {
        sendMessage(c,data,true);
    }

    /* send bytes directly

     */
    public void sendBytes(Connection c, byte[] bytes) throws IOException {
        Main.server.messageToClient(c.getSocket(),bytes);
    }

    public static final int TCP = 0;
    public static final int WEBSOCKET = 1;
    public static final int HTTP = 3;
    public static final int REST = 2;


}