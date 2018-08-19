package com.example.cjh.aboutball.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.cjh.aboutball.R;
import com.example.cjh.aboutball.db.Contract;
import com.example.cjh.aboutball.db.User;
import com.example.cjh.aboutball.db.UserEnterContract;
import com.example.cjh.aboutball.recyclerview.ContractPersonalAdapter;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class InforPersonalActivity extends AppCompatActivity {

    private List<Contract> contractList = new ArrayList<>();

    private TextView titleText;

    private TextView personalName;

    private TextView personalSex;

    private TextView personalAge;

    private CircleImageView personalHeadIcon;

    private TextView personalIntro;

    private RecyclerView recyclerView;

    private ContractPersonalAdapter adapter;

    private String nowUserId;       //当前用户的ID

    private String personalUserId;      //当前查看的资料对应用户的ID

    public String getPersonalUserId() {
        return personalUserId;
    }

    public void setPersonalUserId(String personalUserId) {
        this.personalUserId = personalUserId;
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
        setContentView(R.layout.activity_infor_personal);
        titleText = (TextView) findViewById(R.id.title_text);
        personalName = (TextView) findViewById(R.id.personal_name);
        personalSex = (TextView) findViewById(R.id.personal_sex);
        personalAge = (TextView) findViewById(R.id.personal_age);
        personalHeadIcon = (CircleImageView) findViewById(R.id.personal_head_icon);
        personalIntro = (TextView) findViewById(R.id.personal_intro);
        recyclerView = (RecyclerView) findViewById(R.id.personal_recycler_view);
        titleText.setText("他/她的个人资料");
        Intent intent = getIntent();
        setPersonalUserId(intent.getStringExtra("personal_id"));
        setNowUserId(intent.getStringExtra("user_id"));
        List<User> result = DataSupport.where("id = ?", getPersonalUserId()).find(User.class);
        personalName.setText(result.get(0).getUserName());
        personalSex.setText(result.get(0).getUserSex());
        int age = result.get(0).getUserAge();
        if(age == 0){
            personalAge.setText("");
        }else{
            personalAge.setText(age + "");
        }
        personalHeadIcon.setImageResource(result.get(0).getHeadIcon());
        personalIntro.setText(result.get(0).getUserIntro());
        contractList = DataSupport.where("userId = ?", getPersonalUserId()).find(Contract.class);
        List<UserEnterContract> result1 = DataSupport.where("userId = ?", getPersonalUserId()).find(UserEnterContract.class);
        for(int i = 0; i < result1.size(); i++){
            Contract contract = DataSupport.where("id = ?", result1.get(i).getContractId() + "").find(Contract.class).get(0);
            contractList.add(contract);
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ContractPersonalAdapter(contractList);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }
}

