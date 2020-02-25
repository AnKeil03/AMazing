package com.seProject.groupProject7;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private UserRepository userRepository;

    @PostMapping(path="/add") // Map ONLY POST Requests
    public @ResponseBody
    String addNewUser (@RequestParam String name, @RequestParam String passw, @RequestParam String email) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request

        User n = new User(name,passw,email);
       // n.setName(name);
       // n.setEmail(email);
        try {
            userRepository.save(n);
        } catch (Exception e) {
            return "Failed to add user";
        }
        return "Successfully added user";
    }

    @GetMapping(path="/get")
    public @ResponseBody
    UserRest getUser(@RequestParam String name) {
        // fetch the user from the database
        Iterator<User> users = userRepository.findAll().iterator();

        while (users.hasNext()) {
            User user = users.next();
            if (user.getName().compareTo(name) == 0) {
                UserRest response = new UserRest(user.getName(), user.getPassword(), user.getEmail());
                return response;
            }
        }

        // We didn't the user, return a error response
        return new UserRest("", "", "", "User not found", -1);
    }

    @GetMapping("/all")
    public List<User> retrieveAllStudents() {
        return (List<User>) userRepository.findAll();
    }

    @RequestMapping(value = "/")
    public String index() {
        return "index";
    }


}
