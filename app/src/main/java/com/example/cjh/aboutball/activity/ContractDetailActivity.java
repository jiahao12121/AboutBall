package com.example.cjh.aboutball.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
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
import com.example.cjh.aboutball.util.ImageLoaderApplication;
import com.githang.statusbar.StatusBarCompat;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class ContractDetailActivity extends AppCompatActivity {

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
    private RecyclerView recyclerView;

    private List<UserEnterContract> userEnteredList = new ArrayList<>();
    private String nowContractId;
    private String nowUserId;
    private String nowPersonalId;
    private String isHideEnter;
    private static final int QUERY_CONTRACT_SUCCESS = 0;
    private static final int QUERY_ENTER_SUCCESS = 1;
    private static final int QUERY_ENTER_ENABLE = 2;
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

    public String getNowPersonalId() {
        return nowPersonalId;
    }

    public void setNowPersonalId(String nowPersonalId) {
        this.nowPersonalId = nowPersonalId;
    }

    public String getIsHideEnter() {
        return isHideEnter;
    }

    public void setIsHideEnter(String isHideEnter) {
        this.isHideEnter = isHideEnter;
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

        /*获取该约单ID以及当前用户ID*/
        Intent intent = getIntent();
        setNowContractId(intent.getStringExtra("contract_id"));
        setNowUserId(intent.getStringExtra("user_id"));
        setIsHideEnter(intent.getStringExtra("is_hide_enter"));
        //如果从个人约单界面查看详情，则隐藏参与按钮
        if(getIsHideEnter().equals("yes")){
            detailEnter.setVisibility(View.INVISIBLE);
        }
        /*显示约单信息*/
        QueryContract();
        //显示参与用户
        BindEnteredUser();
        //点击发起人头像跳转个人资料界面
        detailHeadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(ContractDetailActivity.this, InforPersonalActivity.class);
                intent1.putExtra("personal_id", getNowPersonalId());
                intent1.putExtra("user_id", getNowUserId());
                startActivity(intent1);

            }
        });
        /*参与按钮点击事件*/
        detailEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobQuery<User> query = new BmobQuery<User>();
                query.getObject(getNowUserId(), new QueryListener<User>() {
                    @Override
                    public void done(User user, BmobException e) {
                        if(e == null){
                            if(user.getUserCredit() < 0){
                                Toast.makeText(ContractDetailActivity.this, "您的信用值过低，无法加入约单!", Toast.LENGTH_SHORT).show();
                                return;
                            }else{
                                 /*判断能否加入该约单，不能为约单创建人和参与人*/
                                BmobQuery<Contract> query1 = new BmobQuery<Contract>();
                                query1.getObject(getNowContractId(), new QueryListener<Contract>() {
                                    @Override
                                    public void done(final Contract contract, BmobException e) {
                                        final Message message = new Message();
                                        if(e == null){
                                            if(contract.getUserId().equals(getNowUserId())){
                                                Toast.makeText(ContractDetailActivity.this, "您已加入该约单!", Toast.LENGTH_SHORT).show();
                                                return;
                                            }else{
                                                BmobQuery<UserEnterContract> query2 = new BmobQuery<UserEnterContract>();
                                                query2.addWhereEqualTo("contractId", getNowContractId());
                                                query2.findObjects(new FindListener<UserEnterContract>() {
                                                    @Override
                                                    public void done(List<UserEnterContract> list, BmobException e) {
                                                        if(e == null){
                                                            for(int i = 0; i < list.size(); i++){
                                                                if((list.get(i).getUserId()+"").equals(getNowUserId())){
                                                                    Toast.makeText(ContractDetailActivity.this, "您已加入该约单!", Toast.LENGTH_SHORT).show();
                                                                    return;
                                                                }
                                                            }
                                                            if(contract.getMaxPnum() == contract.getNowPnum()){
                                                                Toast.makeText(ContractDetailActivity.this, "该约单人数已满!", Toast.LENGTH_SHORT).show();
                                                                return;
                                                            }else{
                                                                message.what = QUERY_ENTER_ENABLE;
                                                                message.arg1 = contract.getNowPnum();
                                                                int a = message.arg1 + 1;
                                                                Log.d("当前人数", a + "");
                                                                handler.sendMessage(message);
                                                            }
                                                        }else{
                                                            Toast.makeText(ContractDetailActivity.this, "查询1失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        }else{
                                            Toast.makeText(ContractDetailActivity.this, "查询2失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }else{
                            Toast.makeText(ContractDetailActivity.this, "查询3失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void QueryContract(){
        BmobQuery<Contract> query = new BmobQuery<Contract>();
        query.getObject(getNowContractId(), new QueryListener<Contract>() {
            @Override
            public void done(Contract contract, BmobException e) {
                if(e == null){
                    setNowPersonalId(contract.getUserId());
                    Message message = new Message();
                    message.what = QUERY_CONTRACT_SUCCESS;
                    message.obj = contract;
                    handler.sendMessage(message);
                }else{
                    Toast.makeText(ContractDetailActivity.this, "查询4失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("ContractDetailActivity", e.getMessage());
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
                    Message message = new Message();
                    message.what = QUERY_ENTER_SUCCESS;
                    message.obj = list;
                    handler.sendMessage(message);
                }else{
                    Toast.makeText(ContractDetailActivity.this, "查询5失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("ContractDetailActivity", e.getMessage());
                }
            }
        });
    }

    private Handler handler = new Handler(){
        public void handleMessage(final Message msg){
            switch (msg.what){
                case QUERY_CONTRACT_SUCCESS:
                    Contract contract = (Contract) msg.obj;
                    //显示约单信息
                    detailGroupName.setText(contract.getGroupName());
                    detailType.setText(contract.getBallType());
                    detailTime.setText(contract.getBallTime());
                    detailAddress.setText(contract.getBallAddress());
                    detailPnow.setText(String.valueOf(contract.getNowPnum()));
                    detailPnum.setText(String.valueOf(contract.getMaxPnum()));
                    detailIntro.setText(contract.getBallRemark());
                    BmobQuery<User> query = new BmobQuery<User>();
                    query.getObject(contract.getUserId(), new QueryListener<User>() {
                        @Override
                        public void done(User user, BmobException e) {
                            if(e == null){
                                detailName.setText(user.getUserName());
                                detailSex.setText(user.getUserSex());
                                //显示创建人信息
                                int age = user.getUserAge();
                                if(age == 0){
                                    detailAge.setText("");
                                }else{
                                    detailAge.setText(String.valueOf(age) + "岁");
                                }
                                if(user.getHeadIcon() != null){
                                    ImageLoader imageLoader = ImageLoader.getInstance();
                                    imageLoader.displayImage(user.getHeadIcon().getFileUrl(), detailHeadIcon, ImageLoaderApplication.options);
                                }else{
                                    detailHeadIcon.setImageResource(R.drawable.ic_myinfo);
                                }
                            }else{
                                Toast.makeText(ContractDetailActivity.this, "查询6失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    break;

                case QUERY_ENTER_SUCCESS:
                    userEnteredList = (ArrayList<UserEnterContract>) msg.obj;
                    recyclerView = (RecyclerView) findViewById(R.id.user_recycler_view);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(ContractDetailActivity.this);
                    layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    recyclerView.setLayoutManager(layoutManager);
                    ContractEnteredUserAdapter adapter = new ContractEnteredUserAdapter(userEnteredList);
                    recyclerView.setAdapter(adapter);
                    break;
                case QUERY_ENTER_ENABLE:
                    final int pnow = msg.arg1 + 1;
                    AlertDialog dialog = new AlertDialog.Builder(ContractDetailActivity.this)
                    .setTitle("提示")
                    .setMessage("您确认要加入该约单吗？")
                    .setCancelable(false)
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            /*添加对应记录到用户约单表*/
                            UserEnterContract userEnterContract = new UserEnterContract();
                            userEnterContract.setContractId(getNowContractId());
                            userEnterContract.setUserId(getNowUserId());
                            userEnterContract.setUserStatus(0);
                            userEnterContract.save(new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {
                                    /*修改当前人数,当前参与人数加1*/
                                    Contract contract = new Contract();
                                    contract.setValue("nowPnum", pnow);
                                    contract.update(getNowContractId(), new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if(e == null){
                                                Toast.makeText(ContractDetailActivity.this, "加入约单成功!", Toast.LENGTH_SHORT).show();
                                                MainActivity.setFlag("create");
                                                ContractDetailActivity.this.finish();
                                            }else{
                                                Toast.makeText(ContractDetailActivity.this, "查询7失败!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(18);
                    dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextSize(18);
                    break;
                default:
                    break;
            }
        }
    };
}
