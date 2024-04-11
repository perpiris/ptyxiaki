package org.iperp.Services.Implementation;

import lombok.extern.slf4j.Slf4j;
import org.iperp.Dtos.RegistrationRequest;
import org.iperp.Entities.AppUser;
import org.iperp.Repositories.IAppUserRepository;
import org.iperp.Services.IRegistrationService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RegistrationService implements IRegistrationService {

    private final IAppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(final IAppUserRepository appUserRepository,
                               final PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(final RegistrationRequest registrationRequest) {
        log.info("registering new user: {}", registrationRequest.getUsername());

        final AppUser appUser = new AppUser();
        appUser.setUsername(registrationRequest.getUsername());
        appUser.setHash(passwordEncoder.encode(registrationRequest.getPassword()));
        appUser.setEmail(registrationRequest.getEmail());
        appUserRepository.save(appUser);
    }

    public boolean usernameExists(final String username) {
        return appUserRepository.existsByUsernameIgnoreCase(username);
    }

}
