package com.seProject.groupProject7;

import java.lang.reflect.Executable;
import java.util.Iterator;
import java.util.List;
import com.seProject.groupProject7.login.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.ast.NullLiteral;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/web")
public class WebController {

    @Autowired
    private UserController userController;

    @RequestMapping(value = "/myinfo")
    public String index(@CookieValue(value = "sessionID", defaultValue = "-1") String cval) {
        System.out.println("access from "+cval);
        User U = userController.getUserForID(Integer.parseInt(cval));
        if (U!=null)
            return "Your user info:\nName:"+U.getName()+"\nEmail:\n"+U.getEmail();
        else
            return "please login";
    }


}
