package com.example.cjh.aboutball.db;

import org.litepal.crud.DataSupport;

import cn.bmob.v3.BmobObject;

/**
 * Created by cjh on 2018/8/2.
 */

public class Contract extends BmobObject {

    private String userId;

    private String groupName;

    private String ballTime;

    private String ballType;

    private String ballAddress;

    private String ballRemark;

    private Integer nowPnum;

    private Integer maxPnum;

    private Integer status; //默认为0(未完成)

    public Contract(){
    }
    public Contract(String userId, String groupName, String ballTime, String ballType, String ballAddress, String ballRemark, Integer nowPnum, Integer maxPnum){
        this.userId = userId;
        this.groupName = groupName;
        this.ballTime = ballTime;
        this.ballType = ballType;
        this.ballAddress = ballAddress;
        this.ballRemark = ballRemark;
        this.nowPnum = nowPnum;
        this.maxPnum = maxPnum;
    }

    public String getBallAddress() {
        return ballAddress;
    }

    public void setBallAddress(String ballAddress) {
        this.ballAddress = ballAddress;
    }

    public String getBallRemark() {
        return ballRemark;
    }

    public void setBallRemark(String ballRemark) {
        this.ballRemark = ballRemark;
    }

    public String getBallTime() {
        return ballTime;
    }

    public void setBallTime(String ballTime) {
        this.ballTime = ballTime;
    }

    public String getBallType() {
        return ballType;
    }

    public void setBallType(String ballType) {
        this.ballType = ballType;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getMaxPnum() {
        return maxPnum;
    }

    public void setMaxPnum(Integer maxPnum) {
        this.maxPnum = maxPnum;
    }

    public Integer getNowPnum() {
        return nowPnum;
    }

    public void setNowPnum(Integer nowPnum) {
        this.nowPnum = nowPnum;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
