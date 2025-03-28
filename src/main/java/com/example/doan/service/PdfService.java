package com.example.doan.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;

import com.example.doan.entity.Bill;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;

import java.io.InputStream;

@Service
public class PdfService {

    private final InvoicePdfService invoicePdfService;

    public PdfService(InvoicePdfService invoicePdfService) {
        this.invoicePdfService = invoicePdfService;
    }

    public byte[] generateInvoiceImage(Bill bill) {
        try {
            // 1. Tạo PDF từ hóa đơn
            byte[] pdfBytes = invoicePdfService.generateInvoice(bill);

            // 2. Chuyển byte[] thành PDDocument
            ByteArrayInputStream pdfInputStream = new ByteArrayInputStream(pdfBytes);
            PDDocument document = PDDocument.load(pdfInputStream);

            // 3. Render trang đầu tiên của PDF thành ảnh
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            BufferedImage image = pdfRenderer.renderImageWithDPI(0, 300); // DPI 300 cho ảnh sắc nét
            document.close();

            // 4. Chuyển ảnh thành byte[]
            ByteArrayOutputStream imageOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", imageOutputStream);

            return imageOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public PdfFont loadFont() {
        try {
            InputStream fontStream = getClass().getClassLoader().getResourceAsStream("fonts/Roboto-Italic-VariableFont_wdth,wght.ttf");
            if (fontStream == null) {
                throw new RuntimeException("Không tìm thấy font Roboto-Italic");
            }

            return PdfFontFactory.createFont(fontStream.readAllBytes(), "Identity-H", true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi load font", e);
        }
    }
}
