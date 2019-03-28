package com.secondkill.dao;

import com.secondkill.dataobject.UserPassword;
import com.secondkill.dataobject.UserPasswordExample;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPasswordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserPassword record);

    int insertSelective(UserPassword record);

    List<UserPassword> selectByExample(UserPasswordExample example);

    UserPassword selectByPrimaryKey(Integer id);

    UserPassword selectByUserId(Integer userId);

    int updateByPrimaryKeySelective(UserPassword record);

    int updateByPrimaryKey(UserPassword record);
}