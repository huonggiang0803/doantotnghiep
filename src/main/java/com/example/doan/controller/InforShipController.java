package com.example.doan.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.doan.dto.InforShippingDTO;

import com.example.doan.service.InforShipService;

@RestController
@RequestMapping("/api/ship")
public class InforShipController {
    @Autowired
    InforShipService inforShipService;
    @GetMapping("/all")
    public ResponseEntity<List<InforShippingDTO>> getAllShipping() {
        return ResponseEntity.ok(inforShipService.getAllShip());
    }
    @PostMapping("/save")
    public ResponseEntity<String> createShipping(@RequestBody InforShippingDTO inforShippingDTO) {  
        Long shippingId = inforShipService.saveShipping(inforShippingDTO);
        return ResponseEntity.ok("Thông tin giao hàng đã được lưu với ID: " + shippingId);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteShipping(@PathVariable long id) {
        inforShipService.deleteProductByID(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateShipping(
            @PathVariable long id,
            @RequestBody InforShippingDTO inforShippingDTO) {
        inforShipService.updateShip(id, inforShippingDTO);
        return ResponseEntity.ok("Cập nhật thông tin giao hàng thành công!");
    }}
