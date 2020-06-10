package com.legou.user.web;

import com.legou.user.dto.AddressDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("address")
public class AddressController {
    /**
     * 根据
     * @param id 地址id
     * @return 地址信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<AddressDTO> queryAddressById(@PathVariable("id") Long id){
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setId(1L);
        addressDTO.setUserId(30L);
        addressDTO.setStreet("广寒宫");
        addressDTO.setCity("上海");
        addressDTO.setDistrict("浦东新区");
        addressDTO.setAddressee("伐木人");
        addressDTO.setPhone("18888888888");
        addressDTO.setProvince("上海");
        addressDTO.setPostcode("210000");
        addressDTO.setIsDefault(true);
        return ResponseEntity.ok(addressDTO);
    }
}
