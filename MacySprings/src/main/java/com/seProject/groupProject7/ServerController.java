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
@RequestMapping("/server")
public class ServerController {


    @Autowired
    protected SettingsRepository serverSettings;

    @RequestMapping("/get")
    public ServerSetting getSetting(@RequestParam String name) {
        Iterator<ServerSetting> settings = serverSettings.findAll().iterator();

        while (settings.hasNext()) {
            ServerSetting S = settings.next();
            if (S.getName().compareTo(name) == 0) {
                return S;
            }
        }
        return null;
    }

    @RequestMapping("/add")
    public int addSetting(ServerSetting S) {
        try {
            if (getSetting(S.getName())!=null) {
                return 0; //already exists
            } else {
                serverSettings.save(S);
            }
        } catch (Exception e) {
            return -1;
        }
        return 1; //success
    }


}
