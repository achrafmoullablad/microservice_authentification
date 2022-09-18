package com.tchat.ms_authentification.service.facade;

import com.tchat.ms_authentification.bean.User;
import com.tchat.ms_authentification.dto.UserSigninDTO;
import com.tchat.ms_authentification.dto.UserSignupDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    ResponseEntity<Object> signIn(UserSigninDTO user);
    ResponseEntity<String> signUp(UserSignupDTO user);

    ResponseEntity<String> confirmToken(String token);
    User findByUsername(String username);

    List<User> findAll();

    List<User> findUsersByIdIn(List<Long> ids);

    User findUserById(Long id);

    ResponseEntity<String> lockUser(Long userId);
}
