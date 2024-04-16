package org.iperp.Services.Implementation;

import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.iperp.Dtos.HttpUserDetailsDto;
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
    public HttpUserDetailsDto loadUserByUsername(final String username) {
        final AppUser appUser = appUserRepository.findByUsernameIgnoreCase(username);
        if (appUser == null) {
            log.warn("user not found: {}", username);
            throw new UsernameNotFoundException("User " + username + " not found");
        }
        final List<SimpleGrantedAuthority> authorities = appUser.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toList());
        return new HttpUserDetailsDto(appUser.getId(), username, appUser.getHash(), authorities);
    }

}
