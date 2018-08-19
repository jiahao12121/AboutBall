package com.example.cjh.aboutball.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cjh.aboutball.R;
import com.example.cjh.aboutball.db.Contract;
import com.example.cjh.aboutball.db.User;
import com.example.cjh.aboutball.db.UserEnterContract;
import com.example.cjh.aboutball.recyclerview.ContractEnteredUserAdapter;
import com.githang.statusbar.StatusBarCompat;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContractDetailActivity extends AppCompatActivity {

    private List<UserEnterContract> userEnteredList = new ArrayList<>();

    private TextView titleText;

    private CircleImageView detailHeadIcon;

    private TextView detailGroupName;

    private TextView detailType;

    private TextView detailTime;

    private TextView detailAddress;

    private TextView detailPnow;

    private TextView detailPnum;

    private TextView detailName;

    private TextView detailSex;

    private TextView detailAge;

    private TextView detailIntro;

    private Button detailEnter;

    private String nowContractId;

    private String nowUserId;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_detail);
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.orange));
        titleText = (TextView) findViewById(R.id.title_text);
        titleText.setText("约单详情");
        detailHeadIcon = (CircleImageView) findViewById(R.id.detail_head_icon);
        detailGroupName = (TextView) findViewById(R.id.detail_group_name);
        detailType = (TextView) findViewById(R.id.detail_type);
        detailTime = (TextView) findViewById(R.id.detail_time);
        detailAddress = (TextView) findViewById(R.id.detail_address);
        detailPnow = (TextView) findViewById(R.id.detail_pnow);
        detailPnum = (TextView) findViewById(R.id.detail_pnum);
        detailName = (TextView) findViewById(R.id.detail_name);
        detailSex = (TextView) findViewById(R.id.detail_sex);
        detailAge = (TextView) findViewById(R.id.detail_age);
        detailIntro = (TextView) findViewById(R.id.detail_introduce);
        detailEnter = (Button) findViewById(R.id.detail_enter);

/*        nowNum = (TextView) findViewById(R.id.contract_ball_pnow);
        xieGang = (TextView) findViewById(R.id.xie_gang);
        maxNum = (TextView) findViewById(R.id.contract_ball_pnum);*/
        /*获取该约单ID*/
        Intent intent = getIntent();
        setNowContractId(intent.getStringExtra("contract_id"));
        setNowUserId(intent.getStringExtra("user_id"));
        Log.d("ContractDetailActivity", getNowUserId());
        Log.d("ContractDetailActivity", getNowContractId());
        /*设置头像和用户名*/
        List<Contract> result = DataSupport.where("id = ?", getNowContractId()).find(Contract.class);
        final List<User> result1 = DataSupport.where("id = ?", result.get(0).getUserId()+"").find(User.class);
        int headicon = result1.get(0).getHeadIcon();
        String name = result1.get(0).getUserName();
        String sex = result1.get(0).getUserSex();
        int age = result1.get(0).getUserAge();
        detailHeadIcon.setImageResource(headicon);
        detailGroupName.setText(result.get(0).getGroupName());
        detailType.setText(result.get(0).getBallType());
        detailTime.setText(result.get(0).getBallTime());
        detailHeadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(ContractDetailActivity.this, InforPersonalActivity.class);
                intent1.putExtra("personal_id", result1.get(0).getId() + "");
                intent1.putExtra("user_id", getNowUserId());
                startActivity(intent1);
            }
        });
        detailAddress.setText(result.get(0).getBallAddress());
        detailPnow.setText(result.get(0).getNowPum() + "");
        detailPnum.setText(result.get(0).getMaxPum() + "");
        detailName.setText(name);
        detailSex.setText(sex);
        if(age == 0){
            detailAge.setText("");
        }else{
            detailAge.setText(age + "");
        }

        detailIntro.setText(result.get(0).getBallRemark());
        /*将该约单放入userEnteredList，寻找参与用户，设置参与用户的RecyclerView*/
        userEnteredList = DataSupport.where("contractId = ?", getNowContractId()).find(UserEnterContract.class);
        /*Log.d("ContractDetailActivity", userEnteredList.get(0).getUserId() + "");*/
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.user_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        ContractEnteredUserAdapter adapter = new ContractEnteredUserAdapter(userEnteredList);
        recyclerView.setAdapter(adapter);
        /*参与按钮点击事件*/
        detailEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*判断能否加入该约单*/
                if((DataSupport.where("id = ?", getNowContractId()).find(Contract.class).get(0).getUserId() + "").equals(getNowUserId())){
                    Toast.makeText(ContractDetailActivity.this, "您已加入该约单!", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<UserEnterContract> result = DataSupport.where("contractId = ?", getNowContractId()).find(UserEnterContract.class);
                for(int i = 0; i < result.size(); i++){
                    if((result.get(i).getUserId()+"").equals(getNowUserId())){
                        Toast.makeText(ContractDetailActivity.this, "您已加入该约单!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                final int pnum = DataSupport.where("id = ?", getNowContractId()).find(Contract.class).get(0).getMaxPum();
                final int pnow = DataSupport.where("id = ?", getNowContractId()).find(Contract.class).get(0).getNowPum();
                if(pnow == pnum){
                    Toast.makeText(ContractDetailActivity.this, "该约单人数已满!", Toast.LENGTH_SHORT).show();
                    return;
                }
                AlertDialog.Builder dialog = new AlertDialog.Builder(ContractDetailActivity.this);
                dialog.setTitle("提示");
                dialog.setMessage("您确认要加入该约单吗？");
                dialog.setCancelable(false);
                dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ContractDetailActivity.this, "加入约单成功!", Toast.LENGTH_SHORT).show();
                        /*添加对应记录到用户约单表*/
                        UserEnterContract userEnterContract = new UserEnterContract();
                        userEnterContract.setContractId(Integer.parseInt(getNowContractId()));
                        userEnterContract.setUserId(Integer.parseInt(getNowUserId()));
                        userEnterContract.save();
                        /*修改当前人数*/
                        if(pnow + 1 == pnum){
/*                            detailPnow.setTextColor(getResources().getColor(R.color.red));
                            xieGang2.setTextColor(getResources().getColor(R.color.red));
                            detailPnum.setTextColor(getResources().getColor(R.color.red));*/
/*                            nowNum.setTextColor(getResources().getColor(R.color.red));
                            xieGang.setTextColor(getResources().getColor(R.color.red));
                            maxNum.setTextColor(getResources().getColor(R.color.red));*/
                        }
                        /*当前参与人数加1*/
                        Contract contract = new Contract();
                        contract.setNowPum(pnow + 1);
                        contract.updateAll("id = ?", getNowContractId());
                        MainActivity.setFlag("create");
                        ContractDetailActivity.this.finish();
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

}
