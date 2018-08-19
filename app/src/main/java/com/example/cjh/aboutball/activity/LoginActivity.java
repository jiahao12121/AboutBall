package com.example.cjh.aboutball.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
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
import com.example.cjh.aboutball.db.User;
import com.example.cjh.aboutball.db.UserEnterContract;
import com.githang.statusbar.StatusBarCompat;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by cjh on 2018/7/28.
 * 登录界面
 */

public class LoginActivity extends AppCompatActivity{

    private AutoCompleteTextView userNameTel;
    private EditText userPassword;
    private Button signIn;
    private TextView registerIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.orange));
        initUsers();
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

    public void initUsers(){
      /*  User user = new User();
        user.setUserTel("18888888888");
        user.updateAll("userName = ?", "chenjiahao");*/
        /*DataSupport.deleteAll(User.class);
        DataSupport.deleteAll(Contract.class);
        DataSupport.deleteAll(UserEnterContract.class);

        User user1 =  new User(R.drawable.ic_myinfo, "15007550361", "fanxiaohui", "123456");
        user1.save();
        User user2 =  new User(R.drawable.ic_myinfo, "18482281233", "chenxinyue", "123456");
        user2.save();
        User user3 =  new User(R.drawable.ic_pear, "15520765168", "chenyuchen", "123456");
        user3.save();*/
    }

    public void login(){
        userNameTel.setError(null);
        userPassword.setError(null);
        String name_tel = userNameTel.getText().toString();
        String password = userPassword.getText().toString();
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
        } else{      //判断用户名密码是否正确
            List<User> result = DataSupport.select("userPwd")
                    .where("userName = ? or userTel = ?", name_tel, name_tel)
                    .find(User.class);
            if(result.size() == 0){
                Toast.makeText(LoginActivity.this, "帐号不存在，请重新输入!", Toast.LENGTH_SHORT).show();
                userNameTel.setText("");
                userPassword.setText("");
                userNameTel.requestFocus();
            }else{
                if(result.get(0).getUserPwd().equals(password)){
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("user_id", result.get(0).getId() + "");
                    LoginActivity.this.finish();
                    startActivity(intent);
                }else{
                    Toast.makeText(LoginActivity.this, "用户名密码错误，请重新输入!", Toast.LENGTH_SHORT).show();
                    userNameTel.setText("");
                    userPassword.setText("");
                    userNameTel.requestFocus();
                }
            }
        }
    }
}

