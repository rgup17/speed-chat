package com.speedchat.server.repositiories;

import com.speedchat.server.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
    User findUserByEmail(@Param("email") String email);

    @Transactional
    @Modifying
    @Query(value = "UPDATE users SET username = :newUsername WHERE email = :email", nativeQuery = true)
    int updateUsernameByEmail(@Param("email") String email, @Param("newUsername") String newUsername);
}
