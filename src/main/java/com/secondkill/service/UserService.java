package com.secondkill.service;

import com.secondkill.error.BusinessException;
import com.secondkill.service.model.UserModel;

public interface UserService {
    UserModel getUserById(Integer id);

    void register(UserModel userModel) throws BusinessException;

    UserModel validateLogin(String telphone,String password) throws BusinessException;
}
