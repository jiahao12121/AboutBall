package com.example.cjh.aboutball.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cjh.aboutball.R;
import com.example.cjh.aboutball.db.Contract;
import com.example.cjh.aboutball.db.Test;
import com.example.cjh.aboutball.db.User;
import com.githang.statusbar.StatusBarCompat;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by cjh on 2018/7/28.
 * 登录界面
 */

public class LoginActivity extends AppCompatActivity{

    private AutoCompleteTextView userNameTel;
    private EditText userPassword;
    private Button signIn;
    private TextView registerIn;

    private String name_tel;
    private String password;

    private static final int QUERY_NO_USER = 0;
    private static final int QUERY_LOGIN_SUCCESS = 1;
    private static final int QUERY_LOGIN_ERROR = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.orange)); //设置状态栏颜色
        Bmob.initialize(this, "a6ae281975bc11279c375a74884bcca9"); //初始化Bmob服务器
        userNameTel = (AutoCompleteTextView) findViewById(R.id.user_name_tel);
        userPassword = (EditText) findViewById(R.id.user_password);

        /*设置密码输入完毕后软键盘上右下角也为登录按钮*/
        userPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login|| id == EditorInfo.IME_NULL) {
                    login();
                    return true;
                }
                return false;
            }
        });

        signIn = (Button) findViewById(R.id.sign_in);
        signIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        registerIn = (TextView) findViewById(R.id.register_in);
        registerIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, Register1Activity.class);
                startActivity(intent);
            }
        });
    }

    public void login(){
        /*先进行一些基本的输入正误判断*/
        userNameTel.setError(null);
        userPassword.setError(null);
        name_tel = userNameTel.getText().toString();
        password = userPassword.getText().toString();
        boolean cancel = false;
        View focusView = null;
        if (TextUtils.isEmpty(name_tel)) {
            userNameTel.setError("用户名不能为空!");
            focusView = userNameTel;
            cancel = true;
        }
        if(TextUtils.isEmpty(password)){
            userPassword.setError("密码不能为空!");
            focusView = userPassword;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();   //有错误则显示出错信息
            /*输入无误，则到服务器上查询对应用户验证登录*/
        } else{
            BmobQuery<User> query1 = new BmobQuery<User>();
            query1.addWhereEqualTo("userTel", name_tel);
            BmobQuery<User> query2 = new BmobQuery<User>();
            query2.addWhereEqualTo("userName", name_tel);
            List<BmobQuery<User>> queries = new ArrayList<BmobQuery<User>>();
            queries.add(query1);
            queries.add(query2);
            BmobQuery<User> mainQuery = new BmobQuery<User>();
            mainQuery.or(queries);
            mainQuery.findObjects(new FindListener<User>() {
                @Override
                public void done(List<User> list, BmobException e) {        //两种方法，一种message处理结果，一种直接在done里进行UI操作（目前认为可行）
                    if(e == null){      //list为查询结果
                        Message message = new Message();
                        if(list.size() == 0){       //用户不存在
                            message.what = QUERY_NO_USER;
                            handler.sendMessage(message);
                        }else{
                            if(list.get(0).getUserPwd().equals(password)){  //用户名密码正确
                                message.what = QUERY_LOGIN_SUCCESS;
                                message.obj = list.get(0);
                                handler.sendMessage(message);
                            }else{                  //用户名密码不正确
                                message.what = QUERY_LOGIN_ERROR;
                                handler.sendMessage(message);
                            }
                        }
                    }else{
                        Toast.makeText(LoginActivity.this, "查询失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("LoginActivity", e.getMessage());
                    }
                }
            });
        }
    }

    /*接受Bmob服务器查询的不同结果,并进行不同的操作*/
    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case QUERY_NO_USER:
                    Toast.makeText(LoginActivity.this, "帐号不存在，请重新输入!", Toast.LENGTH_SHORT).show();
                    userNameTel.setText("");
                    userPassword.setText("");
                    userNameTel.requestFocus();
                    break;
                case QUERY_LOGIN_SUCCESS:
                    User user = (User)msg.obj;
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("user_id", user.getObjectId());
                    LoginActivity.this.finish();
                    startActivity(intent);
                    break;
                case QUERY_LOGIN_ERROR:
                    Toast.makeText(LoginActivity.this, "用户名密码错误，请重新输入!", Toast.LENGTH_SHORT).show();
                    userNameTel.setText("");
                    userPassword.setText("");
                    userNameTel.requestFocus();
                    break;
                default:
                    break;
            }
        }
    };
}

