package com.legou.auth.web;

import com.legou.auth.dto.AliOssSignatureDTO;
import com.legou.auth.service.AliAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("ali")
public class AliAuthController {
    private AliAuthService aliAuthService;

    public AliAuthController(AliAuthService aliAuthService) {
        this.aliAuthService = aliAuthService;
    }

    @GetMapping("/oss/signature")
    public ResponseEntity<AliOssSignatureDTO> getAliSignature(){
        return ResponseEntity.ok(aliAuthService.getSignature());
    }
}
