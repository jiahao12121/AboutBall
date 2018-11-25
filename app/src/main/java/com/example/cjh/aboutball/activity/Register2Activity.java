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
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cjh.aboutball.R;
import com.example.cjh.aboutball.db.Test;
import com.example.cjh.aboutball.db.User;
import com.githang.statusbar.StatusBarCompat;

import org.litepal.crud.DataSupport;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

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
    private String username;
    private String password;
    private String secondpwd;

    public static final int USERNAME_EXIST = 0;
    public static final int REGISTER_SUCCESS = 1;
    public static final int PWD_NOTEQUAL = 2;
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
        username = userRegName.getText().toString();
        password = userRegPwd.getText().toString();
        secondpwd = userRegSecondPwd.getText().toString();
        boolean cancel = false;
        View focusView = null;
        /*用户名输入检测*/
        if (TextUtils.isEmpty(username)) {
            userRegName.setError("用户名不能为空!");
            focusView = userRegName;
            cancel = true;
        }else if(username.length() <= 3){     //要求用户名长度大于3
            userRegName.setError("用户名长度太短!");
            focusView = userRegName;
            cancel = true;
        }
        /*密码输入检测*/
        if(TextUtils.isEmpty(password)){
            userRegPwd.setError("密码不能为空!");
            focusView = userRegPwd;
            cancel = true;
        }else if(password.length() <= 4){     //要求密码长度大于4
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
        }else{      //全部输入正确，查询数据库用户名是否存在
            BmobQuery<User> query = new BmobQuery<User>();
            query.addWhereEqualTo("userName", username);
            query.findObjects(new FindListener<User>() {
                @Override
                public void done(List<User> list, BmobException e) {
                    if(e == null){
                        Message message = new Message();
                        if(list.size() > 0){    //用户名已存在
                            message.what = USERNAME_EXIST;
                            handler.sendMessage(message);
                        }else{
                            if(password.equals(secondpwd)){     //注册成功，添加数据到数据库
                                message.what = REGISTER_SUCCESS;
                                handler.sendMessage(message);
                            }else{
                                message.what = PWD_NOTEQUAL;
                                handler.sendMessage(message);
                            }
                        }
                    }else{
                        Toast.makeText(Register2Activity.this, "查询1失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("Register2Activity", e.getMessage());
                    }
                }
            });
        }
    }

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case USERNAME_EXIST:
                    Toast.makeText(Register2Activity.this, "该用户名已存在!", Toast.LENGTH_SHORT).show();
                    break;
                case REGISTER_SUCCESS:
                    User user = new User();
                    user.setUserPwd(password);
                    user.setUserName(username);
                    user.setUserTel(telephone);
                    user.setUserAge(0);
                    user.setUserCredit(100);
                    user.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e == null){
                                Toast.makeText(Register2Activity.this, "注册成功!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Register2Activity.this, LoginActivity.class);
                                finish();
                                startActivity(intent);
                            }else{
                                Toast.makeText(Register2Activity.this, "查询2失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d("Register2Activity", e.getMessage());
                            }
                        }
                    });
                    break;
                case PWD_NOTEQUAL:
                    Toast.makeText(Register2Activity.this, "两次密码输入不一致，请重新输入!", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
}
