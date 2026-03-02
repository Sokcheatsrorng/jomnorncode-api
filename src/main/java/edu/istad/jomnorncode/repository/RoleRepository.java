package edu.istad.jomnorncode.repository;

import edu.istad.jomnorncode.entity.Role;
import edu.istad.jomnorncode.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

   Optional<Role> findByRoleName(String roleName);
}
