package com.dalmofelipe.SpringJWT.Auth;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.dalmofelipe.SpringJWT.User.User;
import com.dalmofelipe.SpringJWT.User.UserRepository;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String email) 
            throws UsernameNotFoundException {
        Optional<User> optional = this.userRepository.findByEmail(email);
        return optional.orElseThrow(() -> new UsernameNotFoundException("usuário não encontrado"));        
    }
    
}
