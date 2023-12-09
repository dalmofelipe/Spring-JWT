package com.dalmofelipe.SpringJWT.User;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dalmofelipe.SpringJWT.Auth.dtos.RegisterDTO;
import com.dalmofelipe.SpringJWT.Role.Role;
import com.dalmofelipe.SpringJWT.Role.RoleRecord;
import com.dalmofelipe.SpringJWT.Role.RoleRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;


    public User saveUser(RegisterDTO dto) {
        Optional<User> userOpt = this.userRepository.findByEmail(dto.getEmail());
        if(userOpt.isPresent()) throw new RuntimeException("este email está em uso");
        return this.userRepository.save(dto.toModel());
    }


    public User addUserRole(Long id, RoleRecord roleRecord) {
        Optional<User> userOpt = this.userRepository.findById(id);
        var user = userOpt.orElseThrow(() -> new RuntimeException("usuário não encontrado"));

        Optional<Role> roleOpt = this.roleRepository.findByName(roleRecord.name());
        var role = roleOpt.orElseThrow(() -> new RuntimeException("função não encontrada"));

        if(user.getRoles().contains(role)) 
            throw new RuntimeException("usuário já possui a ROLE ("+roleRecord.name()+")");

        user.getRoles().add(role);
        
        return this.userRepository.save(user);
    }
}
