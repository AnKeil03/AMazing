package com.seProject.groupProject7.mail;

import com.seProject.groupProject7.EmailController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import javax.mail.MessagingException;

import java.io.IOException;
import java.util.Properties;

@SpringBootTest
@AutoConfigureMockMvc
public class EmailTest {
    @Autowired
    private EmailController emailController;


    @Test
    public void testEmail() throws MessagingException, IOException {
        SimpleMessageListenerImpl receiver = new SimpleMessageListenerImpl();
        // for POP3
        String protocol = "pop3";
        String host = "pop.gmail.com";
        String port = "995";

//         for IMAP
//        String protocol = "imap";
//        String host = "imap.gmail.com";
//        String port = "993";


        String userName = "jammybammy404@gmail.com";
        String password = "*jmoney404";

        emailController.sendEmail("jammybammy404@gmail.com", "Test", "Test email");
        try {
            Thread.sleep(5000);
            assert(receiver.testEmailFind(protocol, host, port, userName, password));
        }
        catch (Exception ex){
            System.out.println(ex.toString());
        }

    }


}
