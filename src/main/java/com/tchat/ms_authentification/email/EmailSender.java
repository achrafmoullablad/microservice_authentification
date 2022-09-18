package com.tchat.ms_authentification.email;

import org.springframework.scheduling.annotation.Async;

public interface EmailSender {

    @Async
    void send(String to, String emailContent);
}
