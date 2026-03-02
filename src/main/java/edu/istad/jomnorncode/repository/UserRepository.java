package edu.istad.jomnorncode.repository;

import edu.istad.jomnorncode.dto.UserResponse;
import edu.istad.jomnorncode.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);

     Page<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrFirstNameContainingIgnoreCase(String searchTerm, String searchTerm1, String searchTerm2, Pageable pageable);

    Page<User> findByRolesName(String roleName, Pageable pageable);
}
