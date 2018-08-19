package com.example.cjh.aboutball.activity;

import android.content.Intent;
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
import com.githang.statusbar.StatusBarCompat;

import org.litepal.crud.DataSupport;

import java.util.List;

public class ChangePwdActivity extends AppCompatActivity {

    private TextView titleText;

    private EditText originPwd;

    private EditText newPwd;

    private EditText confirmPwd;

    private Button finishPwd;

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
        setContentView(R.layout.activity_change_pwd);
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.orange));
        titleText = (TextView) findViewById(R.id.title_text);
        titleText.setText("修改密码");
        originPwd = (EditText) findViewById(R.id.origin_pwd);
        newPwd = (EditText) findViewById(R.id.new_pwd);
        confirmPwd = (EditText) findViewById(R.id.confirm_pwd);
        finishPwd = (Button) findViewById(R.id.finish_pwd);
        Intent intent = getIntent();
        setNowUserId(intent.getStringExtra("user_id"));
        finishPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                originPwd.setError(null);
                newPwd.setError(null);
                confirmPwd.setError(null);
                String origin_pwd = originPwd.getText().toString();
                String new_pwd = newPwd.getText().toString();
                String confirm_pwd = confirmPwd.getText().toString();
                boolean cancel = false;
                View focusView = null;

                if (TextUtils.isEmpty(origin_pwd)) {
                    originPwd.setError("原密码不能为空!");
                    focusView = originPwd;
                    cancel = true;
                }

                if(TextUtils.isEmpty(new_pwd)){
                    newPwd.setError("新密码不能为空!");
                    focusView = newPwd;
                    cancel = true;
                }

                if(TextUtils.isEmpty(confirm_pwd)){
                    confirmPwd.setError("请确认密码!");
                    focusView = confirmPwd;
                    cancel = true;
                }

                if(cancel){
                    focusView.requestFocus();   //有错误则显示出错信息
                }else{
                    List<User> result = DataSupport.where("id = ?", getNowUserId()).find(User.class);
                    if(origin_pwd.equals(result.get(0).getUserPwd())){
                        if(new_pwd.equals(confirm_pwd)){
                            User user = new User();
                            user.setUserPwd(new_pwd);
                            user.updateAll("id = ?", getNowUserId());
                            Toast.makeText(ChangePwdActivity.this, "密码修改成功!", Toast.LENGTH_SHORT).show();
                            ChangePwdActivity.this.finish();
                        }else{
                            Toast.makeText(ChangePwdActivity.this, "两次密码输入不一致，请重新输入!", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(ChangePwdActivity.this, "原密码错误，请重新输入!", Toast.LENGTH_SHORT).show();
                        originPwd.requestFocus();
                    }
                }
            }
        });
    }
}
