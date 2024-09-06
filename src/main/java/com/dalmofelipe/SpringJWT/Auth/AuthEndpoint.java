package com.dalmofelipe.SpringJWT.Auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import com.dalmofelipe.SpringJWT.Auth.dtos.LoginDTO;
import com.dalmofelipe.SpringJWT.Auth.dtos.RegisterDTO;
import com.dalmofelipe.SpringJWT.Exceptions.ApiError;
import com.dalmofelipe.SpringJWT.User.User;
import com.dalmofelipe.SpringJWT.User.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Endpoints para gestão de acessos")
public class AuthEndpoint {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserService userService;


    @Operation(
        summary = "Login", 
        description = "Logar para obter token de acesso"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Retorna TOKEN de acesso as rotas restritas",
        content = {
            @Content(
                mediaType = "text/plain;charset=UTF-8", 
                schema = @Schema(implementation = String.class)
            )
        }
    )
    @PostMapping("/login")
    public ResponseEntity<Object> login(@Validated @RequestBody LoginDTO login) {

        // TODO: Mover logica do toAuthToken para controller do login
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
            = login.toAuthToken();

        try {
            Authentication autheticate = this.authenticationManager
                .authenticate(usernamePasswordAuthenticationToken);

            String token = this.tokenService.generateJWT(autheticate);
            String fullToken = "Bearer " + token;
            
            return ResponseEntity.ok(fullToken);
        }
        catch (Exception e) {
            var err = new ApiError();
            err.setMessage(e.getMessage());

            return ResponseEntity.badRequest().body(err);
        }
    }


    // @PostMapping("/logout")
    // public ResponseEntity<?> logout(@RequestHeader("Authorization") String token)
    // {
    //     String jwt = token.substring(7); // Remove "Bearer "

    //     long ttlMillis = tokenService.getExpirationTimeInMilis() - System.currentTimeMillis();
        
    //     if(ttlMillis > 0)
    //     {
    //         tokenService.blacklistToken(jwt, ttlMillis);
    //         return ResponseEntity.ok().body("Logout realizado com sucesso");
    //     }

    //     return ResponseEntity.ok().body("Token já expirada, logout não necessário");   
    // }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token)
    {
        String jwt = token.substring(7); // Remove "Bearer "

        try {
            if(tokenService.isTokenValid(jwt)) 
            {
                Long userId = tokenService.getSubject(jwt);
                tokenService.blacklistToken(jwt);
                return ResponseEntity.ok()
                    .body("Logout realizado com sucesso para o usuário " + userId);
            } 
            else 
            {
                return ResponseEntity.ok()
                    .body("Token inválida ou já expirada, logout não necessário");
            }
        } 
        catch (Exception e) {
            System.out.println("Erro ao processar token: " + e.getMessage());
            return ResponseEntity.badRequest().body("Erro ao processar token: " + e.getMessage());
        }
    }


    @Operation(
        summary = "Registrar novo Usuário", 
        description = "Cadastro de novos usuários"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Retorna novo usuário cadastrado",
        content = {
            @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = User.class)
            )
        }
    )
    @PostMapping("/register")
    public ResponseEntity<Object> register(@Validated @RequestBody RegisterDTO dto) {
        try {
            return ResponseEntity.ok(this.userService.saveUser(dto));
        }
        catch (RuntimeException e) {
            var err = new ApiError();
            err.setMessage(e.getMessage());

            return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(err);
        }
    }
}
