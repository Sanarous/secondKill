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
     * 用户注册接口
     * @param telphone
     * @param optCode
     * @param name
     * @param gender
     * @param age
     * @param password
     * @return
     * @throws BusinessException
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
     * @param str
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
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
     * @param telphone
     * @return
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
     * @param id
     * @return
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
     * @param userModel
     * @return
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
