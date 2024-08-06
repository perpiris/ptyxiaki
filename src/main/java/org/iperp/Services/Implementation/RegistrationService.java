package org.iperp.Services.Implementation;

import lombok.extern.slf4j.Slf4j;
import org.iperp.Dtos.RegisterDto;
import org.iperp.Entities.AppRole;
import org.iperp.Entities.AppUser;
import org.iperp.Enums.UserRole;
import org.iperp.Repositories.IAppRoleRepository;
import org.iperp.Repositories.IAppUserRepository;
import org.iperp.Services.IRegistrationService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class RegistrationService implements IRegistrationService {

    private final IAppUserRepository appUserRepository;
    private final IAppRoleRepository appRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(final IAppUserRepository appUserRepository,
                               final IAppRoleRepository appRoleRepository,
                               final PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.appRoleRepository = appRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(final RegisterDto registerDto) {

        final AppUser appUser = new AppUser();
        appUser.setUsername(registerDto.getUsername());
        appUser.setName(registerDto.getName());
        appUser.setSurname(registerDto.getSurname());
        appUser.setHash(passwordEncoder.encode(registerDto.getPassword()));
        appUser.setEmail(registerDto.getEmail());
        Set<AppRole> roles = new HashSet<>();

        AppRole baseRole = appRoleRepository.findByAuthority(UserRole.USER.toString());
        roles.add(baseRole);
        AppRole userRole = appRoleRepository.findByAuthority(registerDto.getRole().toString());
        roles.add(userRole);

        appUser.setRoles(roles);

        appUserRepository.save(appUser);
    }

    public boolean usernameExists(final String username) {
        return appUserRepository.existsByUsernameIgnoreCase(username);
    }
}
