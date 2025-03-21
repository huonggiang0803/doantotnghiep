package com.example.doan.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.doan.dto.OrderDTO;
import com.example.doan.dto.OrderItemDTO;
import com.example.doan.entity.Cart;
import com.example.doan.entity.CartItem;
import com.example.doan.entity.InforShipping;
import com.example.doan.entity.OrderItem;
import com.example.doan.entity.Orders;
import com.example.doan.entity.Product;
import com.example.doan.entity.ProductVariant;
import com.example.doan.entity.UserEntity;
import com.example.doan.repository.CartItemRepository;
import com.example.doan.repository.CartRepository;
import com.example.doan.repository.InforShipRepository;
import com.example.doan.repository.OrderRepository;
import com.example.doan.repository.ProductRepository;
import com.example.doan.repository.ProductVariantReposi;
import com.example.doan.status.OrderEnum;
import com.example.doan.status.PaymentStatus;
import jakarta.transaction.Transactional;
@Service
public class OderImple implements OrderService{

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductVariantReposi productVariantReposi;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private InforShipRepository inforShipRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    @Transactional
    public OrderDTO createOrderFromCart(Long cartId, Long shippingId, String paymentMethod) {
            Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new RuntimeException("Cart not found"));

            if (cart.getUser() == null) {
                throw new RuntimeException("Không tìm thấy id giỏ hàng: " + cartId);
            }
        
            UserEntity user = cart.getUser();
    
    List<CartItem> cartItems = cartItemRepository.findByCart(cart);
    if (cartItems.isEmpty()) {
        throw new RuntimeException("Cart is empty");
    }
    InforShipping shippingAddress = inforShipRepository.findById(shippingId)
        .orElseThrow(() -> new RuntimeException("SKhông tìm thất địa chỉ: " + shippingId));

        Orders order = Orders.builder()
                .userId(user)
                .inforShipping(shippingAddress)
                .paymentMethod(paymentMethod)
                .paymentStatus(PaymentStatus.UNPAID)
                .orderEnum(OrderEnum.PENDING)
                .items(new ArrayList<>())
                .totalPrice(0.0)
                .build();
        double total = 0.0;
       for (CartItem cartItem : cartItems) {
        ProductVariant productVariant = cartItem.getProductVariant();
        Product product = productVariant.getProduct();
        double finalPrice = productVariant.getFinalPrice();

        if (productVariant.getStock() < cartItem.getQuantity().intValue()) {
            throw new RuntimeException("Sản phẩm hết: " + productVariant.getProduct().getProductName() + 
                                        " - " + productVariant.getColor() + ", " + productVariant.getSize());
        }
            String img = null;
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                img = product.getImages().get(0).getFileName();
            }
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(productVariant) 
                    .quantity(cartItem.getQuantity())
                    .price(finalPrice)
                    .subTotal(finalPrice * cartItem.getQuantity()) 
                    .img(img)
                    .build();
                    total += orderItem.getSubTotal();
                    order.getItems().add(orderItem);
                    productVariant.setStock(productVariant.getStock() - cartItem.getQuantity().intValue());
                    // productVariant.getProduct().updateStatus();
                    // productRepository.save(product);
                    productVariantReposi.save(productVariant);
                }
                order.setTotalPrice(total);
                orderRepository.save(order);
                        cartItemRepository.deleteAll(cartItems);
        
                return mapToDTO(order);
    }
    public OrderDTO mapToDTO(Orders order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setCustomerId(order.getUserId().getId());
        dto.setTotalPrice(order.getTotalPrice());
        
        if (order.getInforShipping() != null) {
            dto.setShippingId(order.getInforShipping().getId());
            dto.setSetShippingAddress(order.getInforShipping().getAddress());
        } else {
            dto.setSetShippingAddress("N/A");
        }
        
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setStatus(order.getOrderEnum());
        dto.setPaymentStatus(order.getPaymentStatus());

        List<OrderItemDTO> orderItemDTOs = order.getItems().stream().map(item -> {
            OrderItemDTO itemDTO = new OrderItemDTO();
            itemDTO.setProductId(item.getProduct().getId());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setPrice(item.getPrice());
            itemDTO.setSubTotal(item.getSubTotal());
            itemDTO.setImg(item.getImg());
            return itemDTO;
        }).collect(Collectors.toList());

        dto.setOrderItems(orderItemDTOs);
        return dto;
    }
}
  