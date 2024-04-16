package org.iperp.Repositories;

import org.iperp.Entities.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IAppRoleRepository extends JpaRepository<AppRole, Long> {
    AppRole findByAuthority(String authority);
}
