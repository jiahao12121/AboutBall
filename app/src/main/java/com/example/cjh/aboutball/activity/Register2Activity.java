package com.example.cjh.aboutball.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.githang.statusbar.StatusBarCompat;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by cjh on 2018/7/28.
 * 注册界面2（设置用户名密码）
 */


public class Register2Activity extends AppCompatActivity {

    private AutoCompleteTextView userRegName;

    private EditText userRegPwd;

    private EditText userRegSecondPwd;

    private Button finishRegister;

    private String telephone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.orange));
        userRegName = (AutoCompleteTextView) findViewById(R.id.user_reg_name);
        userRegPwd = (EditText) findViewById(R.id.user_reg_password);
        userRegSecondPwd = (EditText) findViewById(R.id.user_reg_second_password);
        finishRegister = (Button) findViewById(R.id.finish_regitser);

        Intent intent = getIntent();
        telephone = intent.getStringExtra("telephone_extra");
        Log.d("Register2Activity", telephone);

        userRegSecondPwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.finish|| id == EditorInfo.IME_NULL) {
                    regitser();
                    return true;
                }
                return false;
            }
        });

        finishRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regitser();
            }
        });
    }

    public void regitser(){
        userRegName.setError(null);
        userRegPwd.setError(null);
        userRegSecondPwd.setError(null);
        String name = userRegName.getText().toString();
        String pwd = userRegPwd.getText().toString();
        String secondpwd = userRegSecondPwd.getText().toString();
        boolean cancel = false;
        View focusView = null;
        /*用户名输入检测*/
        if (TextUtils.isEmpty(name)) {
            userRegName.setError("用户名不能为空!");
            focusView = userRegName;
            cancel = true;
        }else if(name.length() <= 3){     //要求用户名长度大于3
            userRegName.setError("用户名长度太短!");
            focusView = userRegName;
            cancel = true;
        }else{
            List<User> result = DataSupport.where("username = ?",name).find(User.class);
            if(result.size() > 0){
                userRegName.setError("该用户名已存在!");
                focusView = userRegName;
                cancel = true;
            }
        }
        /*密码输入检测*/
        if(TextUtils.isEmpty(pwd)){
            userRegPwd.setError("密码不能为空!");
            focusView = userRegPwd;
            cancel = true;
        }else if(pwd.length() <= 4){     //要求密码长度大于4
            userRegName.setError("密码长度太短!");
            focusView = userRegName;
            cancel = true;
        }

        if(TextUtils.isEmpty(secondpwd)){
            userRegSecondPwd.setError("请确认密码!");
            focusView = userRegSecondPwd;
            cancel = true;
        }

        if(cancel){
            focusView.requestFocus();
        }else{      //全部输入正确，存手机号，用户名，密码到数据库，跳转到主页
            if(pwd.equals(secondpwd)){
                User user = new User();
                user.setUserTel(telephone);
                user.setUserName(name);
                user.setUserPwd(pwd);
                user.setHeadIcon(R.drawable.ic_myinfo);
                user.save();
                Toast.makeText(Register2Activity.this, "注册成功!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Register2Activity.this, LoginActivity.class);
                Register2Activity.this.finish();
                startActivity(intent);
            }else{
                Toast.makeText(Register2Activity.this, "两次密码输入不一致，请重新输入!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
