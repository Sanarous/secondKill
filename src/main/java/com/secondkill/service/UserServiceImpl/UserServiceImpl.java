package com.secondkill.service.UserServiceImpl;

import com.secondkill.dao.UserMapper;
import com.secondkill.dao.UserPasswordMapper;
import com.secondkill.dataobject.User;
import com.secondkill.dataobject.UserPassword;
import com.secondkill.error.BusinessException;
import com.secondkill.error.EmBusinessError;
import com.secondkill.service.UserService;
import com.secondkill.service.model.UserModel;
import com.secondkill.validator.ValidationResult;
import com.secondkill.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户信息Service
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserPasswordMapper userPasswordMapper;

    @Autowired
    private ValidatorImpl validator;

    /**
     * 根据id获取用户信息
     * @param id  用户id
     * @return 组合对象模型
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
     * @param user 单表查询的一个User表信息
     * @param userPassword  携带密码的User表
     * @return  组合user对象
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

    /**
     * 注册业务
     * @param userModel 用户信息
     * @throws BusinessException  自定义异常
     */
    @Override
    @Transactional
    public void register(UserModel userModel) throws BusinessException{
        if(userModel == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

//        if(StringUtils.isEmpty(userModel.getName()) || userModel.getGender() == null || userModel.getAge() == null || StringUtils.isEmpty(userModel.getTelphone())){
//            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR.setErrMsg("未知参数错误"));
//        }
        ValidationResult result = validator.validate(userModel);
        if(result.isHasErrors()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,result.getErrMsg());
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

    /**
     * 登陆实现
     * @param telphone  用户注册手机
     * @param encrptPassword  用户加密密码
     */
    @Override
    public UserModel validateLogin(String telphone, String encrptPassword) throws BusinessException {
        //通过用户手机获取用户信息
        User user = userMapper.selectByTelphone(telphone);
        if(user == null){
            throw new BusinessException((EmBusinessError.USER_LOGIN_FAIL));
        }
        UserPassword userPassword = userPasswordMapper.selectByUserId(user.getId());

        UserModel userModel = convertFromDataObject(user,userPassword);
        //对比用户信息内加密的密码是否和用户登陆输入的密码匹配
        if(!com.alibaba.druid.util.StringUtils.equals(encrptPassword,userModel.getEncrptPassword())){
            throw new BusinessException((EmBusinessError.USER_LOGIN_FAIL));
        }
        return userModel;
    }
}
