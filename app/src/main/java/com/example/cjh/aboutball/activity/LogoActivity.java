package com.example.cjh.aboutball.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.example.cjh.aboutball.R;

public class LogoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        ActionBar actionBar = getSupportActionBar();        //隐藏原生顶部标题栏
        if(actionBar != null){
            actionBar.hide();
        }
        setContentView(R.layout.activity_logo);
        Thread myThread = new Thread(){//创建子线程
            @Override
            public void run() {
                try{
                    sleep(3000);//使程序休眠五秒
                    Intent intent = new Intent(LogoActivity.this, LoginActivity.class);//显示5sLOGO后跳转到登录界面
                    startActivity(intent);
                    finish();//关闭当前活动
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        myThread.start();//启动线程
    }
}
