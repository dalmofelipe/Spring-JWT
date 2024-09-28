package com.dalmofelipe.SpringJWT.role;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/roles")
@Tag(
    name = "Roles", 
    description = "Endpoints para gerenciamento de permissões (ROLES)"
)
public class RoleEndpoints {

    @Autowired
    private RoleService roleService;


    @Operation(
        summary = "Criar novas ROLEs", 
        description = "Retorna nova ROLE cadastrada"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Retorna ROLE criada",
        content = {
            @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = Role.class)
            )
        }
    )
    @PostMapping
    public ResponseEntity<Object> createRole(@RequestBody RoleRecord record) {
        return ResponseEntity.ok().body(this.roleService.save(record.toModel()));
    }

    
    @Operation(
        summary = "Obter todas ROLEs cadastradas", 
        description = "Retorna uma lista de todas as ROLEs cadastradas"
    )
    @GetMapping
    public List<Role> listAll() {
        return this.roleService.listAll();
    }
}
