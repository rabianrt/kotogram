package com.rabinart.service;

import com.rabinart.UserDao;
import com.rabinart.entity.User;

public class UserService {
    private static final UserService INSTANCE = new UserService();
    private final UserDao userDao = UserDao.getInstance();

    private UserService(){
    }

    public Long create (User user){
        var id = userDao.save(user).getId();
        return id;
    }


    public static UserService getInstance(){
        return INSTANCE;
    }
}
