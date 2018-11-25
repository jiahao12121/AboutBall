package com.example.cjh.aboutball.db;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by cjh on 2018/8/19.
 */

public class Test extends BmobObject {

    private String userName;

    private String userPwd;

    private BmobFile headIcon;

    public BmobFile getHeadIcon() {
        return headIcon;
    }

    public void setHeadIcon(BmobFile headIcon) {
        this.headIcon = headIcon;
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
}
