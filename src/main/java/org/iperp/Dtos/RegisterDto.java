package org.iperp.Dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.iperp.Enums.UserRole;
import org.iperp.Utilities.UniqueUserName;

@Getter
@Setter
public class RegisterDto {

    @NotEmpty(message = "This field is required.")
    @Size(max = 255)
    @UniqueUserName
    private String username;

    @NotEmpty(message = "This field is required.")
    @Size(max = 255)
    private String email;

    @NotEmpty(message = "This field is required.")
    @Size(max = 255)
    private String name;

    @NotEmpty(message = "This field is required.")
    @Size(max = 255)
    private String surname;

    @NotEmpty(message = "This field is required.")
    @Size(max = 255)
    private String password;

    @NotNull(message = "This field is required.")
    private UserRole role;
}
