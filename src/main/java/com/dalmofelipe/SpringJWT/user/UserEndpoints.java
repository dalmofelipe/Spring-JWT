package com.dalmofelipe.SpringJWT.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dalmofelipe.SpringJWT.role.RoleRecord;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/users")
@Tag(
    name = "Usuários", 
    description = "Endpoints para gerenciamento de usuários"
)
public class UserEndpoints {

    @Autowired
    private UserService userService;


    @Operation(summary = "Obter todos os usuários", 
        description = "Retorna uma lista de todos os usuários cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso")
    @SecurityRequirements
    @GetMapping
    public ResponseEntity<List<User>> listUsers() {
        return ResponseEntity.ok().body(this.userService.findAll());
    }


    @Operation(summary = "Obter um usuário", description = "Busca de usuário por ID cadastrado")
    @ApiResponse(responseCode = "200", description = "Retorna um usuários caso encontre", content = {
        @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))})
    @SecurityRequirements
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return userService.findByID(id).map((user) -> ResponseEntity.ok().body(user))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @Operation(summary = "Atribuir permissões ao usuário", description = """
        Rota para atribuir permissão ROLE a um usuário(a).\n
        @params id = ID do usuário\n
        @body role = ROLE a ser atribuida\n
        @returns Usuário com nova permissão atribuida
        """)
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "Retorna o usuário com nova ROLE atribuida", content = { 
                @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "409", description = "Conflita se o usuário já possui a ROLE informada", content = { 
                @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))})
        }
    )
    @PostMapping("/{id}/role")
    public ResponseEntity<Object> setUserRole(@PathVariable(name = "id") Long id, @RequestBody RoleRecord role) {
        return ResponseEntity.ok().body(this.userService.addUserRole(id, role));
    }
}
