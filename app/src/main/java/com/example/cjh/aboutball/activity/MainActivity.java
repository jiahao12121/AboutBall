package com.example.cjh.aboutball.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cjh.aboutball.R;
import com.example.cjh.aboutball.db.Contract;
import com.example.cjh.aboutball.db.User;
import com.example.cjh.aboutball.db.UserEnterContract;
import com.example.cjh.aboutball.fragment.FragmentMain;
import com.example.cjh.aboutball.fragment.FragmentUser;
import com.example.cjh.aboutball.recyclerview.ContractMainAdapter;
import com.example.cjh.aboutball.util.BottomBar;
import com.example.cjh.aboutball.util.ImageLoaderApplication;
import com.githang.statusbar.StatusBarCompat;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by cjh on 2018/7/28.
 * 主界面（约单广场）
 */

public class MainActivity extends AppCompatActivity {

    private BottomBar bottomBar;

    private List<Contract> contractList = new ArrayList<>();
    private String nowUserId;      //保存登录界面传来的用户ID
    public static String flag = "main";     //记录后续由哪个界面返回（更新数据）
    private static final int QUERY_USER_SUCCESS = 0;
    private static final int QUERY_CONTRACT_SUCCESS = 1;

    public String getNowUserId() {
        return nowUserId;
    }

    public void setNowUserId(String nowUserId) {
        this.nowUserId = nowUserId;
    }
 
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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ImageLoaderApplication.initImageLoader(this);   //初始化ImageLoader
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.orange));    //设置顶部标题栏同色
        ActionBar actionBar = getSupportActionBar();        //隐藏原生顶部标题栏
        if(actionBar != null){
            actionBar.hide();
        }
        initContract();  //初始化订单状态
        Intent intent = getIntent();
        setNowUserId(intent.getStringExtra("user_id"));
        bottomBar = (BottomBar) findViewById(R.id.bottom_bar);
        bottomBar.setContainer(R.id.fl_container)
                .setTitleBeforeAndAfterColor("#999999", "#FF9900")
                .addItem(FragmentMain.class,
                        "首页",
                        R.drawable.item1_before,
                        R.drawable.item1_after)
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
            BmobQuery<User> query = new BmobQuery<User>();
            query.getObject(getNowUserId(), new QueryListener<User>() {
                @Override
                public void done(User user, BmobException e) {
                    if(e == null){
                        Message message = new Message();
                        message.what = QUERY_USER_SUCCESS;
                        message.obj = user;
                        handler.sendMessage(message);
                    }else{
                        Toast.makeText(MainActivity.this, "查询1失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("MainActivity", e.getMessage());
                    }
                }
            });
        }
        if(getFlag().equals("create")){
             /*创建约单返回时更新数据*/
            BmobQuery<Contract> query = new BmobQuery<Contract>();
            query.addWhereEqualTo("status", 0);
            query.findObjects(new FindListener<Contract>() {
                @Override
                public void done(List<Contract> list, BmobException e) {
                    if(e == null){
                        Message message = new Message();
                        message.what = QUERY_CONTRACT_SUCCESS;
                        message.obj = list;
                        handler.sendMessage(message);
                    }else{
                        Toast.makeText(MainActivity.this, "查询2失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("MainActivity", e.getMessage());
                    }
                }
            });
        }
    }

    private void initContract(){
        final BmobQuery<Contract> query = new BmobQuery<Contract>();
        query.findObjects(new FindListener<Contract>() {
            @Override
            public void done(final List<Contract> list, BmobException e) {
                if(e == null){
                    Calendar calendar = Calendar.getInstance();
                    //获取系统年月日
                    int nowYear = calendar.get(Calendar.YEAR);
                    int nowMonth = calendar.get(Calendar.MONTH)+1;
                    int nowDay = calendar.get(Calendar.DAY_OF_MONTH);
                    for(int i = 0; i < list.size(); i++){
                        final String contractId = list.get(i).getObjectId();
                        String ballTime = list.get(i).getBallTime();
                        int ballYear = Integer.parseInt(ballTime.split("-")[0]);
                        int ballMonth = Integer.parseInt(ballTime.split("-")[1]);
                        int ballDay = Integer.parseInt(ballTime.split("-")[2].split(" ")[0]);
                        boolean isFinished = false; //记录该约单是否已完成
                        if(list.get(i).getStatus() == 0){
                            //判断当前约单是否完成，通过和当前时间比较，时间超过当前时间则视为已完成
                            if(ballYear < nowYear){
                                isFinished = true;
                            }else if(ballMonth < nowMonth){
                                isFinished = true;
                            }else if(ballDay < nowDay){
                                isFinished = true;
                            }
                        }
                        //已完成订单修改其状态
                        if(isFinished){
                            Contract contract = new Contract();
                            contract.setStatus(1);
                            contract.update(contractId, new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if(e == null){
                                       updateCredit(contractId);    //修改约单状态后进行信用值修改
                                    }else{
                                        Toast.makeText(MainActivity.this, "查询3失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }else{
                    Toast.makeText(MainActivity.this, "查询4失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //修改相应用户信用值
    private void updateCredit(String contractId){
        BmobQuery<UserEnterContract> query1 = new BmobQuery<UserEnterContract>();
        query1.addWhereEqualTo("contractId", contractId);
        query1.findObjects(new FindListener<UserEnterContract>() {
            @Override
            public void done(List<UserEnterContract> list, BmobException e) {
                if(e == null){
                    for(int j = 0; j < list.size(); j++){
                        final int userStatus = list.get(j).getUserStatus();
                        Log.d("当前参与用户ID", list.get(j).getUserId());
                        Log.d("当前参与用户状态", userStatus+"");
                        BmobQuery<User> query = new BmobQuery<User>();
                        query.getObject(list.get(j).getUserId(), new QueryListener<User>() {
                            @Override
                            public void done(User user, BmobException e) {
                                if(e == null){
                                    Log.d("当前参与用户信用值", user.getUserCredit()+"");
                                    int newSubCredit = user.getUserCredit() - 30;
                                    int newAddCredit = user.getUserCredit() + 10;
                                    Log.d("更改后信用值", newSubCredit+"");
                                    if(userStatus == 0){
                                        User user1 = new User();
                                        user1.setUserCredit(newSubCredit);
                                        user1.update(user.getObjectId(), new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                if(e == null){
                                                }else{
                                                    Toast.makeText(MainActivity.this, "查询5失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }else{
                                        User user1 = new User();
                                        user1.setUserCredit(newAddCredit);
                                        user1.update(user.getObjectId(), new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                if(e == null){

                                                }else{
                                                    Toast.makeText(MainActivity.this, "查询6失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }else{
                                    Toast.makeText(MainActivity.this, "查询7失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }else{
                    Toast.makeText(MainActivity.this, "查询8失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case QUERY_USER_SUCCESS:
                    Log.d("dsadsa","aaa");
                    User user = (User) msg.obj;
                    TextView userName = (TextView) findViewById(R.id.user_person_name);
                    TextView userSex = (TextView) findViewById(R.id.user_sex);
                    TextView userAge = (TextView) findViewById(R.id.user_age);
                    TextView userIntro = (TextView) findViewById(R.id.user_intro);
                    CircleImageView userHeadIcon = (CircleImageView) findViewById(R.id.head_icon);
                    userName.setText(user.getUserName());
                    userSex.setText(user.getUserSex());
                    userAge.setText(user.getUserAge() + "岁");
                    userIntro.setText(user.getUserIntro());
                    if(user.getHeadIcon() != null){
                        ImageLoader imageLoader = ImageLoader.getInstance();
                        imageLoader.displayImage(user.getHeadIcon().getFileUrl(), userHeadIcon, ImageLoaderApplication.options);
                    }else{
                        userHeadIcon.setImageResource(R.drawable.ic_myinfo);
                    }
                    setFlag("main");
                    break;
                case QUERY_CONTRACT_SUCCESS:
                    contractList = (ArrayList<Contract>) msg.obj;
                    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.contract_recycler_view);
                    StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(layoutManager);
                    ContractMainAdapter adapter = new ContractMainAdapter(contractList);
                    recyclerView.setAdapter(adapter);
                    setFlag("main");
                    break;
                default:
                    break;
            }
        }
    };

}
