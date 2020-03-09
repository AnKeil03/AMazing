package com.seProject.groupProject7;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import javax.mail.internet.*;
import javax.mail.*;
import java.io.IOException;
import java.util.Properties;
import java.util.Date;

@RestController
@RequestMapping("/mail")
public class EmailController {
    private String fromAddress = "seproject7mailer@gmail.com";
    private String fromPass = "seproject";

    @RequestMapping(value = "/sendemail")
    @ResponseStatus(HttpStatus.CREATED)
    public String sendEmail(@RequestParam String email,@RequestParam String header,@RequestParam String body) {
        try {
            sendmail(email, header, body);
            return "Email sent successfully";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "failure to send email";
    }


    private void sendmail(String email, String header, String body) throws AddressException, MessagingException, IOException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        System.out.println("trying to send an email...");

        //String toAddress1 = "kevin-steele@uiowa.edu";
        //String toAddress2 = "macy-schmidt@uiowa.edu";
        //String toAddress3 = "kane-templeton@uiowa.edu";

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromAddress, fromPass);
            }
        });
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(fromAddress, false));

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
        msg.setSubject(header);
        msg.setContent(" ", "text/html");
        msg.setSentDate(new Date());

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(body, "text/html");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        MimeBodyPart attachPart = new MimeBodyPart();

        //attachPart.attachFile("/var/tmp/image19.png");
        //multipart.addBodyPart(attachPart);
        msg.setContent(multipart);
        Transport.send(msg);

    }
}