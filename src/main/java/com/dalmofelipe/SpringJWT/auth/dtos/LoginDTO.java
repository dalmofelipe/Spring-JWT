package com.dalmofelipe.SpringJWT.auth.dtos;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {
    
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    // TODO: remover isso daqui
    public UsernamePasswordAuthenticationToken toAuthToken() {
        return new UsernamePasswordAuthenticationToken(email, password);
    }

}
