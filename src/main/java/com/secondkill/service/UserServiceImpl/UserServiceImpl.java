package com.secondkill.service.UserServiceImpl;

import com.secondkill.dao.UserMapper;
import com.secondkill.dao.UserPasswordMapper;
import com.secondkill.dataobject.User;
import com.secondkill.dataobject.UserPassword;
import com.secondkill.error.BusinessException;
import com.secondkill.error.EmBusinessError;
import com.secondkill.service.UserService;
import com.secondkill.service.model.UserModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

/**
 * 用户信息Service
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserPasswordMapper userPasswordMapper;

    /**
     * 根据id获取用户信息
     * @param id
     * @return
     */
    @Override
    public UserModel getUserById(Integer id) {
        User user = userMapper.selectByPrimaryKey(id);
        if(user == null){
            return null;
        }
        //通过用户id获取对应的用户加密密码信息
        UserPassword userPassword = userPasswordMapper.selectByUserId(user.getId());

        //返回组合对象
        return convertFromDataObject(user,userPassword);
    }

    /**
     * 组合对象
     * @param user
     * @param userPassword
     * @return
     */
    private UserModel convertFromDataObject(User user, UserPassword userPassword){
        if(user == null) return null;

        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(user,userModel);

        if(userPassword != null){
            userModel.setEncrptPassword(userPassword.getEncrptPassword());
        }

        return userModel;
    }

    @Override
    public void register(UserModel userModel) throws BusinessException{
        if(userModel == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        if(StringUtils.isEmpty(userModel.getName()) || userModel.getGender() == null || userModel.getAge() == null || StringUtils.isEmpty(userModel.getTelphone())){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR.setErrMsg("未知参数错误"));
        }
        //实现model -> dataObject方法
        User user = convertFromModel(userModel);
        try{
            userMapper.insertSelective(user);
        }catch (DuplicateKeyException ex){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"手机号已被注册");
        }

        userModel.setId(user.getId());

        UserPassword userPassword = convertPasswordFromModel(userModel);
        userPasswordMapper.insertSelective(userPassword);
    }

    private User convertFromModel(UserModel userModel){
        if(userModel == null) return null;

        User user = new User();
        BeanUtils.copyProperties(userModel,user);

        return user;
    }

    private UserPassword convertPasswordFromModel(UserModel userModel){
        if(userModel == null) return null;

        UserPassword userPassword = new UserPassword();
        userPassword.setEncrptPassword(userModel.getEncrptPassword());
        userPassword.setUserId(userModel.getId());

        return userPassword;
    }
}
