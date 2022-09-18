package com.tchat.ms_authentification.dao;

import com.tchat.ms_authentification.bean.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserDao extends JpaRepository<User, Long> {

    User findByUsername(String username);
    User findByEmail(String email);

    User findByUsernameOrEmail(String username, String email);

    List<User> findAll();

    @Modifying
    @Transactional
    @Query("UPDATE User u set u.isLocked = ?2 where u.email = ?1")
    void updateLocking(String email, boolean lockedOrNot);

    List<User> findUsersByIdInAndIsLockedFalse(List<Long> ids);
}
