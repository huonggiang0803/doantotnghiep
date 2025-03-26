package com.example.doan.service;

import java.nio.charset.StandardCharsets;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
     private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }
    public void sendEmail(String to, String subject, String body) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true); 
        javaMailSender.send(message);
    }
    public void sendInvoiceEmail(String to, String subject, String body, byte[] pdfData) throws MessagingException {
        if (pdfData == null || pdfData.length == 0) {
            throw new MessagingException(" Lỗi: File PDF rỗng, không thể gửi email!");
        }

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);
        helper.addAttachment("HoaDon.pdf", new ByteArrayResource(pdfData));

        javaMailSender.send(message);
        System.out.println("✅ Email hóa đơn đã gửi thành công tới: " + to);
    }
}
