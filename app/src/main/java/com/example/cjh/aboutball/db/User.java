package com.example.cjh.aboutball.db;

import org.litepal.crud.DataSupport;

/**
 * Created by cjh on 2018/7/27.
 * 用户表
 */

public class User extends DataSupport{

    private int id;

    private int headIcon;

    private String userTel;

    private String userName;

    private String userPwd;

    private String userSex;

    private int userAge;

    private String userIntro;

    public User(){}

    public User(int headIcon, String userTel, String userName, String userPwd){
        this.headIcon = headIcon;
        this.userTel = userTel;
        this.userName = userName;
        this.userPwd = userPwd;
    }

    public String getUserIntro() {
        return userIntro;
    }

    public void setUserIntro(String userIntro) {
        this.userIntro = userIntro;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public int getUserAge() {
        return userAge;
    }

    public void setUserAge(int userAge) {
        this.userAge = userAge;
    }

    public int getHeadIcon() {
        return headIcon;
    }

    public void setHeadIcon(int headIcon) {
        this.headIcon = headIcon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public String getUserTel() {
        return userTel;
    }

    public void setUserTel(String userTel) {
        this.userTel = userTel;
    }
}
