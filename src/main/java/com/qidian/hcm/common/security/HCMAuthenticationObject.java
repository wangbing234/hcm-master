package com.qidian.hcm.common.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
@Setter
class HCMAuthenticationObject extends UsernamePasswordAuthenticationToken {

    private final String token;

    HCMAuthenticationObject(Object principal, Object credentials, String token) {
        super(principal, credentials);
        this.token = token;
    }
}
