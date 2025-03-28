package com.example.doan.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.example.doan.entity.Bill;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;

@Service
public class InvoicePdfService {

    public byte[] generateInvoice(Bill bill) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("HÓA ĐƠN MUA HÀNG")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setFontSize(18));

            document.add(new Paragraph("Mã đơn hàng: " + bill.getId()));
            document.add(new Paragraph("Khách hàng: " + bill.getUserEntity().getFullName()));
            document.add(new Paragraph("Tổng tiền: " + bill.getTotalAmount() + " VND")
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBold());

            document.close();
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
