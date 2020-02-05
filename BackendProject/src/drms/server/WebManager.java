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
                System.out.println("get.find()");
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
        String[] lines = msg.split(System.getProperty("line.separator"));
        String keyResponse = "";
        boolean flag[] = {false,false,false};
        for (int i=0; i<lines.length; i++) {
            if (i==0) { //first line GET /ws HTTP/1.1
                String[] tokens = lines[i].split(" ");
                if (tokens.length < 3)
                    return BAD_REQUEST;
                if (!tokens[0].equals("GET"))
                    return BAD_REQUEST;
                if (!tokens[2].startsWith("HTTP/")) //may want a more specific case here
                    return BAD_REQUEST;
            }
            else {
                int j=0;
                for (int k=0; k<lines[i].length(); k++) {
                    if (lines[i].charAt(k)==':') {
                        j = k;
                        break;
                    }
                }
                if (j==0 && i!=0)
                    break;
                String header = lines[i].substring(0,j);
                String data = lines[i].substring(j+2,lines[i].length()-1);
                //two important headers for websocket handshake
                if (header.equals("Upgrade")) {
                    if (!data.equals("websocket"))
                        return BAD_REQUEST;
                    flag[0]=true;
                }
                if (header.equals("Connection")) {
                    if (!data.equals("Upgrade"))
                        return BAD_REQUEST;
                    flag[1]=true;
                }
                if (header.equals("Sec-WebSocket-Key")) {
                    flag[2]=true;

                    keyResponse = keyResponse(data);
                }
            }
        }
        if (flag[0]&&flag[1]&&flag[2]) {
            return keyResponse;
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


}
