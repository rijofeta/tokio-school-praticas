package com.tokioschool.praticas.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "roles")
@Data
@Getter
@Setter
public class Role {
    public static final String USER_ROLE = "USER";
    public static final String ADMIN_ROLE = "ADMIN";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
}
