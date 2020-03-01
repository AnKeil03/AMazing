package com.seProject.groupProject7.login;

import com.seProject.groupProject7.DatabaseLoader;
import com.seProject.groupProject7.User;
import com.seProject.groupProject7.UserRepository;
import com.seProject.groupProject7.requests.ServerRequest;
import org.hibernate.dialect.Database;

import java.util.Iterator;

public class LoginManager {
    private UserRepository repository;

    public LoginManager(UserRepository rep) {
        repository=rep;
    }

    public User getUser(User U) {
        Iterable<User> usrs = this.repository.findAll();
        Iterator<User> it = usrs.iterator();
        while (it.hasNext()) {
            User cur = it.next();
            if (cur.getName().equals(U.getName())) {
                //user already exists
                return cur;
            }
        }
        return null;
    }

    public void handleLoginRequest(String user, String pass) {
        User check = new User(user,pass,"empty@noemail.com");
        loginCheck(check);
    }

    public void createNewUser(String user, String pass, String email) {
        System.out.println("Attempting to create and store new user: "+user);
        User U = new User(user,pass,email);
        if (getUser(U)==null) {
            System.out.println("Create user failed; user "+user+" already exists.");
            return;
        }
        this.repository.save(U);
        System.out.println("Saved new user successfully: "+U.toString());
    }

    public void loginCheck(User check) {
        User compareWith = getUser(check);
        if (compareWith==null) { //new user, create
            //createNewUser(check.getName(),check.getPassword(),check.getEmail());
            System.out.println("Failed login for user "+check.toString()+": user does not exist.");
        } else {
            if (check.getPassword().equals(compareWith.getPassword())) { //login details match
                System.out.println("Successful login for user "+compareWith.toString());
            } else {
                System.out.println("Invalid password for user "+compareWith.getName()+"; expected password = "+compareWith.getPassword());
            }
        }
    }

    public void addLoginRequest(String user, String encryptedPass) {
        String pass = Cryptography.cipher(encryptedPass);
        ServerRequest R = new ServerRequest(ServerRequest.LOGIN_REQUEST,user,pass);
        DatabaseLoader.serverRequests.requests.add(R);
        System.out.println("[Server] Received login request... adding to queue.");
        //run();
    }
}
