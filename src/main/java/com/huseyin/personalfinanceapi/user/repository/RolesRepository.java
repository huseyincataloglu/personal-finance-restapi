package com.huseyin.personalfinanceapi.user.repository;

import com.huseyin.personalfinanceapi.user.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolesRepository extends JpaRepository<Roles,Long> {

    Roles findByName(Roles.UserRole name);
    boolean existsByName(Roles.UserRole name);

}
