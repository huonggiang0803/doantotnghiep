package com.example.doan.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.doan.dto.InforShippingDTO;
import com.example.doan.entity.InforShipping;
import com.example.doan.repository.InforShipRepository;

@Service
public class InforShipImple implements InforShipService{
    @Autowired
    InforShipRepository inforShipRepository;
    @Override
    public long saveShipping(InforShippingDTO inforShippingDTO) {
        InforShipping inforShipping = InforShipping.builder()
                .fullName(inforShippingDTO.getFullName())
                .phone(inforShippingDTO.getPhone())
                .address(inforShippingDTO.getAddress())
                .build();
        inforShipRepository.save(inforShipping);
        System.out.println("Shipping ID: " + inforShipping.getId()); // Log shippingId
        return inforShipping.getId();
    }

    @Override
    public void updateShip(long ship_id, InforShippingDTO inforShippingDTO) {
        InforShipping uInforShipping = getShipping(ship_id);
        uInforShipping.setFullName(inforShippingDTO.getFullName());
        uInforShipping.setPhone(inforShippingDTO.getPhone());
        uInforShipping.setAddress(inforShippingDTO.getAddress());
        inforShipRepository.save(uInforShipping);
    }
     private InforShipping getShipping(long ship_id){
        return inforShipRepository.findById(ship_id).orElseThrow(()-> new RuntimeException("shipping not found"));
    }
    @Override
public List<InforShippingDTO> getAllShip() {
    List<InforShipping> shippingList = inforShipRepository.findAll();
    List<InforShippingDTO> shippingDTOList = new ArrayList<>();

    for (InforShipping ship : shippingList) {
        InforShippingDTO dto = new InforShippingDTO(
            ship.getFullName(),
            ship.getPhone(),
            ship.getAddress()
           
        );
        shippingDTOList.add(dto);
    }
    return shippingDTOList;
}

    @Override
    public void deleteProductByID(long id) {
            this.inforShipRepository.deleteById(id);
    }

}
    

