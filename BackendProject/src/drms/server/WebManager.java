package drms.server;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebManager {

    public static final String BAD_REQUEST = "HTTP/1.1 400 BAD REQUEST\r\nContent-Type: text/html\r\n\r\n";

    /*
        webSocketHandShakeRequest(msg):
        returns true iff the received message is in the form of a web socket handshake request
     */
    public static String webSocketHandshakeRequest(String msg) {
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
            System.out.println("unsupported");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("algorithm");
        }

        return BAD_REQUEST;
    }

    public static String keyResponse(String key) {
        String resp = "";
        try {

            byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
                    + "Connection: Upgrade\r\n"
                    + "Upgrade: websocket\r\n"
                    + "Sec-WebSocket-Accept: "
                    + Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest((key.getBytes() + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")))
                    + "\r\nSec-WebSocket-Protocol: chat\r\n\r\n").getBytes("UTF-8");
            resp = new String(response);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return resp;
    }


    public static byte[] decodeFrame(byte[] msg) {
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




}
