package com.legou.common.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UserInfo {
    private Long id;

    private String username;

    private List<String> role;

    public UserInfo(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public UserInfo(Long id, String username, List<String> role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }
}
