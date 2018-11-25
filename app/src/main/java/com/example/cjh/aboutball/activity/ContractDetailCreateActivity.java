package com.example.cjh.aboutball.activity;

import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cjh.aboutball.R;
import com.example.cjh.aboutball.db.Contract;

import com.example.cjh.aboutball.db.UserEnterContract;
import com.example.cjh.aboutball.recyclerview.ContractEnteredUserCreateAdapter;
import com.githang.statusbar.StatusBarCompat;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

public class ContractDetailCreateActivity extends AppCompatActivity {

    private TextView titleText;
    private TextView detailGroupName;
    private TextView detailType;
    private TextView detailTime;
    private TextView detailAddress;
    private TextView detailPnow;
    private TextView detailPnum;
    private TextView detailIntro;
    private RecyclerView recyclerView;

    private List<UserEnterContract> userEnteredList = new ArrayList<>();
    private String nowContractId;
    private String nowUserId;
    private String nowContractStatus;

    public String getNowContractId() {
        return nowContractId;
    }

    public void setNowContractId(String nowContractId) {
        this.nowContractId = nowContractId;
    }

    public String getNowUserId() {
        return nowUserId;
    }

    public void setNowUserId(String nowUserId) {
        this.nowUserId = nowUserId;
    }

    public String getNowContractStatus() {
        return nowContractStatus;
    }

    public void setNowContractStatus(String nowContractStatus) {
        this.nowContractStatus = nowContractStatus;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_detail_create);
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.orange));
        titleText = (TextView) findViewById(R.id.title_text);
        titleText.setText("约单详情");
        detailGroupName = (TextView) findViewById(R.id.detail_create_group_name);
        detailType = (TextView) findViewById(R.id.detail_create_type);
        detailTime = (TextView) findViewById(R.id.detail_create_time);
        detailAddress = (TextView) findViewById(R.id.detail_create_address);
        detailPnow = (TextView) findViewById(R.id.detail_create_pnow);
        detailPnum = (TextView) findViewById(R.id.detail_create_pnum);
        detailIntro = (TextView) findViewById(R.id.detail_create_introduce);

        /*获取该约单ID以及当前用户ID*/
        Intent intent = getIntent();
        setNowContractId(intent.getStringExtra("contract_id"));
        setNowUserId(intent.getStringExtra("user_id"));
        setNowContractStatus(intent.getStringExtra("contract_status"));
        /*显示约单信息*/
        QueryContract();
        //显示参与用户
        BindEnteredUser();
    }

    private void QueryContract(){
        BmobQuery<Contract> query = new BmobQuery<Contract>();
        query.getObject(getNowContractId(), new QueryListener<Contract>() {
            @Override
            public void done(Contract contract, BmobException e) {
                if(e == null){
                    //显示约单信息
                    detailGroupName.setText(contract.getGroupName());
                    detailType.setText(contract.getBallType());
                    detailTime.setText(contract.getBallTime());
                    detailAddress.setText(contract.getBallAddress());
                    detailPnow.setText(String.valueOf(contract.getNowPnum()));
                    detailPnum.setText(String.valueOf(contract.getMaxPnum()));
                    detailIntro.setText(contract.getBallRemark());
                }else{
                    Toast.makeText(ContractDetailCreateActivity.this, "查询1失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void BindEnteredUser(){
    /*将该约单放入userEnteredList，寻找参与用户，设置参与用户的RecyclerView*/

        BmobQuery<UserEnterContract> query = new BmobQuery<UserEnterContract>();
        query.addWhereEqualTo("contractId", getNowContractId());
        query.findObjects(new FindListener<UserEnterContract>() {
            @Override
            public void done(List<UserEnterContract> list, BmobException e) {
                if(e == null){
                    userEnteredList = list;
                    recyclerView = (RecyclerView) findViewById(R.id.user_recycler_view_create);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(ContractDetailCreateActivity.this);
                    layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    recyclerView.setLayoutManager(layoutManager);
                    ContractEnteredUserCreateAdapter adapter = new ContractEnteredUserCreateAdapter(userEnteredList);
                    recyclerView.setAdapter(adapter);
                }else{
                    Toast.makeText(ContractDetailCreateActivity.this, "查询2失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
