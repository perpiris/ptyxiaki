package org.iperp.Services.Implementation;

import org.iperp.Entities.AppUser;
import org.iperp.Repositories.IAppUserRepository;
import org.iperp.Services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService {
    private final IAppUserRepository appUserRepository;

    @Autowired
    public UserService(IAppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public Set<Long> getUserSkills(String username) {
        AppUser user = appUserRepository.findByUsernameIgnoreCase(username);
        if (user == null) {
            return Collections.emptySet();
        }
        return user.getSkills().stream()
                .map(userSkill -> userSkill.getSkill().getId())
                .collect(Collectors.toSet());
    }
}
