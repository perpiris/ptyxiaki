package org.iperp.Dtos;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class HttpUserDetails extends User {

    public final Long id;

    public HttpUserDetails(final Long id, final String username, final String hash,
                           final Collection<? extends GrantedAuthority> authorities) {
        super(username, hash, authorities);
        this.id = id;
    }

}
