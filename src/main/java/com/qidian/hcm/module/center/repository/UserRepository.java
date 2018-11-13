package com.qidian.hcm.module.center.repository;

import com.qidian.hcm.module.center.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    Optional<User> findByPhone(String phone);

    User findByPhoneOrEmail(String phone, String email);

    void deleteAllByGroupId(Long groupId);
}
