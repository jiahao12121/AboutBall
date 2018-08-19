package com.example.cjh.aboutball.db;

import org.litepal.crud.DataSupport;

/** Created by cjh on 2018/8/7.
 *
 */

public class UserEnterContract extends DataSupport {

    private int id;

    private int contractId;

    private int userId;

    public UserEnterContract(){

    }
    public UserEnterContract(int contractId, int userId){
        this.contractId = contractId;
        this.userId = userId;
    }

    public int getContractId() {
        return contractId;
    }

    public void setContractId(int contractId) {
        this.contractId = contractId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
