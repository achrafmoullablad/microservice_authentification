package com.tchat.ms_authentification.mapper;

import com.tchat.ms_authentification.bean.User;
import com.tchat.ms_authentification.dto.UserSignupDTO;

public interface UserMapper {
    User fromSignupUser(UserSignupDTO signupDTO);
}
