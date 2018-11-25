package com.example.cjh.aboutball.db;

import org.litepal.crud.DataSupport;

import cn.bmob.v3.BmobObject;

/** Created by cjh on 2018/8/7.
 *
 */

public class UserEnterContract extends BmobObject {

    private String contractId;

    private String userId;

    private Integer userStatus;

    public UserEnterContract(){

    }
    public UserEnterContract(String contractId, String userId){
        this.contractId = contractId;
        this.userId = userId;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(Integer userStatus) {
        this.userStatus = userStatus;
    }
}
