package com.tchat.ms_authentification.mapper;

import com.tchat.ms_authentification.bean.User;
import com.tchat.ms_authentification.dto.UserSignupDTO;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper{

    @Override
    public User fromSignupUser(UserSignupDTO signupDTO) {
        User user = new User();
        user.setFullname(signupDTO.getFullname());
        user.setUsername(signupDTO.getUsername());
        user.setEmail(signupDTO.getEmail());
        user.setProfession(signupDTO.getProfession());
        user.setGender(signupDTO.getGender());
        user.setPassword(signupDTO.getPassword());
        return user;
    }

}
