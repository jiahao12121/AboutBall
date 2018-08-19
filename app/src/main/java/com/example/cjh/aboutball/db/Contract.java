package com.example.cjh.aboutball.db;

import org.litepal.crud.DataSupport;

/**
 * Created by cjh on 2018/8/2.
 */

public class Contract extends DataSupport {

    private int id;

    private int userId;

    private String groupName;

    private String ballTime;

    private String ballType;

    private String ballAddress;

    private String ballRemark;

    private int nowPum;

    private int maxPum;

    private int status; //默认为0(未完成)

    public Contract(){
    }
    public Contract(int userId, String groupName, String ballTime, String ballType, String ballAddress, String ballRemark, int nowPum, int maxPum){
        this.userId = userId;
        this.groupName = groupName;
        this.ballTime = ballTime;
        this.ballType = ballType;
        this.ballAddress = ballAddress;
        this.ballRemark = ballRemark;
        this.nowPum = nowPum;
        this.maxPum = maxPum;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMaxPum() {
        return maxPum;
    }

    public void setMaxPum(int maxPum) {
        this.maxPum = maxPum;
    }

    public int getNowPum() {
        return nowPum;
    }

    public void setNowPum(int nowPum) {
        this.nowPum = nowPum;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
