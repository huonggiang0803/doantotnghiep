package com.example.doan.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.doan.entity.Bill;
import com.example.doan.entity.BillItem;
import com.example.doan.entity.Orders;
import com.example.doan.repository.BillRepository;
import com.example.doan.repository.OrderRepository;

@Service
public class BillService {
    @Autowired
    private BillRepository billRepository;

    @Autowired
    private OrderRepository ordersRepository;

    @Autowired
    private EmailService emailService;

    public Bill getBillById(Long billId) {
        return billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với ID: " + billId));
    }
    public Bill createBill(Long orderId) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (billRepository.existsByOrder(order)) {
            throw new RuntimeException("Bill for this order already exists!");
        }

        // Kiểm tra xem đơn hàng có thông tin người dùng hay không
        if (order.getUserId() == null) {
            throw new RuntimeException("User not found for this order!");
        }

        Bill bill = new Bill();
        bill.setOrder(order);

        // Truyền toàn bộ đối tượng UserEntity
        bill.setUserEntity(order.getUserId());

        bill.setShipping(order.getInforShipping());
        bill.setTotalAmount(order.getTotalPrice());
        bill.setPaymentMethod(order.getPaymentMethod());
        bill.setShippingFee(order.getShippingFee());
        bill.setStatus(order.getPaymentStatus().toString());
        billRepository.save(bill);

        List<BillItem> billItems = order.getItems().stream().map(orderItem -> {
            BillItem billItem = new BillItem();
            billItem.setBill(bill);
            billItem.setProductVariant(orderItem.getProduct());
            billItem.setQuantity(orderItem.getQuantity());
            billItem.setPrice(orderItem.getPrice());
            billItem.setTotalPrice(orderItem.getSubTotal());
            return billItem;
        }).collect(Collectors.toList());

        bill.setBillItems(billItems);

        billRepository.save(bill);

        emailService.sendInvoiceEmail(bill);

        return bill;
    }
}
