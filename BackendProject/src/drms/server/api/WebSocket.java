/* WebSocket.java
    api for WebSocket protocol
 */

package drms.server.api;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;
import drms.server.Server;
import drms.server.entity.Connection;
import drms.Main;

public class WebSocket extends API {

    public WebSocket() {
        super(API.WEBSOCKET);
    }


    /*  webSocketHandShakeRequest(msg):
        returns the handshake response if msg is in format of handshake request
     */
    private String webSocketHandshakeRequest(String msg) {
        Matcher get = Pattern.compile("^GET").matcher(msg);
        try {
            if (get.find()) {
                Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(msg);
                match.find();
                byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
                        + "Connection: Upgrade\r\n"
                        + "Upgrade: websocket\r\n"
                        + "Sec-WebSocket-Accept: "
                        + Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")))
                        + "\r\n\r\n").getBytes("UTF-8");
                return (new String(response));
            }
        } catch (UnsupportedEncodingException e) {
            System.out.println("unsupported encoding");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("no algorithm");
        }

        return BAD_REQUEST;
    }



    /* decodeFrame(msg):
        returns the proper ascii bytes of a decoded message
        from a websocket client.
        found this algorithm at:
        https://stackoverflow.com/questions/18368130/how-to-parse-and-validate-a-websocket-frame-in-java/18368334
     */
    private byte[] decodeFrame(byte[] msg) {
        int maskIndex = 2;
        byte[] maskBytes = new byte[4];

        if ((msg[1] & (byte) 127) == 126) {
            maskIndex = 4;
        } else if ((msg[1] & (byte) 127) == 127) {
            maskIndex = 10;
        }

        System.arraycopy(msg, maskIndex, maskBytes, 0, 4);

        byte[] message = new byte[msg.length - maskIndex - 4];

        for (int i = maskIndex + 4; i < msg.length; i++) {
            message[i - maskIndex - 4] = (byte) (msg[i] ^ maskBytes[(i - maskIndex - 4) % 4]);
        }

        return message;
    }

    /* encodeFrame(msg):
        encodes msg into websocket bytes to send over TCP
        created from referencing RFC 6455
        https://tools.ietf.org/html/rfc6455
     */
    private byte[] encodeFrame(byte[] msg) {

        byte textFrame = (byte)0b10000001; //first 8 bits of frame
        byte maskedPayloadLen = (byte)0;
        byte unmaskedPayloadLen = (byte)0;
        byte extPayloadLen[] = new byte[8];
        byte maskingKey[] = new byte[4];
        if (msg.length<126) { //msg lengths that can be expressed in 7 bits
            maskedPayloadLen = (byte) (0b10000000 + msg.length);
            unmaskedPayloadLen = (byte) msg.length;
        }
        else if (msg.length<65535) { //msg lengths that can be expressed in 16 bits (needs work/verification)
            maskedPayloadLen = (byte)0b11111110;
            extPayloadLen[0] = (byte)((msg.length)>>>8); //right shift to get first half as one byte
            extPayloadLen[1] = (byte)(((msg.length)^(0b1111111100000000))&(msg.length)); //get second half as one byte
        }
        else { // message lengths up to a 64 bit integer. currently no actual support for this
            for (int i=0; i<8; i++)
                extPayloadLen[i] = (byte)0;
        }

        //basic unmaksed sending of a short message
        byte unmaskedShortFrame[] = new byte[2+msg.length];
        unmaskedShortFrame[0]=textFrame;
        unmaskedShortFrame[1]=unmaskedPayloadLen;
        for (int i=0; i<msg.length; i++)
            unmaskedShortFrame[i+2]=msg[i];

        return unmaskedShortFrame;
    }

    public void receiveMessage(Connection c, String data) throws IOException {
        if (!c.isActive()) { //handshake needed
            if (c.getState()==HANDSHAKE_INCOMPLETE) {
                String R = webSocketHandshakeRequest(data);
                if (!R.equals(BAD_REQUEST)) {
                    Main.server.messageToClient(c, R);
                    c.setState(ACTIVE);
                }
            } else {
                Main.server.dropConnection(c);
            }
        } else { //receiving messages from client after handshake
            byte[] msgBytes = new byte[Main.server.getBuffer().remaining()];
            Main.server.getBuffer().get(msgBytes); //copy bytes from buffer to byte array
            String recvd = new String(decodeFrame(msgBytes));
            System.out.println("Received web message: <"+recvd+">");
            //handleWebsocketMessage(c,recvd);
        }
    }

    public void sendMessage(Connection c, String data) throws IOException {
        Main.server.messageToClient(c.getSocket(), encodeFrame(data.getBytes()));
    }




    public static final String BAD_REQUEST = "HTTP/1.1 400 BAD REQUEST\r\nContent-Type: text/html\r\n\r\n";
    public static final int AWAITING_REGISTRATION = -1;
    public static final int HANDSHAKE_INCOMPLETE = 0;
    public static final int ACTIVE = 1;

    public int getPort() {
        return 80;
    }

}
