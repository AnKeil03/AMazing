package drms.server.api;

import drms.server.entity.Connection;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import drms.Main;

public class HTTP extends API {

    public HTTP() {
        super();
    }

    /* processMessage(c,data):

    */
    @Override
    public void processMessage(Connection c, String data) throws IOException {
        processHTTPRequest(c,data);
    }

    protected void processHTTPRequest(Connection c, String data) throws IOException {
        String[] lines = data.split("\r\n");
        if (lines.length<1) {
            sendBadRequest(c);
            return;
        }
        String[] words = lines[0].split(" ");
        if (words.length<3) {
            sendBadRequest(c);
            return;
        }
        String response = "";
        switch (words[0]) {
            case "GET":
                response = response_GET(words[1],words[2]);
                break;
            case "POST":
                break;
            case "PUT":
                break;
            case "PATCH":
                break;
            case "DELETE":
                break;
            default:
                response = HTTP_BAD_REQUEST+""+fileData(BAD_REQUEST_PATH);
                break;
        }

        sendMessage(c,response);
        Main.server.dropConnection(c);
    }

    protected String response_GET(String uri, String version) {

        if (uri.equals("/"))
            return HTTP_OK+""+fileData(INDEX_PATH);
        else {
            String path = "tests/"+uri;
            File f = new File(path);
            if (f.exists())
                return HTTP_OK+""+ fileData(path);
            else
                return HTTP_NOT_FOUND+""+fileData(NOT_FOUND_PATH);

        }

    }

    protected void sendBadRequest(Connection c) throws IOException {
        sendMessage(c,HTTP_BAD_REQUEST+""+fileData(BAD_REQUEST_PATH),false);
    }

    protected String fileData(String path)  {
        //String str = FileUtils.readFileToString(file);
        try {
            String str = Files.readString(Paths.get(path));
            return str;
        } catch (IOException e) {
            System.out.println("Error finding path "+path);
        }
        return "";
    }

    /*def http_response(obj):
            if obj == None: # 400 bad request
        return "HTTP/1.1 400 BAD REQUEST\r\nContent-Type: text/html\r\n\r\n"+filetostring("codes/400.html")
    if obj=="/": #default to index.html
        return "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n"+filetostring("index.html")
    if path.exists(obj[1:]): # 200 ok
        if obj.endswith(".png"):
                return "HTTP/1.1 200 OK\r\nContent-Type: image/png\r\n\r\n"+filetostring(obj[1:])
        if obj.endswith(".jpg"):
                return "HTTP/1.1 200 OK\r\nContent-Type: image/jpeg\r\n\r\n"+filetostring(obj[1:])
        if obj.endswith(".gif"):
                return "HTTP/1.1 200 OK\r\nContent-Type: image/gif\r\n\r\n"+filetostring(obj[1:])
        return "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n"+filetostring(obj[1:])
    #404 not found
    return "HTTP/1.1 404 Not Found\r\nContent-Type: text/html\r\n\r\n"+filetostring("codes/404.html")*/



    public int getPort() {
        return 80;
    }
    public int getID() {
        return API.HTTP;
    }
    public String getName() {
        return "HTTP";
    }

    private static final String INDEX_PATH = "tests/index.html";
    private static final String BAD_REQUEST_PATH = "tests/pages/400.html";
    private static final String NOT_FOUND_PATH = "tests/pages/404.html";

    public static final String HTTP_OK = "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n";
    public static final String HTTP_BAD_REQUEST = "HTTP/1.1 400 BAD REQUEST\r\nContent-Type: text/html\r\n\r\n";
    public static final String HTTP_NOT_FOUND = "HTTP/1.1 404 Not Found\r\nContent-Type: text/html\r\n\r\n";

}