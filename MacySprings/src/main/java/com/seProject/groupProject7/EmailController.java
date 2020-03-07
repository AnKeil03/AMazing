package com.seProject.groupProject7;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.mail.internet.*;
import javax.mail.*;
import java.io.IOException;
import java.util.Properties;
import java.util.Date;

@RestController
@RequestMapping("/mail")
public class EmailController {
    @RequestMapping(value = "/sendemail")
    public String sendEmail() {
        try {
            sendmail();
            return "Email sent successfully";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "failure to send email";
    }
    private void sendmail() throws AddressException, MessagingException, IOException {
        System.out.println("trying to send an email...");
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        String fromAddress = "seproject7mailer@gmail.com";
        String fromPass = "seproject";
        String toAddress1 = "kevin-steele@uiowa.edu";
        String toAddress2 = "macy-schmidt@uiowa.edu";
        String toAddress3 = "kane-templeton@uiowa.edu";

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromAddress, fromPass);
            }
        });
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(fromAddress, false));

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress1+","+toAddress2+","+toAddress3));
        msg.setSubject("Testing spring email");
        msg.setContent(" ", "text/html");
        msg.setSentDate(new Date());

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent("sent this to you guys from our backend :)", "text/html");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        MimeBodyPart attachPart = new MimeBodyPart();

        //attachPart.attachFile("/var/tmp/image19.png");
        //multipart.addBodyPart(attachPart);
        msg.setContent(multipart);
        Transport.send(msg);
    }
}