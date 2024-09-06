package com.dalmofelipe.SpringJWT.role;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;


    public Role save(Role role) {
        Optional<Role> roleOpt = this.roleRepository.findByName(role.getName());

        if(roleOpt.isPresent()) return null;

        return this.roleRepository.save(role);
    }

    public List<Role> listAll() {
        return this.roleRepository.findAll();
    }
}
