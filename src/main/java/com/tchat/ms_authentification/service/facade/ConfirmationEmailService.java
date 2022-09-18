package com.tchat.ms_authentification.service.facade;

import com.tchat.ms_authentification.bean.ConfirmationEmail;

import java.time.LocalDateTime;

public interface ConfirmationEmailService {

    void save(ConfirmationEmail confirmationEmail);

    ConfirmationEmail findByToken(String token);

    void setConfirmed(String token, LocalDateTime confirmedAt);
}
