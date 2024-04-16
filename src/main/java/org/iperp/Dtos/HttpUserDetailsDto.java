package org.iperp.Dtos;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class HttpUserDetailsDto extends User {

    public final Long id;

    public HttpUserDetailsDto(final Long id, final String username, final String hash,
                              final Collection<? extends GrantedAuthority> authorities) {
        super(username, hash, authorities);
        this.id = id;
    }

}
