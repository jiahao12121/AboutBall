package com.example.cjh.aboutball.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cjh.aboutball.R;
import com.example.cjh.aboutball.db.User;
import com.example.cjh.aboutball.util.CountDownButton;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mob.MobSDK;

import org.litepal.crud.DataSupport;

import java.util.List;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class ChangeTelActivity extends AppCompatActivity {

    private TextView titleText;

    private TextView nowTel;

    private EditText newTel;

    private EditText code;

    private CountDownButton getCode;

    private Button finishTel;

    private View focusView = null;

    private String telephone;

    private GoogleApiClient client;

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
        setContentView(R.layout.activity_change_tel);
        titleText = (TextView) findViewById(R.id.title_text);
        titleText.setText("修改手机号");
        nowTel = (TextView) findViewById(R.id.now_tel);
        newTel = (EditText) findViewById(R.id.new_tel);
        code = (EditText) findViewById(R.id.change_identify_code);
        getCode = (CountDownButton) findViewById(R.id.change_get_code);
        finishTel = (Button) findViewById(R.id.finish_tel);
        Intent intent = getIntent();
        setNowUserId(intent.getStringExtra("user_id"));
        nowTel.setText(DataSupport.where("id = ?", getNowUserId()).find(User.class).get(0).getUserTel());
        getCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telephone = newTel.getText().toString();
                List<User> result = DataSupport.select("userTel")
                        .where("userTel = ?", telephone)
                        .find(User.class);
                if(telephone.equals("")){
                    newTel.setError("手机号码不能为空!");
                    focusView = newTel;
                    focusView.requestFocus();
                }else if (telephone.length() != 11) {
                    newTel.setError("手机号码不合法!");
                    focusView = newTel;
                    focusView.requestFocus();
                }else if(result.size() == 0){
                    /*判断倒计时是否结束，避免重复点击*/
                    if(getCode.isFinish()){
                        getCode.setEnabled(true);
                        getCode.start();
                        SMSSDK.getVerificationCode("86", telephone);
                    }else{
                        getCode.setEnabled(false);
                    }
                }else{
                    newTel.setError("该号码已被注册!");
                    focusView = newTel;
                    focusView.requestFocus();
                }
            }
        });

        finishTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newTel.setError(null);
                code.setError(null);
                if(TextUtils.isEmpty(code.getText())){
                    code.setError("验证码不能为空!");
                    focusView = code;
                    focusView.requestFocus();
                }else{
                    String identifycode = code.getText().toString();
                    SMSSDK.submitVerificationCode("86", telephone, identifycode);
                }
            }
        });

        MobSDK.init(this, "27153412f2b52", "d0ae4b347f4332285f4bc724ec124ba6");

        EventHandler eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(eventHandler);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!getCode.isFinish()){
            getCode.cancel();
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            int event=msg.arg1;
            int result=msg.arg2;
            Object data=msg.obj;
            if(result==SMSSDK.RESULT_COMPLETE){  /*回调成功*/
                if(event==SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE){  /*通过验证*/
                    Toast.makeText(ChangeTelActivity.this,"手机号修改成功！",Toast.LENGTH_SHORT).show();
                    User user = new User();
                    user.setUserTel(telephone);
                    user.updateAll("id = ?", getNowUserId());
                    ChangeTelActivity.this.finish();
                }else if(event == SMSSDK.EVENT_GET_VERIFICATION_CODE){  /*发送成功*/
                    Toast.makeText(ChangeTelActivity.this, "验证码已发送", Toast.LENGTH_SHORT).show();
                }
            }else{   /*回调失败*/
                    ((Throwable)data).printStackTrace();
                     Toast.makeText(ChangeTelActivity.this,"验证码错误",Toast.LENGTH_SHORT).show();
            }
        }
    };
}
