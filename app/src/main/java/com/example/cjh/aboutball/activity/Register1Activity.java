package com.example.cjh.aboutball.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cjh.aboutball.R;
import com.example.cjh.aboutball.db.User;
import com.example.cjh.aboutball.util.CountDownButton;
import com.githang.statusbar.StatusBarCompat;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mob.MobSDK;

import org.litepal.crud.DataSupport;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by cjh on 2018/7/28.
 * 注册界面1（手机号验证码）
 */

public class Register1Activity extends AppCompatActivity {

    private AutoCompleteTextView telePhone;
    private EditText identifyCode;
    private CountDownButton getCode;
    private Button nextStep;
    private View focusView = null;
    private GoogleApiClient client;

    private String telephone;
    private String identifycode;

    public static final int TEL_EMPTY = 0;
    public static final int TEL_ILLEGAL = 1;
    public static final int TEL_SUCCESS = 2;
    public static final int TEL_REGISTERED = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register1);
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.orange));
        telePhone = (AutoCompleteTextView) findViewById(R.id.telephone);
        identifyCode = (EditText) findViewById(R.id.identify_code);
        getCode = (CountDownButton) findViewById(R.id.get_code);
        nextStep = (Button) findViewById(R.id.next_step);
        getCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telephone = telePhone.getText().toString();
                if(telephone.equals("")){
                    telePhone.setError("手机号码不能为空!");
                    focusView = telePhone;
                    focusView.requestFocus();
                }else if (telephone.length() != 11) {
                    telePhone.setError("手机号码不合法!");
                    focusView = telePhone;
                    focusView.requestFocus();
                }else{
                    BmobQuery<User> query = new BmobQuery<User>();
                    query.addWhereEqualTo("userTel", telephone);
                    query.findObjects(new FindListener<User>() {
                        @Override
                        public void done(List<User> list, BmobException e) {
                            if(e == null){
                                Message message = new Message();
                                if(list.size() == 0){     //数据库中没有该手机号则可以注册
                                    message.what = TEL_SUCCESS;
                                    RegisterHandler.sendMessage(message);
                                }else{
                                    message.what = TEL_REGISTERED;
                                    RegisterHandler.sendMessage(message);
                                }
                            }else{
                                Toast.makeText(Register1Activity.this, "查询失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d("Register1Activity", e.getMessage());
                            }
                        }
                    });
                }
            }
        });
        nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next();
            }
        });
        /*设置密码输入完毕后软键盘上右下角也为登录按钮*/
        getCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.next || id == EditorInfo.IME_NULL) {
                    next();
                    return true;
                }
                return false;
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

    //下一步
    public void next(){
        telePhone.setError(null);
        identifyCode.setError(null);
        if(TextUtils.isEmpty(identifyCode.getText())){
            identifyCode.setError("验证码不能为空!");
            focusView = identifyCode;
            focusView.requestFocus();
        }else{
            identifycode = identifyCode.getText().toString();
            SMSSDK.submitVerificationCode("86", telephone, identifycode);
        }
    }

    private Handler RegisterHandler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case TEL_SUCCESS:
                    /*判断倒计时是否结束，避免重复点击*/
                    if(getCode.isFinish()){
                        getCode.setEnabled(true);
                        getCode.start();
                        SMSSDK.getVerificationCode("86", telephone);
                    }else{
                        getCode.setEnabled(false);
                    }
                    break;
                case TEL_REGISTERED:
                    telePhone.setError("该号码已被注册!");
                    focusView = telePhone;
                    focusView.requestFocus();
                    break;
                default:
                    break;
            }
        }
    };

    //验证码回调事件处理
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            int event=msg.arg1;
            int result=msg.arg2;
            Object data=msg.obj;

            if(result==SMSSDK.RESULT_COMPLETE){  /*回调成功*/
                if(event==SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE){  /*通过验证*/
                    Toast.makeText(Register1Activity.this,"验证成功！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Register1Activity.this, Register2Activity.class);
                    intent.putExtra("telephone_extra", telephone);
                    startActivity(intent);
                    finish();
                }else if(event == SMSSDK.EVENT_GET_VERIFICATION_CODE){  /*发送成功*/
                    Toast.makeText(Register1Activity.this, "验证码已发送", Toast.LENGTH_SHORT).show();
                }
            }else{   /*回调失败，一天内验证码发送次数有限*/
                    ((Throwable)data).printStackTrace();
                    Toast.makeText(Register1Activity.this, "验证码错误", Toast.LENGTH_SHORT).show();
            }
        }
    };
}
