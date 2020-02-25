package com.seProject.groupProject7;

import com.seProject.groupProject7.command.CommandListener;
import com.seProject.groupProject7.login.FrontendSim;
import com.seProject.groupProject7.login.LoginManager;
import com.seProject.groupProject7.requests.RequestListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Scanner;

@Component
public class DatabaseLoader implements CommandLineRunner {

    private final UserRepository repository;

    public static LoginManager loginManager;
    public static RequestListener serverRequests;
    public static CommandListener serverCommands;

    @Autowired
    public DatabaseLoader(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... strings) throws Exception {

        System.out.println("deleting users...");
        clearUsers();
        System.out.println("initializing test users...");
        addUsers();
        System.out.println("listing all users:");
        listUsers();
        loginManager = new LoginManager(this.repository);

        loginManager.handleLoginRequest("testuser","testpassword");
        loginManager.handleLoginRequest("kane","notkanespassword");
        loginManager.handleLoginRequest("newuser","nonexistentpassword");
        loginManager.handleLoginRequest("newuser","notthenewuserspassword");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        serverCommands = new CommandListener(br);
        serverRequests = new RequestListener();


        new Thread(serverRequests).start();
        new Thread(serverCommands).start();

        System.out.println("started server requests thread... sending a request.");

        FrontendSim.enterLoginDetails("kane","kanespassword");
        FrontendSim.enterLoginDetails("kane","notkanespassword");
        FrontendSim.enterLoginDetails("newuser","newpassword");


        //serverRequests.addLoginRequest("kane","kanespassword");
        //serverRequests.addLoginRequest("kane","notkanespassword");
        //serverRequests.addLoginRequest("kane","kanespassword");


    }

    /* flush all users from database;
        probably shouldn't call this regularly
     */
    public void clearUsers() {
        this.repository.deleteAll();
    }

    public void addUsers() {
        this.repository.save(new User("testuser","testpassword","test@email.com"));
        this.repository.save(new User("kane","kanespassword","kane@email.com"));
        this.repository.save(new User("kevin","kevinspassword","kevin@email.com"));
        this.repository.save(new User("macy","macyspassword","macy@email.com"));
    }

    public void listUsers() {
        Iterable<User> usrs = this.repository.findAll();
        Iterator<User> it = usrs.iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }

    /*public void addUser(User U) {
        System.out.println("Attempting to add user "+U.toString());
        Iterable<User> usrs = this.repository.findAll();
        Iterator<User> it = usrs.iterator();
        while (it.hasNext()) {
            User cur = it.next();
            if (cur.getName().equals(U.getName())) {
                //user already exists
                System.out.println("User already exists.");
                return;
            }
        }

    }*/
}