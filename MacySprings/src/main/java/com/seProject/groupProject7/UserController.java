package com.seProject.groupProject7;

import java.lang.reflect.Executable;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import com.seProject.groupProject7.login.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.ast.NullLiteral;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private UserRepository userRepository;
    @Autowired
    private ServerController serverController;

    @GetMapping(path="/add") // Map ONLY POST Requests
    public @ResponseBody
    String addNewUser (@RequestParam String user, @RequestParam String pass, @RequestParam String email) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request
        User n = new User(user,Cryptography.cipher(Cryptography.unpad(pass)),email);
        //User n2 = new User(name,passw,email);
       // n.setName(name);
       // n.setEmail(email);
        try {
            if (getUser(user)!=null) {
                return "registeralreadyexists";
            } else {
                userRepository.save(n);
            }
        } catch (Exception e) {
            return "registerfail";
        }
        return "registersuccess";
    }


    @GetMapping(path="/get")
    public @ResponseBody
    User getUser(@RequestParam String name) {
        // fetch the user from the database
        Iterator<User> users = userRepository.findAll().iterator();

        while (users.hasNext()) {
            User user = users.next();
            if (user==null)
                continue;
            if (user.getName().compareTo(name) == 0) {
                //User response = new User(user.getName(), user.getPassword(), user.getEmail());
                return user;
            }
        }

        // We didn't the user, return a error response
        return null;
    }

    public User getUserForID(int ses) {
        // fetch the user from the database
        Iterator<User> users = userRepository.findAll().iterator();

        while (users.hasNext()) {
            User user = users.next();
            if (user.getUserSessionID() == ses) {
                return user;
            }
        }

        // We didn't the user, return a error response
        return null;
    }

    @GetMapping("/all")
    public List<User> retrieveAllStudents() {
        return (List<User>) userRepository.findAll();
    }

    @GetMapping("/checkLogin")
    public @ResponseBody
    String handleLoginRequest(@CookieValue(value = "sessionID", defaultValue = "-1") String cval, @RequestParam String user,@RequestParam String pass,HttpServletResponse response) {
        System.out.println("Processing login request... ");
        ServerSetting cfg = new ServerSetting();
        serverController.addSetting(cfg);
        ServerSetting SeshSetting = serverController.getSetting("nextSessionToken");
        if (SeshSetting!=null)
            System.out.println("Next session ID will be "+SeshSetting.getValue());

        if (Integer.parseInt(cval)==-1) {
            User check = new User(user, Cryptography.cipher(Cryptography.unpad(pass)), "empty@noemail.com");
            int checkcode = loginCheck(check);
            if (checkcode == 1) {
                User U = getUser(check.getName());
                int ss = Integer.parseInt(SeshSetting.getValue());
                U.setUserSessionID(ss);
                userRepository.save(U);
                Cookie sesh = new Cookie("sessionID", "" + ss);
                sesh.setMaxAge(-1);
                sesh.setPath("/");
                System.out.println("sending session token: " + sesh.getValue());
                SeshSetting.setValue(""+(ss+1));
                serverController.serverSettings.save(SeshSetting);
                response.addCookie(sesh);
                return "loginsuccess";
            } else if (checkcode == -1) {
                return "loginnouser";
            } else {
                return "logininvalid";
            }
        } else {
            System.out.println("Already logged in. Attempting logout...");
            User U = getUser(user);
            U.setUserSessionID(-1);
            Cookie seshBye = new Cookie("sessionID", null);
            seshBye.setMaxAge(0);
            seshBye.setPath("/");
            response.addCookie(seshBye);
            return "logout";
    }
        //Extend to return Session Token
    }




    @DeleteMapping(value = "removeUser")
    public void removeUser(@RequestParam String username){
        try{
            Iterator<User> users = userRepository.findAll().iterator();

            while (users.hasNext()) {
                User user = users.next();
                if (user.getName().compareTo(username) == 0) {
                    userRepository.delete(user);
                    System.out.println("Removing user: " + username);
                    return;
                }
            }

        }
        catch (Exception ex){
            System.out.println(ex.toString());
        }
    }

    /* loginCheck(U):

     */
    private int loginCheck(User check) {
        User compareWith = getUser(check.getName());
        if (compareWith==null) { //new user, create
            //createNewUser(check.getName(),check.getPassword(),check.getEmail());
            System.out.println("Failed login for user "+check.toString()+": user does not exist.");
            return -1;
        } else {
            if (check.getPassword().equals(compareWith.getPassword())) { //login details match
                System.out.println("Successful login for user "+compareWith.toString());
                return 1;
            } else {
                System.out.println("Invalid password for user "+compareWith.getName()+"; expected password = "+compareWith.getPassword());
                return 0;
            }
        }
    }


}
