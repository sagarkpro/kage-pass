package com.sagar.kagepass.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserDto {
    @NotNull @NotBlank
    private String firstName;

    private String middleName;
    private String lastName;

    @NotNull @NotBlank
    private String email;

    @NotNull @NotBlank
    private String password;

    @NotNull
    private RoleDto role;
}
