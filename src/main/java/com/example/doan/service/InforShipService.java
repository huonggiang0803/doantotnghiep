package com.example.doan.service;

import java.util.List;

import com.example.doan.dto.InforShippingDTO;


public interface InforShipService {
    long saveShipping (InforShippingDTO inforShippingDTO);
    void updateShip(long ship_id, InforShippingDTO inforShippingDTO);
    List<InforShippingDTO> getAllShip();
    void deleteProductByID(long id); 
}
