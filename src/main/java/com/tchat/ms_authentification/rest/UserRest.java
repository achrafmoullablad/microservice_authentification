package com.tchat.ms_authentification.rest;

import com.tchat.ms_authentification.bean.User;
import com.tchat.ms_authentification.dto.UserSigninDTO;
import com.tchat.ms_authentification.dto.UserSignupDTO;
import com.tchat.ms_authentification.service.impl.UserServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/authentication")
@AllArgsConstructor
public class UserRest {

    private UserServiceImpl userService;

    @PostMapping( "/sign-in")
    public ResponseEntity<Object> signIn(@RequestBody @Valid UserSigninDTO user) {
        return userService.signIn(user);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@Valid @RequestBody UserSignupDTO user) {
        return userService.signUp(user);
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirmToken(@RequestParam("token") String token) {
        return userService.confirmToken(token);
    }

    @GetMapping("/user/username/{username}")
    public User findUserByUsername(@PathVariable String username) {
        return userService.findByUsername(username);
    }

    @GetMapping("/users")
    public List<User> findAll() {
        return userService.findAll();
    }

    @PostMapping("/users")
    public List<User> findUsersByIdIn(@RequestBody List<Long> usersId) {
        return userService.findUsersByIdIn(usersId);
    }

    @GetMapping("/user/id/{userId}")
    public User findUserById(@PathVariable Long userId) {
        return userService.findUserById(userId);
    }

    @PutMapping("/user/lock/{userId}")
    public ResponseEntity<String> lockUser(@PathVariable Long userId) {
        return userService.lockUser(userId);
    }
}
