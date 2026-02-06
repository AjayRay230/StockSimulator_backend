package org.ajay.stockSimulator.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final SendGrid sendGrid;
    private final String fromEmail;
    private final String fromName;

    public EmailService(
            @Value("${sendgrid.api.key}") String apiKey,
            @Value("${app.mail.from}") String fromEmail,
            @Value("${app.mail.from-name}") String fromName
    ) {
        this.sendGrid = new SendGrid(apiKey);
        this.fromEmail = fromEmail;
        this.fromName = fromName;
    }

    public void sendResetLink(String email, String link) {

        Email from = new Email(fromEmail, fromName);
        Email to = new Email(email);

        String subject = "Password Reset";

        Content content = new Content(
                "text/html",
                """
                <p>Click the link below to reset your password:</p>
                <p><a href="%s">Reset Password</a></p>
                <p>This link expires in <b>15 minutes</b>.</p>
                """.formatted(link)
        );

        Mail mail = new Mail(from, subject, to, content);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);

            if (response.getStatusCode() >= 400) {
                throw new RuntimeException("SendGrid error: " + response.getBody());
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
