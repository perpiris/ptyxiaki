package org.iperp.Dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppUserDto {
    
    private Long id;
    
    private String username;
    
    private String email;

    private String name;

    private String surname;
}
