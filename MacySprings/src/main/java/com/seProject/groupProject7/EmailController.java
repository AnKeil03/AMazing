//from https://www.tutorialspoint.com/spring_boot/spring_boot_sending_email.htm

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
            String[] toAddr = {email};
            send_email(toAddr,header,body,null);
            return "Email sent successfully";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "failure to send email";
    }


    private void send_email(String[] toAddr, String subject, String bodyText, String fileAtchmt) throws AddressException, MessagingException, IOException {
        System.out.println("trying to send an email...");
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        String fromAddress = "seproject7mailer@gmail.com";
        String fromPass = "seproject";
        String to = ""+toAddr[0];
        for (int i=1; i<toAddr.length;i++)
            to+=","+toAddr[i];

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromAddress, fromPass);
            }
        });
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(fromAddress, false));


        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        msg.setSubject(subject);
        msg.setContent(" ", "text/html");
        msg.setSentDate(new Date());

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(bodyText, "text/html");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        MimeBodyPart attachPart = new MimeBodyPart();

        if (fileAtchmt!=null) {
            attachPart.attachFile(fileAtchmt);
            multipart.addBodyPart(attachPart);
        }

        msg.setContent(multipart);
        Transport.send(msg);

    }
}