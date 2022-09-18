package com.tchat.ms_authentification.service.impl;

import com.tchat.ms_authentification.bean.ConfirmationEmail;
import com.tchat.ms_authentification.dao.ConfirmationEmailDao;
import com.tchat.ms_authentification.service.facade.ConfirmationEmailService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class ConfirmationEmailServiceImpl implements ConfirmationEmailService {

    private ConfirmationEmailDao emailDao;

    public void save(ConfirmationEmail confirmationEmail){
        emailDao.save(confirmationEmail);
    }

    @Override
    public ConfirmationEmail findByToken(String token) {
        return emailDao.findByToken(token);
    }


    @Override
    public void setConfirmed(String token, LocalDateTime confirmedAt){
        emailDao.setConfirmed(token, confirmedAt);
    }
}
