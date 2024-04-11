package org.iperp.Dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.iperp.Utilities.UniqueUserName;


@Getter
@Setter
public class RegistrationRequest {

    @NotEmpty(message = "This field is required.")
    @Size(max = 255)
    @UniqueUserName
    private String username;

    @NotEmpty(message = "This field is required.")
    @Size(max = 255)
    private String password;

    @NotEmpty(message = "This field is required.")
    @Size(max = 255)
    private String email;

}
