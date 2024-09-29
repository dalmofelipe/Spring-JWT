package com.dalmofelipe.SpringJWT.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dalmofelipe.SpringJWT.auth.dtos.LoginDTO;
import com.dalmofelipe.SpringJWT.auth.dtos.RegisterDTO;
import com.dalmofelipe.SpringJWT.user.User;
import com.dalmofelipe.SpringJWT.user.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Endpoints para gestão de acessos")
public class AuthEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserService userService;


    @Operation(summary = "Login", description = "Informe credenciais para gerar token de acesso")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Retorna TOKEN de acesso as rotas restritas", content = {
            @Content(mediaType = "text/plain;charset=UTF-8", schema = @Schema(implementation = String.class))}),
        @ApiResponse(responseCode = "400", 
            description = "Requisição Inválida, se o email e senha informado não forem validos", content = {
            @Content(mediaType = "application/json", schema = @Schema(
                implementation = com.dalmofelipe.SpringJWT.exceptions.ApiResponse.class))}),
        @ApiResponse(responseCode = "401", 
            description = "Não autorizado, se email ou senha incorreta ou não cadastrado no sistema", content = {
            @Content(mediaType = "text/plain;charset=UTF-8", schema = @Schema(implementation = String.class))})
    })
    @SecurityRequirements
    @PostMapping("/login")
    public ResponseEntity<Object> login(@Validated @RequestBody LoginDTO login) {
        // TODO: Mover logica do toAuthToken para controller do login
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = login.toAuthToken();
    
        Authentication autheticate = this.authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        String token = this.tokenService.generateJWT(autheticate);
        String fullToken = "Bearer " + token;
        
        return ResponseEntity.ok(fullToken);
    }

    @SecurityRequirements
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7); // Remove "Bearer "
        logger.debug("[AuthEndpoint::logout] Bearer removido : {}", jwt);

        if(tokenService.isTokenValid(jwt) && !tokenService.isTokenBlacklisted(jwt)) {
            Long userId = tokenService.getSubject(jwt);
            tokenService.blacklistToken(jwt);
            return ResponseEntity.ok().body("Logout realizado com sucesso para o usuário " + userId);
        }

        return ResponseEntity.ok().body("Token inválida ou já expirada, logout não necessário");
    }

    @Operation(summary = "Registrar novo Usuário", description = "Cadastro de novos usuários")
    @ApiResponse(responseCode = "200", description = "Retorna novo usuário cadastrado", content = { 
        @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)) })
    @SecurityRequirements
    @PostMapping("/register")
    public ResponseEntity<Object> register(@Validated @RequestBody RegisterDTO dto) {
        return ResponseEntity.ok(this.userService.saveUser(dto));
    }
}
