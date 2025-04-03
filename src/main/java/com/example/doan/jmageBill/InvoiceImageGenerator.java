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
        int width = 600;
        int height = 450 + bill.getBillItems().size() * 40;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.BLACK);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.drawString("SHOP THỜI TRANG UNETIFASHION", 150, 40);
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        g2d.drawString("Địa chỉ: HÀ NỘI", 150, 60);
        g2d.drawString("Hotline: 0123 456 789", 150, 80);
        g2d.drawLine(50, 90, 550, 90);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.drawString("HÓA ĐƠN THANH TOÁN", 200, 120);
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        g2d.drawString("Mã hóa đơn: " + bill.getId(), 50, 150);
        g2d.drawString("Ngày lập: " + sdf.format(new Date()), 350, 150);
        g2d.drawString("Khách hàng: " + bill.getUserEntity().getFullName(), 50, 170);
        g2d.drawString("Phương thức thanh toán: " + bill.getPaymentMethod(), 50, 190);
        
        int tableStartY = 220;
        g2d.drawLine(50, tableStartY, 550, tableStartY);
        g2d.drawString("Sản phẩm", 60, tableStartY + 20);
        g2d.drawString("Đơn giá", 250, tableStartY + 20);
        g2d.drawString("Số lượng", 370, tableStartY + 20);
        g2d.drawString("Thành tiền", 470, tableStartY + 20);
        g2d.drawLine(50, tableStartY + 30, 550, tableStartY + 30);
        
        int y = tableStartY + 50;
        for (BillItem item : bill.getBillItems()) {
            String productName = item.getProductVariant().getProduct().getProductName();
            int maxWidth = 180; // Chiều rộng tối đa của cột tên sản phẩm
            FontMetrics metrics = g2d.getFontMetrics();

            // Xử lý cắt dòng cho tên sản phẩm
            int lineHeight = metrics.getHeight();
            int lineY = y;
            String[] words = productName.split(" ");
            StringBuilder line = new StringBuilder();

            for (String word : words) {
                if (metrics.stringWidth(line + word) > maxWidth) {
                    g2d.drawString(line.toString(), 60, lineY);
                    line = new StringBuilder(word + " ");
                    lineY += lineHeight;
                } else {
                    line.append(word).append(" ");
                }
            }
            g2d.drawString(line.toString(), 60, lineY); // Vẽ dòng cuối cùng
            y = Math.max(y, lineY) + 30; // Cập nhật vị trí Y cho dòng tiếp theo

            // Vẽ các cột khác
            g2d.drawString(String.format("%,.0f VND", item.getProductVariant().getPrice()), 250, y - 30);
            g2d.drawString(String.valueOf(item.getQuantity()), 390, y - 30);
            g2d.drawString(String.format("%,.0f VND", item.getTotalPrice()), 470, y - 30);
        }
 
        g2d.drawLine(50, y, 550, y);
        y += 20;
        
        g2d.drawString("Phí vận chuyển: " + String.format("%,.0f VND", bill.getShippingFee()), 50, y);
        y += 30;
        g2d.setFont(new Font("Arial", Font.BOLD, 16));

        double finalTotal = bill.getTotalAmount() + bill.getShippingFee();
        g2d.drawString("TỔNG TIỀN: " + String.format("%,.0f VND", finalTotal), 50, y);
                
        g2d.dispose();
        File file = new File("invoice_" + bill.getId() + ".png");
        ImageIO.write(image, "png", file);
        return file;
    }
}