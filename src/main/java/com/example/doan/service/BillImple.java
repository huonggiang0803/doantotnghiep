package com.example.doan.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.doan.entity.Bill;
import com.example.doan.entity.BillItem;
import com.example.doan.entity.InforShipping;
import com.example.doan.entity.OrderItem;
import com.example.doan.entity.Orders;
import com.example.doan.entity.UserEntity;
import com.example.doan.repository.BillItemRepository;
import com.example.doan.repository.BillRepository;
import com.example.doan.repository.InforShipRepository;
import com.example.doan.repository.OrderRepository;

import jakarta.mail.MessagingException;

@Service
public class BillImple implements BillService {
    @Autowired
    private BillRepository billRepository;
    @Autowired
    private BillItemRepository billItemRepository;
    @Autowired
    private InforShipRepository shippingRepository;
    @Autowired
    private PdfService pdfService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private EmailService emailService;

    @Override
    @Transactional
    public Bill createBill(UserEntity user, Orders order, List<OrderItem> orderItems, String paymentMethod) {
        Bill bill = new Bill();
        bill.setOrder(order);
        bill.setUserEntity(user);
        bill.setShipping(order.getInforShipping());
        bill.setPaymentMethod(paymentMethod);
        bill.setStatus("UNPAID");
        bill.setTotalAmount(order.getTotalPrice());

        bill = billRepository.save(bill);

        for (OrderItem orderItem : orderItems) {
            BillItem billItem = new BillItem();
            billItem.setBill(bill);
            billItem.setProductVariant(orderItem.getProduct());
            billItem.setQuantity(orderItem.getQuantity());
            billItem.setPrice(orderItem.getPrice());
            bill.setShippingFee(order.getShippingFee()); 
            billItem.setTotalPrice(orderItem.getSubTotal());
            billItemRepository.save(billItem);
        }
        String emailBody = "<h3>Cảm ơn bạn đã đặt hàng!</h3>"
                + "<p>Mã đơn hàng: <b>" + bill.getId() + "</b></p>"
                + "<p>Tổng tiền: <b>" + bill.getTotalAmount() + " VND</b></p>"
                + "<p>Phương thức thanh toán: <b>" + bill.getPaymentMethod() + "</b></p>"
                + "<p>Trạng thái: <b>" + bill.getStatus() + "</b></p>"
                + "<p>Hóa đơn của bạn được đính kèm bên dưới.</p>";

        // Giả sử bạn có một phương thức tạo file PDF hóa đơn
        byte[] invoicePdf = generateInvoicePdf(bill.getId());

        try {
            emailService.sendInvoiceEmail(user.getEmail(), "Hóa đơn #" + bill.getId(), emailBody, invoicePdf);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bill;
    }

    @Override
    public byte[] generateInvoicePdf(Long billId) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với ID: " + billId));
        try {
            return pdfService.generateInvoice(bill);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo file PDF hóa đơn!", e);
        }
    }

    @Override
    public void sendInvoiceEmail(Long billId) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với ID: " + billId));

        if (bill.getUserEntity().getEmail() == null || bill.getUserEntity().getEmail().isEmpty()) {
            throw new RuntimeException("Người dùng chưa có email, không thể gửi hóa đơn!");
        }

        byte[] pdfInvoice = generateInvoicePdf(billId);

        try {
            emailService.sendInvoiceEmail(
                    bill.getUserEntity().getEmail(),
                    "Hóa đơn mua hàng #" + bill.getId(),
                    "Cảm ơn bạn đã mua hàng! Đính kèm là hóa đơn của bạn.",
                    pdfInvoice
            );
        } catch (MessagingException e) {
            System.err.println("Lỗi khi gửi email hóa đơn: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updatePaymentStatus(Long billId, boolean isPaid) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với ID: " + billId));

        if ("PAID".equals(bill.getStatus())) {
            throw new RuntimeException("Hóa đơn này đã được thanh toán trước đó!");
        }

        bill.setStatus(isPaid ? "PAID" : "FAILED");
        billRepository.save(bill);
    }
}
