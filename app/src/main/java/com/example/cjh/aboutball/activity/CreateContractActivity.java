package com.example.cjh.aboutball.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.codbking.widget.DatePickDialog;
import com.codbking.widget.OnSureLisener;
import com.codbking.widget.bean.DateType;
import com.example.cjh.aboutball.R;
import com.example.cjh.aboutball.db.Contract;

import com.githang.statusbar.StatusBarCompat;


import java.text.SimpleDateFormat;
import java.util.Date;


import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class CreateContractActivity extends AppCompatActivity {

    private TextView titleText;
    private EditText createDate;
    private Spinner createType;
    private Spinner createAddress;
    private Spinner createPnum;
    private EditText createGroupName;
    private EditText createIntro;
    private Button createFinish;

    private String time = "";
    private String type;
    private String address;
    private int pnum;
    private String groupname;
    private String introduce;

    private String nowUserId;

    public String getNowUserId() {
        return nowUserId;
    }

    public void setNowUserId(String nowUserId) {
        this.nowUserId = nowUserId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_contract);
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.orange));
        titleText = (TextView) findViewById(R.id.title_text);
        createType = (Spinner) findViewById(R.id.create_type);
        createAddress = (Spinner) findViewById(R.id.create_address);
        createPnum = (Spinner) findViewById(R.id.create_pnum);
        createGroupName = (EditText) findViewById(R.id.create_group_name);
        createIntro = (EditText) findViewById(R.id.create_intro);
        createDate = (EditText) findViewById(R.id.create_date);

        titleText.setText("创建约单");
        createType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        createAddress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                address = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        createPnum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pnum = Integer.parseInt(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        createDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickDialog dialog = new DatePickDialog(CreateContractActivity.this);
                //设置上下年分限制
                dialog.setYearLimt(5);
                //设置标题
                dialog.setTitle("选择时间");
                //设置类型
                dialog.setType(DateType.TYPE_ALL);
                //设置消息体的显示格式，日期格式
                dialog.setMessageFormat("yyyy-MM-dd HH:mm");
                //设置选择回调
                dialog.setOnChangeLisener(null);
                //设置点击确定按钮回调
                dialog.setOnSureLisener(new OnSureLisener() {
                    @Override
                    public void onSure(Date date) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        time = sdf.format(date.getTime());
                        createDate.setText(time);
                    }
                });
                dialog.show();
            }
        });
        createFinish = (Button) findViewById(R.id.create_finish);
        createFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupname = createGroupName.getText().toString();
                introduce = createIntro.getText().toString();
                View focusView = null;
                createGroupName.setError(null);
                if(time.equals("")){
                    Toast.makeText(CreateContractActivity.this, "请选择运动时间!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("CreateContractActivity", groupname);
                if(TextUtils.isEmpty(groupname)){
                    createGroupName.setError("队名不能为空!");
                    focusView = createGroupName;
                    focusView.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(introduce)){
                    introduce = "无";
                }
                AlertDialog dialog = new AlertDialog.Builder(CreateContractActivity.this)
                .setTitle("提示")
                .setMessage("您确认要创建该约单吗？")
                .setCancelable(false)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(CreateContractActivity.this, "创建约单成功!", Toast.LENGTH_SHORT).show();
                        Intent intent = getIntent();
                        setNowUserId(intent.getStringExtra("user_id"));
                        Contract contract = new Contract();
                        contract.setUserId(getNowUserId());
                        contract.setBallTime(time);
                        contract.setBallType(type);
                        contract.setBallAddress(address);
                        contract.setMaxPnum(pnum);
                        contract.setNowPnum(1);
                        contract.setGroupName(groupname);
                        contract.setBallRemark(introduce);
                        contract.setStatus(0);
                        contract.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if(e == null){
                                    MainActivity.setFlag("create");
                                    CreateContractActivity.this.finish();
                                }else{
                                    Toast.makeText(CreateContractActivity.this, "查询失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
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
            }
        });
    }
}
