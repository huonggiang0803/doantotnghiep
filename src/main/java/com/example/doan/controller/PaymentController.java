package com.example.doan.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import com.example.doan.config.Config;
import com.example.doan.dto.CartDTO;
import com.example.doan.entity.Bill;
import com.example.doan.entity.CartItem;
import com.example.doan.entity.OrderItem;
import com.example.doan.entity.Orders;
import com.example.doan.entity.UserEntity;
import com.example.doan.repository.BillRepository;
import com.example.doan.repository.CartRepository;
import com.example.doan.repository.OrderRepository;
import com.example.doan.repository.UserRepository;
import com.example.doan.service.BillService;
import com.example.doan.service.CartService;
import com.example.doan.service.UserService;
import com.example.doan.status.OrderEnum;
import com.example.doan.status.PaymentStatus;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private BillService billService;

    @Autowired
    private CartService cartService;
    

    @GetMapping("/create_payment")
    public String createPayment(@RequestParam("orderId") Long orderId) throws UnsupportedEncodingException {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        // Đánh dấu đơn hàng chưa thanh toán
        order.setPaymentStatus(PaymentStatus.UNPAID);
        order.setOrderEnum(OrderEnum.PENDING);
        orderRepository.save(order);
        
        double amount = order.getTotalPrice() * 100;

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        String bankCode = "NCB";
        String vnp_TxnRef = order.getId().toString();
        String vnp_IpAddr = "127.0.0.1";
        String vnp_TmnCode = Config.vnp_TmnCode;
        
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf((long) amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", bankCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang: " + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", "http://localhost:8080/api/payment/payment_return");
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (Iterator<String> itr = fieldNames.iterator(); itr.hasNext(); ) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString())).append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);
        return Config.vnp_PayUrl + "?" + query.toString();
    }

    @GetMapping("/payment_return")
    public ResponseEntity<String> paymentReturn(@RequestParam Map<String, String> params) {
        String vnp_ResponseCode = params.get("vnp_ResponseCode");
        String vnp_TransactionStatus = params.get("vnp_TransactionStatus");
        Long orderId = Long.parseLong(params.get("vnp_TxnRef"));

        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if ("00".equals(vnp_ResponseCode) && "00".equals(vnp_TransactionStatus)) {
            order.setPaymentStatus(PaymentStatus.PAID);
            order.setOrderEnum(OrderEnum.PENDING);
            orderRepository.save(order);
     
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntity user = userService.findByUsername(username);
            List<OrderItem> orderItems = order.getItems(); 

    Bill bill = billService.createBill(user, order, orderItems, "VNPAY");
    billService.sendInvoiceEmail(bill.getId());        
            return ResponseEntity.ok("Thanh toán thành công! Đơn hàng đã được cập nhật.");
        } else {
            order.setPaymentStatus(PaymentStatus.UNPAID);
            order.setOrderEnum(OrderEnum.PENDING);
            orderRepository.save(order);
            return ResponseEntity.badRequest().body("Thanh toán thất bại. Vui lòng thử lại!");
        }
    }

    @GetMapping("/refund_payment")
    public ResponseEntity<String> refundPayment(@RequestParam("orderId") Long orderId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getPaymentStatus() != PaymentStatus.PAID) {
            return ResponseEntity.badRequest().body("Chỉ có thể hoàn tiền cho đơn hàng đã thanh toán.");
        }
        boolean refundSuccess = true;
        if (refundSuccess) {
            order.setPaymentStatus(PaymentStatus.REFUNDED);
            order.setOrderEnum(OrderEnum.CANCELLED);
            orderRepository.save(order);
            return ResponseEntity.ok("Hoàn tiền thành công! Đơn hàng đã được cập nhật trạng thái.");
        } else {
            return ResponseEntity.badRequest().body("Hoàn tiền thất bại. Vui lòng thử lại!");
        }
    }
}
