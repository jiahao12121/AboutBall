package com.example.cjh.aboutball.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.widget.TextView;

import com.example.cjh.aboutball.R;
import com.example.cjh.aboutball.db.Contract;
import com.example.cjh.aboutball.db.User;
import com.example.cjh.aboutball.fragment.FragmentMain;
import com.example.cjh.aboutball.fragment.FragmentUser;
import com.example.cjh.aboutball.fragment.FragmentYue;
import com.example.cjh.aboutball.recyclerview.ContractMainAdapter;
import com.example.cjh.aboutball.util.BottomBar;
import com.example.cjh.aboutball.util.SpaceItemDecoration;
import com.githang.statusbar.StatusBarCompat;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cjh on 2018/7/28.
 * 主界面（约单广场）
 */

public class MainActivity extends AppCompatActivity {

    private List<Contract> contractList = new ArrayList<>();

    private BottomBar bottomBar;

    private TextView userName;

    private TextView userSex;

    private TextView userAge;

    private TextView userIntro;

    private String nowUserId;      //保存登录界面传来的用户ID

    public String getNowUserId() {
        return nowUserId;
    }

    public void setNowUserId(String nowUserId) {
        this.nowUserId = nowUserId;
    }

    public static String flag = "main";     //记录后续由哪个界面返回（更新数据）

    public static String getFlag() {
        return flag;
    }

    public static void setFlag(String flag) {
        MainActivity.flag = flag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.orange));
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }
        Intent intent = getIntent();
        setNowUserId(intent.getStringExtra("user_id"));
        Log.d("MainActivity",intent.getStringExtra("user_id"));
        bottomBar = (BottomBar) findViewById(R.id.bottom_bar);
        bottomBar.setContainer(R.id.fl_container)
                .setTitleBeforeAndAfterColor("#999999", "#FF9900")
                .addItem(FragmentMain.class,
                        "首页",
                        R.drawable.item1_before,
                        R.drawable.item1_after)
                .addItem(FragmentYue.class,
                        "订单",
                        R.drawable.item2_before,
                        R.drawable.item2_after)
                .addItem(FragmentUser.class,
                        "我的",
                        R.drawable.item3_before,
                        R.drawable.item3_after)
                .build();
    }
    /*返回主页时更新各项参数*/
    @Override
    protected void onRestart() {
        super.onRestart();
        if(getFlag().equals("user")){
            /*个人中心各子项返回时更新数据*/
            List<User> result = DataSupport.where("id = ?", getNowUserId()).find(User.class);
            userName = (TextView) findViewById(R.id.user_person_name);
            userSex = (TextView) findViewById(R.id.user_sex);
            userAge = (TextView) findViewById(R.id.user_age);
            userIntro = (TextView) findViewById(R.id.user_intro);
            userName.setText(result.get(0).getUserName());
            userSex.setText(result.get(0).getUserSex());
            userAge.setText(result.get(0).getUserAge() + "");
            userIntro.setText(result.get(0).getUserIntro());
            setFlag("main");
        }
        if(getFlag().equals("create")){
             /*创建约单返回时更新数据*/
            contractList = DataSupport.findAll(Contract.class);
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.contract_recycler_view);
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            ContractMainAdapter adapter = new ContractMainAdapter(contractList);
            recyclerView.setAdapter(adapter);
            setFlag("main");
        }
    }

}
