package com.seProject.groupProject7.requests;

import com.seProject.groupProject7.DatabaseLoader;

public class ServerRequest {

    public static final int LOGIN_REQUEST = 100;

    private int requestID;
    private String[] contents;

    public ServerRequest(int id, String... args) {
        requestID=id;
        contents=args;
    }


    public void process() {
        System.out.println("PROCESSING");
        switch (requestID) {
            case LOGIN_REQUEST:
                System.out.println("Processing login request...");
                DatabaseLoader.loginManager.handleLoginRequest(contents[0],contents[1]);
                break;
        }
    }
}
