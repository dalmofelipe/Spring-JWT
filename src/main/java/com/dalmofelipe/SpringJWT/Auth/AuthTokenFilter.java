package com.dalmofelipe.SpringJWT.Auth;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.dalmofelipe.SpringJWT.User.User;
import com.dalmofelipe.SpringJWT.User.UserRepository;

import io.netty.util.internal.StringUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class AuthTokenFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);
    private final TokenService tokenService;
    private final UserRepository userRepository;
    
	public AuthTokenFilter (TokenService tokenService, UserRepository userRepository) {
		this.tokenService = tokenService;
		this.userRepository = userRepository;
	}

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response, 
            @NonNull FilterChain filterChain)
                throws ServletException, IOException {

        String tokenWithoutBearer = this.getTokenWithoutBearer(request);
        logger.debug("Requisição recebida para: {}", request.getRequestURI());
        logger.debug("Token extraída: {}", tokenWithoutBearer);
       
        if(!StringUtil.isNullOrEmpty(tokenWithoutBearer) && tokenService.isTokenValid(tokenWithoutBearer))
        {
            if (tokenService.isTokenBlacklisted(tokenWithoutBearer)) 
            {
                SecurityContextHolder.clearContext(); // Limpa a autenticação
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
            else 
            {
                this.doAuthUser(tokenWithoutBearer);
                logger.debug("Usuário autenticado com sucesso");
            }
        }
        else
        {
            logger.info("Token inválida");
            SecurityContextHolder.clearContext(); // Limpa a autenticação se a token for inválida
        }

        filterChain.doFilter(request, response);
    }

    private void doAuthUser(String tokenWithoutBearer) {
        Long userId = this.tokenService.getSubject(tokenWithoutBearer);
        Optional<User> optional = this.userRepository.findByIdWithJoinFecth(userId);
        User user = optional.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        UsernamePasswordAuthenticationToken authetication = 
            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authetication);
    }

    
    private String getTokenWithoutBearer(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if(token == null || token.isBlank() || !token.startsWith("Bearer ")) {
            return null;
        }

        return token.substring(7);
    }

}
