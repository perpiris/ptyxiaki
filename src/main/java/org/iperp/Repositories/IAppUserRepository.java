package org.iperp.Repositories;

import org.iperp.Entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IAppUserRepository extends JpaRepository<AppUser, Long> {

    AppUser findByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);

}
