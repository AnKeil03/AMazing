package com.seProject.groupProject7.requests;

import com.seProject.groupProject7.DatabaseLoader;
import com.seProject.groupProject7.login.Cryptography;

import java.util.ArrayList;

public class RequestListener implements Runnable {

    public ArrayList<ServerRequest> requests;

    private static int MAX_REQUESTS = 100;

    public RequestListener() {
        requests = new ArrayList<>();
    }

    @Override
    public void run() {
        while (true) {
            if (requests.size()>0) {
                ServerRequest R = requests.remove(0);
                R.process();
            }
            try {
                Thread.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* addLoginRequest(user,encryptedPass):
       receive a login request from front end.
       password field is encrypted so decrypt before checking
     */
    public void addLoginRequest(String user, String encryptedPass) {
        String pass = Cryptography.cipher(encryptedPass);
        ServerRequest R = new ServerRequest(ServerRequest.LOGIN_REQUEST,user,pass);
        requests.add(R);
        System.out.println("[Server] Received login request... adding to queue.");
        //run();
    }
}
