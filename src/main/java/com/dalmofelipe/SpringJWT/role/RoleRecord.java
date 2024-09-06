package com.dalmofelipe.SpringJWT.role;

import org.springframework.beans.BeanUtils;

public record RoleRecord(String name) {

    public Role toModel() {
        Role role = new Role();
        BeanUtils.copyProperties(this, role);
        return role;
    }
}
