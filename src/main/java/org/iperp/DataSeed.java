package org.iperp;

import org.iperp.Entities.AppRole;
import org.iperp.Repositories.IAppRoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeed implements CommandLineRunner {

    private final IAppRoleRepository appRoleRepository;

    public DataSeed(IAppRoleRepository appRoleRepository) {
        this.appRoleRepository = appRoleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        SeedRoles();
    }

    private void SeedRoles() {
        if (appRoleRepository.count() == 0) {

            AppRole userRole = new AppRole();
            userRole.setAuthority("USER");

            AppRole adminRole = new AppRole();
            adminRole.setAuthority("ADMIN");

            AppRole developerRole = new AppRole();
            developerRole.setAuthority("DEVELOPER");

            AppRole recruiterRole = new AppRole();
            recruiterRole.setAuthority("RECRUITER");

            appRoleRepository.saveAll(List.of(userRole, adminRole, developerRole, recruiterRole));
        }
    }
}
