package com.dalmofelipe.SpringJWT.user;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.dalmofelipe.SpringJWT.auth.dtos.RegisterDTO;
import com.dalmofelipe.SpringJWT.role.Role;
import com.dalmofelipe.SpringJWT.role.RoleRecord;
import com.dalmofelipe.SpringJWT.role.RoleRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserPasswordEncoder userPasswordEncoder;


    public List<User> findAll() {
        return userRepository.findAll();
    }


    public Optional<User> findByID(Long id) {
        return userRepository.findById(id);
    }

    public User saveUser(RegisterDTO dto) {
        Optional<User> userOpt = this.userRepository.findByEmail(dto.getEmail());
        if(userOpt.isPresent()) throw new RuntimeException("este email está em uso");

        var user = dto.toModel();
        user.setPassword(userPasswordEncoder.encode(dto.getPassword()));

        return this.userRepository.save(user);
    }

    public User addUserRole(@NonNull Long id, RoleRecord roleRecord) {
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
