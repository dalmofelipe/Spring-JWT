package com.dalmofelipe.SpringJWT.Role;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dalmofelipe.SpringJWT.Exceptions.ApiError;

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
        Role role = this.roleService.save(record.toModel());
        
        if (role == null) {
            var err = new ApiError();
            err.setMessage("a ROLE ("+record.name()+") já existe");

            return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(err);
        }

        return ResponseEntity.ok().body(role);
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
