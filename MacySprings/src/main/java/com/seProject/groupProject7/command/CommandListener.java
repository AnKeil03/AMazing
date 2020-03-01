package com.seProject.groupProject7.command;

import com.seProject.groupProject7.DatabaseLoader;
import com.seProject.groupProject7.requests.RequestListener;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.IOException;

public class CommandListener implements Runnable {

    private BufferedReader reader;
    private String build;

    public CommandListener(BufferedReader br) {
        reader=br;
        build="";
    }

    @Override
    public void run() {
        while (true) {
            listen();
            try {
                Thread.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void listen() {
        try {
            char ch = 0;
            if (reader.ready()) {
                try {
                    ch = (char) reader.read(); //read input one char at a time
                    build += ch;
                } catch (IOException e) {
                    System.out.println("Error reading from input device");
                }
            }
            if (!reader.ready() && build.length() > 0) {
                build = build.substring(0, build.length() - 1); //remove \n at end
                System.out.println("processing command: " + build);
                processCommand(build);
                build = "";

            }
        } catch (IOException e) {
            System.out.println("ioex");
        }
    }


    public static void processCommand(String com) {
        String[] args = com.split(" ");
        if (args[0].equals("login")) {
            if (args.length!=3) {
                System.out.println("Syntax error; use 'login username password'");
                return;
            }
            String usr = args[1];
            String pas = args[2];
            DatabaseLoader.loginManager.addLoginRequest(usr,pas);
        }
        else if (args[0].equalsIgnoreCase("adduser")) {
            if (args.length!=4) {
                System.out.println("Syntax error; use 'adduser username password email'");
                return;
            }
            String usr = args[1];
            String pas = args[2];
            String eml = args[3];
            DatabaseLoader.loginManager.createNewUser(usr,pas,eml);
            try {
                Thread.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
