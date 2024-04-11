package org.iperp.Services.Implementation;

import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.iperp.Dtos.HttpUserDetails;
import org.iperp.Entities.AppUser;
import org.iperp.Repositories.IAppUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class HttpUserDetailsService implements UserDetailsService {

    private final IAppUserRepository appUserRepository;

    public HttpUserDetailsService(final IAppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public HttpUserDetails loadUserByUsername(final String username) {
        final AppUser appUser = appUserRepository.findByUsernameIgnoreCase(username);
        if (appUser == null) {
            log.warn("user not found: {}", username);
            throw new UsernameNotFoundException("User " + username + " not found");
        }
        final List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("USER"));
        return new HttpUserDetails(appUser.getId(), username, appUser.getHash(), authorities);
    }

}
