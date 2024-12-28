package com.tokioschool.praticas.dtos;

import lombok.Data;

@Data
public class RegisterDTO {
    private String username;
    private String password;
    private String name;
    private String surname;
    private String email;
}
