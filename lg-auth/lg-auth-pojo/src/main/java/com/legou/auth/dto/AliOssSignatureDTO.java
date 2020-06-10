package com.legou.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class AliOssSignatureDTO {
    private String accessId;
    private String host;
    private String policy;
    private String signature;
    private long expire;
    private String dir;
}
