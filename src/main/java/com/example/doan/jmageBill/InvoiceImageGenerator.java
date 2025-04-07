package com.example.doan.jmageBill;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;

import com.example.doan.entity.Bill;
import com.example.doan.entity.BillItem;

public class InvoiceImageGenerator {
    public static File generateInvoiceImage(Bill bill) throws IOException {
        int width = 700;
        int height = 450 + bill.getBillItems().size() * 50;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.BLACK);

        int logoWidth = 57;
        int logoHeight = 57;
        int logoX = 50;
        int logoY = 30;
        try {
            BufferedImage logo = ImageIO.read(new File("src/main/resources/static/images/photo.jpg"));
           
            g2d.drawImage(logo, logoX, logoY, logoWidth, logoHeight, null);
            
        } catch (IOException e) {
            System.out.println("Không tìm thấy logo.");
        }
        int textStartX = logoX + logoWidth + 20;
       g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.drawString("SHOP THỜI TRANG UNET FASHION", textStartX, 45);
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        g2d.drawString("Địa chỉ: HÀ NỘI", textStartX, 65);
        g2d.drawString("Hotline: 0123 456 789", textStartX, 85);
        g2d.drawLine(50, 100, 650, 100);

        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.drawString("HÓA ĐƠN THANH TOÁN", 250, 130);

        g2d.setFont(new Font("Arial", Font.PLAIN, 14));

        String invoiceCode = generateInvoiceCode(bill.getId());

        g2d.drawString("Mã hóa đơn: " + invoiceCode, 50, 160);
        g2d.drawString("Ngày lập: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()), 400, 160);
        g2d.drawString("Khách hàng: " + bill.getUserEntity().getFullName(), 50, 180);
        g2d.drawString("Phương thức thanh toán: " + bill.getPaymentMethod(), 50, 200);

        int tableStartY = 230;
        g2d.drawLine(50, tableStartY, 650, tableStartY);
        g2d.drawString("STT", 55, tableStartY + 20);
        g2d.drawString("Sản phẩm", 120, tableStartY + 20);
        g2d.drawString("Đơn giá", 350, tableStartY + 20);
        g2d.drawString("Số lượng", 460, tableStartY + 20);
        g2d.drawString("Thành tiền", 580, tableStartY + 20);
        g2d.drawLine(50, tableStartY + 30, 650, tableStartY + 30);

        int y = tableStartY + 50;
        int index = 1;

        for (BillItem item : bill.getBillItems()) {
            FontMetrics metrics = g2d.getFontMetrics();
            int lineHeight = metrics.getHeight();
            int lineY = y;

            // STT
            g2d.drawString(String.valueOf(index), 55, lineY);

            String productName = item.getProductVariant().getProduct().getProductName();
            int maxWidth = 160;
            String[] words = productName.split(" ");
            StringBuilder line = new StringBuilder();

            for (String word : words) {
                if (metrics.stringWidth(line + word) > maxWidth) {
                    g2d.drawString(line.toString(), 120, lineY);
                    line = new StringBuilder(word + " ");
                    lineY += lineHeight;
                } else {
                    line.append(word).append(" ");
                }
            }
            g2d.drawString(line.toString(), 120, lineY);
            y = Math.max(y, lineY) + 30;

            double price = item.getPrice();
            g2d.drawString(String.format("%,.0f VND", price), 350, y - 30);
            String quantityText = String.valueOf(item.getQuantity());
            int quantityWidth = metrics.stringWidth(quantityText);
            int quantityColumnWidth = 80; 
            int quantityX = 460 + (quantityColumnWidth - quantityWidth) / 2; 
            g2d.drawString(quantityText, quantityX, y - 30);
            g2d.drawString(String.format("%,.0f VND", item.getTotalPrice()), 580, y - 30);

            index++;
        }

        g2d.fillRect(50, y, 600, 1); 

y += 20;

g2d.setColor(Color.BLACK);
g2d.setFont(new Font("Arial", Font.PLAIN, 14));
String shippingText = "Phí vận chuyển: " + String.format("%,.0f VND", bill.getShippingFee());
int shippingTextWidth = g2d.getFontMetrics().stringWidth(shippingText);
g2d.drawString(shippingText, 600 - shippingTextWidth, y);

y += 30;

        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        double finalTotal = bill.getTotalAmount() + bill.getShippingFee();
        String totalText = "TỔNG TIỀN: " + String.format("%,.0f VND", finalTotal);
        int totalTextWidth = g2d.getFontMetrics().stringWidth(totalText);
        g2d.drawString(totalText, 600 - totalTextWidth, y);
        y += 20;
        g2d.fillRect(50, y, 600, 1);

        y += 40;
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        g2d.drawString("Cảm ơn bạn đã mua sắm tại UNET Fashion!", 50, y);
        y += 20;
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        g2d.drawString("Hãy gửi về support@unetfashion.com nếu bạn có thắc mắc gì về hóa đơn", 50, y);

        g2d.dispose();

        File file = new File("invoice_" + invoiceCode + ".png");
        ImageIO.write(image, "png", file);
        System.out.println("Invoice generated: " + file.getAbsolutePath());
        return file;
    }

    private static String generateInvoiceCode(Long billId) {
        String prefix = "UNET"; 
        String paddedId = String.format("%06d", billId); 
        return prefix + "HD" + paddedId;
    }
}
