package com.tokioschool.praticas.repositories;

import com.tokioschool.praticas.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role getByName(String roleName);
}
