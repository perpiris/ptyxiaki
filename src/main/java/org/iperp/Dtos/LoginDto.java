package org.iperp.Dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDto {

    @NotEmpty(message = "This field is required.")
    @Size(max = 255)
    private String username;

    @NotEmpty(message = "This field is required.")
    @Size(max = 255)
    private String password;
}
