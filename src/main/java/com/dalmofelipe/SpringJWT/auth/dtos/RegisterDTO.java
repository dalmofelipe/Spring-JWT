package com.dalmofelipe.SpringJWT.auth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import com.dalmofelipe.SpringJWT.user.User;

@Data
public class RegisterDTO {

    @Size(min = 3, max = 20)
    private String name;

    @Size(min = 3, max = 50)
    private String username;

    @Email
    private String email;

    @Size(min = 6, max = 20)
    private String password;

    // TODO: implementar Mappers
    public User toModel() {
        var user = new User();
        BeanUtils.copyProperties(this, user);
        return user;
    }
}
