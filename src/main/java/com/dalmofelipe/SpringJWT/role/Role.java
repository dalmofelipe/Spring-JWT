package com.dalmofelipe.SpringJWT.role;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"name", "id"})
@Entity
@Table(name = "tb_roles")
public class Role implements GrantedAuthority {

    @Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Override
    public String getAuthority() {
        return this.name;
    }
}
