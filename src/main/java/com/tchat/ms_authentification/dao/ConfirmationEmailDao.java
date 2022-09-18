package com.tchat.ms_authentification.dao;

import com.tchat.ms_authentification.bean.ConfirmationEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface ConfirmationEmailDao extends JpaRepository<ConfirmationEmail, Long> {

    ConfirmationEmail findByToken(String token);

    @Modifying
    @Transactional
    @Query("UPDATE ConfirmationEmail c set c.confirmedAt = ?2 where c.token = ?1")
    void setConfirmed(String token, LocalDateTime confirmedAt);
}
