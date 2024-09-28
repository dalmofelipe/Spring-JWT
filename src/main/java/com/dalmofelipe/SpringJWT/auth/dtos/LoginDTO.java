package com.dalmofelipe.SpringJWT.auth.dtos;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginDTO {
    
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 20, message = "a senha deve conter entre {min} e {max} caracteres")
    private String password;

    // TODO: remover isso daqui
    public UsernamePasswordAuthenticationToken toAuthToken() {
        return new UsernamePasswordAuthenticationToken(email, password);
    }

}
