package org.iperp.Services.Implementation;

import org.iperp.Entities.AppUser;
import org.iperp.Entities.UserSkill;
import org.iperp.Repositories.IAppUserRepository;
import org.iperp.Services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService {
    private final IAppUserRepository appUserRepository;

    @Autowired
    public UserService(IAppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public Map<Long, Integer> getUserSkills(String username) {
        AppUser user = appUserRepository.findByUsernameIgnoreCase(username);
        if (user == null) {
            return Collections.emptyMap();
        }
        return user.getSkills().stream()
                .collect(Collectors.toMap(
                        skill -> skill.getSkill().getId(),
                        UserSkill::getYears
                ));
    }
}
