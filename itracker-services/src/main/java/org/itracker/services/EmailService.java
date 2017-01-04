package org.itracker.services;

import javax.mail.internet.InternetAddress;

/**
 * The Email service.
 */
public interface EmailService {

    void sendEmail(String toAddress, String subject, String msgText);

    void sendEmail(InternetAddress toAddress, String subject, String msgText);

    void sendEmail(InternetAddress[] recipients, String subject, String msgText);
}
