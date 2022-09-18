package com.tchat.ms_authentification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class UserSigninDTO {

    @NotNull
    private String usernameOrEmail;

    @NotNull
    private String password;

}
