package com.example.cjh.aboutball.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cjh.aboutball.R;
import com.example.cjh.aboutball.db.Contract;
import com.example.cjh.aboutball.db.User;
import com.example.cjh.aboutball.db.UserEnterContract;
import com.example.cjh.aboutball.recyclerview.ContractPersonalAdapter;
import com.example.cjh.aboutball.util.ImageLoaderApplication;
import com.githang.statusbar.StatusBarCompat;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import de.hdodenhof.circleimageview.CircleImageView;


public class InforPersonalActivity extends AppCompatActivity {

    private List<Contract> contractList = new ArrayList<>();

    private TextView titleText;
    private TextView personalName;
    private TextView personalSex;
    private TextView personalAge;
    private TextView personalHobby;
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
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.orange));
        titleText = (TextView) findViewById(R.id.title_text);
        personalName = (TextView) findViewById(R.id.personal_name);
        personalSex = (TextView) findViewById(R.id.personal_sex);
        personalAge = (TextView) findViewById(R.id.personal_age);
        personalHobby = (TextView) findViewById(R.id.personal_hobby);
        personalHeadIcon = (CircleImageView) findViewById(R.id.personal_head_icon);
        personalIntro = (TextView) findViewById(R.id.personal_intro);
        recyclerView = (RecyclerView) findViewById(R.id.personal_recycler_view);
        titleText.setText("他/她的个人资料");
        //获取当前用户ID和该用户查看的用户的ID
        Intent intent = getIntent();
        setPersonalUserId(intent.getStringExtra("personal_id"));
        setNowUserId(intent.getStringExtra("user_id"));
        QueryUser();    //显示该用户信息
        QueryContract();       //显示他的约单

    }

    private void QueryUser(){
        BmobQuery<User> query = new BmobQuery<User>();
        query.getObject(getPersonalUserId(), new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if(e == null){
                    personalName.setText(user.getUserName());
                    personalSex.setText(user.getUserSex());
                    personalIntro.setText(user.getUserIntro());
                    if(user.getUserHobby() != null){
                        personalHobby.setText(user.getUserHobby());
                    }
                    if(user.getUserAge() == 0){
                        personalAge.setText("");
                    }else{
                        String age = user.getUserAge() + "岁";
                        personalAge.setText(age);
                    }
                    if(user.getHeadIcon() != null){
                        ImageLoader imageLoader = ImageLoader.getInstance();
                        imageLoader.displayImage(user.getHeadIcon().getFileUrl(), personalHeadIcon, ImageLoaderApplication.options);
                    }
                }else {
                    Toast.makeText(InforPersonalActivity.this, "查询1失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void QueryContract(){       //三层嵌套（需要可用Message消除一层）
        //先获取该用户所创建的所有约单
        BmobQuery<Contract> query = new BmobQuery<Contract>();
        query.addWhereEqualTo("userId", getPersonalUserId());
        query.findObjects(new FindListener<Contract>() {
            @Override
            public void done(List<Contract> list, BmobException e) {
                if(e == null){
                    contractList = list;
                    //再获取该用户参与的所有约单（分两步）
                    BmobQuery<UserEnterContract> query1 = new BmobQuery<UserEnterContract>();
                    query1.addWhereEqualTo("userId", getPersonalUserId());
                    query1.findObjects(new FindListener<UserEnterContract>() {
                        @Override
                        public void done(List<UserEnterContract> list, BmobException e) {
                            if(e == null){
                                //若用户未参与任何约单则直接绑定recyclerview
                                if(list.size() == 0){
                                    bindPersonalContract();
                                }else{
                                    for(int i = 0; i < list.size(); i++){
                                        //否则将该用户参与的每一单添加到contractList中
                                        BmobQuery<Contract> query2 = new BmobQuery<Contract>();
                                        query2.getObject(list.get(i).getContractId(), new QueryListener<Contract>() {
                                            @Override
                                            public void done(Contract contract, BmobException e) {
                                                if(e == null){
                                                    contractList.add(contract);
                                                    bindPersonalContract();
                                                }else{
                                                    Toast.makeText(InforPersonalActivity.this, "查询2失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }

                            }else{
                                Toast.makeText(InforPersonalActivity.this, "查询3失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(InforPersonalActivity.this, "查询4失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void bindPersonalContract(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(InforPersonalActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ContractPersonalAdapter(contractList);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(InforPersonalActivity.this,
                DividerItemDecoration.VERTICAL));
    }
}

