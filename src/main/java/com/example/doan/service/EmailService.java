package com.example.doan.service;

import java.io.File;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.example.doan.entity.Bill;
import com.example.doan.jmageBill.InvoiceImageGenerator;

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
    public void sendInvoiceEmail(Bill bill) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(bill.getUserEntity().getEmail());
            helper.setSubject("Hóa đơn thanh toán");
            helper.setText("Xin chào, đây là hóa đơn sản phẩm của bạn!");

            // Tạo ảnh hóa đơn
            File invoiceImage = InvoiceImageGenerator.generateInvoiceImage(bill);
            FileSystemResource file = new FileSystemResource(invoiceImage);
            helper.addAttachment("invoice.png", file);

            javaMailSender.send(message);
            System.out.println("Email gửi thành công!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
