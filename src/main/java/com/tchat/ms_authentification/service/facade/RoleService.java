package com.tchat.ms_authentification.service.facade;

import com.tchat.ms_authentification.bean.Role;

public interface RoleService {
    Role findByName(String name);

}
