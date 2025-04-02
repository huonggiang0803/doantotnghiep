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
            ship.getId(),
            ship.getFullName(),
            ship.getPhone(),
            ship.getAddress(),
                ship.isDefault()
        );
        shippingDTOList.add(dto);
    }
    return shippingDTOList;
}

    @Override
    public void deleteProductByID(long id) {
            this.inforShipRepository.deleteById(id);
    }

    public InforShippingDTO getDefaultShipping() {
        return inforShipRepository.findByIsDefaultTrue()
                .map(this::convertToDTO) // Chuyển đổi từ entity sang DTO
                .orElse(null);
    }

    private InforShippingDTO convertToDTO(InforShipping entity) {
        InforShippingDTO dto = new InforShippingDTO();
        dto.setId(entity.getId());
        dto.setFullName(entity.getFullName());
        dto.setAddress(entity.getAddress());
        dto.setPhone(entity.getPhone());
        return dto;
    }

    public void setDefaultShipping(Long id) {
        // Bỏ trạng thái mặc định của tất cả các địa chỉ
        List<InforShipping> allShipping = inforShipRepository.findAll();
        for (InforShipping shipping : allShipping) {
            shipping.setDefault(false); // Đặt tất cả địa chỉ thành không mặc định
        }
        inforShipRepository.saveAll(allShipping);

        // Đặt trạng thái mặc định cho địa chỉ được chọn
        InforShipping defaultShipping = inforShipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ với ID: " + id));
        defaultShipping.setDefault(true);
        inforShipRepository.save(defaultShipping);
    }
}
    

