package com.secondkill.controller;

import com.alibaba.druid.util.StringUtils;
import com.secondkill.controller.viewObject.UserVO;
import com.secondkill.error.BusinessException;
import com.secondkill.error.EmBusinessError;
import com.secondkill.response.CommonReturnType;
import com.secondkill.service.UserService;
import com.secondkill.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;


@RestController
@RequestMapping("/user")
@CrossOrigin(allowedHeaders = "*",allowCredentials = "true")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    //内部使用ThreadLocal，每个请求对应自己的线程，所以并不是单例
    @Autowired
    private HttpServletRequest httpServletRequest;

    /**
     * 用户登陆接口
     * @param telphone
     * @param password
     * @return
     */
    @PostMapping(value = "/login",consumes = {CONTEXT_TYPE_FORMED})
    public CommonReturnType login(@RequestParam("telphone") String telphone,
                                  @RequestParam("encrptPassword") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //入参校验
        if(org.apache.commons.lang3.StringUtils.isEmpty(telphone) || org.apache.commons.lang3.StringUtils.isEmpty(password)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        //用户登陆服务，用来校验用户登陆是否合法
        UserModel userModel = userService.validateLogin(telphone, this.encodeByMD5(password));

        //将登陆凭证加入到用户登陆成功的session内
        this.httpServletRequest.getSession().setAttribute("IS_LOGIN",true);
        this.httpServletRequest.getSession().setAttribute("LOGIN_USER",userModel);

        return CommonReturnType.create(null);
    }

    /**
     * 用户注册接口
     * @param telphone  手机号
     * @param optCode  验证码
     * @param name  用户昵称
     * @param gender  性别
     * @param age  年龄
     * @param password  密码
     * @return 通用返回类型
     * @throws BusinessException  自定义异常
     */
    @PostMapping(value = "/register",consumes = {CONTEXT_TYPE_FORMED})
    public CommonReturnType register(@RequestParam(name = "telphone") String telphone,
                                     @RequestParam("optCode") String optCode,
                                     @RequestParam("name") String name,
                                     @RequestParam("gender")Byte gender,
                                     @RequestParam("age") Integer age,
                                     @RequestParam("encrptPassword") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //验证手机号和optCode符合
        String sessionOptCode = (String) this.httpServletRequest.getSession().getAttribute(telphone);
        if(!StringUtils.equals(optCode,sessionOptCode)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"短信验证码错误");
        }
        //用户的注册流程
        UserModel userModel = new UserModel();
        userModel.setId(null);
        userModel.setName(name);
        userModel.setGender(gender);
        userModel.setAge(age);
        userModel.setTelphone(telphone);
        userModel.setRegisterMode("byphone");
        userModel.setEncrptPassword(this.encodeByMD5(password));
        userService.register(userModel);
        return CommonReturnType.create(null);
    }

    /**
     * MD5加密
     * @param str  任一字符串
     * @return  字符串
     * @throws NoSuchAlgorithmException  算术异常
     * @throws UnsupportedEncodingException  不支持编码异常
     */
    public String encodeByMD5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        //加密字符串
        String newstr = base64Encoder.encode(md5.digest(str.getBytes("UTF-8")));
        return newstr;
    }

    /**
     * 用户获取otp短信接口
     * @param telphone  手机号
     * @return  返回
     */
    @PostMapping(value = "/getotp",consumes = {CONTEXT_TYPE_FORMED})
    public CommonReturnType getOtp(@RequestParam(name = "telphone") String telphone){
        //需要按照一定的规则生成otp验证码
        Random random = new Random();
        int randomInt = random.nextInt(99999);
        randomInt += 10000;
        String optCode = String.valueOf(randomInt);

        //将otp验证码同对应用户的手机号关联,使用HttpSession的方式绑定他的手机号和验证码。实际开发中应该使用redis
        httpServletRequest.getSession().setAttribute(telphone,optCode);

        //将otp验证码通过短信通道发送给用户，省略
        System.out.println("telphone =" + telphone + "&optCode=" + optCode);

        return CommonReturnType.create(null);
    }

    /**
     * 根据用户id获取用户信息
     * @param id 用户id
     * @return  通用返回类型
     */
    @GetMapping("/get")
    public CommonReturnType getUser(@RequestParam(name = "id") Integer id){
        //调用service服务获取对应的id的用户对象并返回给前端
        UserModel userModel = userService.getUserById(id);

        //若获取的用户信息不存在
        if(userModel == null){
            userModel.setEncrptPassword("123");
        }

        //转换成UI使用的用户数据信息
        UserVO userVO = convertFromModel(userModel);

        //返回通用对象
        return CommonReturnType.create(userVO);
    }

    /**
     * 将User信息的领域模型转换成UserVO给前端用户
     * @param userModel  用户模型
     * @return 返回给UI的字段
     */
    private UserVO convertFromModel(UserModel userModel){
        if(userModel == null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel,userVO);
        return userVO;
    }
}
