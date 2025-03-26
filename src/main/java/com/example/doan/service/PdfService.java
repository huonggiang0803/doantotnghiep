package com.example.doan.service;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.example.doan.entity.Bill;
import com.example.doan.entity.BillItem;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;

@Service
public class PdfService {

    public byte[] generateInvoice(Bill bill) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            PdfFont font = loadFont();

            Paragraph title = new Paragraph("HÓA ĐƠN MUA HÀNG")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setFontSize(18)
                    .setFont(font);
            document.add(title);
            document.add(new Paragraph("------------------------------------------------").setFont(font));

            document.add(new Paragraph("Mã đơn hàng: " + bill.getId()).setFont(font));
            document.add(new Paragraph("Khách hàng: " + bill.getUserEntity().getFullName()).setFont(font));
            document.add(new Paragraph("Địa chỉ: " + bill.getShipping().getAddress()).setFont(font));
            
            document.add(new Paragraph("Phương thức thanh toán: " + bill.getPaymentMethod()).setFont(font));
            document.add(new Paragraph("-------------------------------------------------------------------").setFont(font));

            Table table = new Table(5);
            table.addCell(new Cell().add(new Paragraph("Sản phẩm").setBold().setFont(font)));
            table.addCell(new Cell().add(new Paragraph("SL").setBold().setFont(font)));
            table.addCell(new Cell().add(new Paragraph("Giá").setBold().setFont(font)));
            table.addCell(new Cell().add(new Paragraph("Phí ship").setBold().setFont(font)));
            table.addCell(new Cell().add(new Paragraph("Thành tiền sản phẩm").setBold().setFont(font)));

            for (BillItem item : bill.getBillItems()) {
                String productName = item.getProductVariant().getProduct().getProductName();
                table.addCell(new Cell().add(new Paragraph(productName).setFont(font)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantity())).setFont(font)));
                table.addCell(new Cell().add(new Paragraph(String.format("%,.0f VND", item.getPrice())).setFont(font)));
                table.addCell(new Cell().add(new Paragraph(String.format("%,.0f VND", bill.getShippingFee())).setFont(font)));
                table.addCell(new Cell().add(new Paragraph(String.format("%,.0f VND", item.getTotalPrice())).setFont(font)));

            }
            document.add(table);

            document.add(new Paragraph("--------------------------------------------------------------").setFont(font));
            document.add(new Paragraph("Tổng cộng: " + String.format("%,.0f VND", bill.getTotalAmount()))
                    .setBold()
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFont(font));

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public PdfFont loadFont() {
        try {
            InputStream fontStream = getClass().getClassLoader().getResourceAsStream("fonts/Roboto-Italic-VariableFont_wdth,wght.ttf");
            if (fontStream == null) {
                throw new RuntimeException("Không tìm thấy font arialuni.ttf");
            }
    
            return PdfFontFactory.createFont(fontStream.readAllBytes(), "Identity-H", true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi load font", e);
        }
    }
    
}
