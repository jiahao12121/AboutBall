package com.example.cjh.aboutball.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.example.cjh.aboutball.R;
import com.example.cjh.aboutball.db.User;
import com.githang.statusbar.StatusBarCompat;

import org.litepal.crud.DataSupport;

import java.util.List;

public class MyInformationActivity extends AppCompatActivity {

    private EditText infoUserName;

    private EditText infoIntro;

    private Button saveInfo;

    private Spinner selectSex;

    private Spinner selectAge;

    private String username;

    private String userintro;

    private String sex;

    private int age;

    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_information);
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.orange));
        saveInfo = (Button) findViewById(R.id.title_save);
        infoUserName = (EditText) findViewById(R.id.info_user_name);
        infoIntro = (EditText) findViewById(R.id.info_intro);
        selectSex = (Spinner) findViewById(R.id.select_sex);
        selectAge = (Spinner) findViewById(R.id.select_age);
        saveInfo.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        setUserId(intent.getStringExtra("user_id"));
        username = DataSupport.where("id = ?", getUserId()).find(User.class)
                .get(0).getUserName();
        userintro = DataSupport.where("id = ?", getUserId()).find(User.class)
                .get(0).getUserIntro();
        infoUserName.setText(username);
        setSpinnerItemSelectedByValue(selectSex, DataSupport.where("id = ?", getUserId()).find(User.class)
                .get(0).getUserSex());
        setSpinnerItemSelectedByValue(selectAge, DataSupport.where("id = ?", getUserId()).find(User.class)
                .get(0).getUserAge() + "");
        if(TextUtils.isEmpty(userintro)){
            infoIntro.setText("");
        }else{
            infoIntro.setText(userintro);
        }
        selectSex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sex = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        selectAge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                age = Integer.parseInt(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        saveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View focusView = null;
                if(TextUtils.isEmpty(username)){
                    infoUserName.setError("用户名不能为空!");
                    focusView = infoUserName;
                    focusView.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(userintro)){
                    userintro = "无";
                }
                Toast.makeText(MyInformationActivity.this, "修改个人信息成功!", Toast.LENGTH_SHORT).show();
                User user = new User();
                user.setUserName(infoUserName.getText().toString());
                user.setUserSex(sex);
                user.setUserAge(age);
                user.setUserIntro(infoIntro.getText().toString());
                user.updateAll("id = ?", getUserId());
                MainActivity.setFlag("user");
                MyInformationActivity.this.finish();
            }
        });
    }
    public void setSpinnerItemSelectedByValue(Spinner spinner,String value){
        SpinnerAdapter adapter= spinner.getAdapter(); //得到SpinnerAdapter对象
        int k= adapter.getCount();
        for(int i=0;i<k;i++){
            if(TextUtils.isEmpty(value)) {
                spinner.setSelection(0, true);
            }else{
                if(value.equals(adapter.getItem(i).toString())){
                    spinner.setSelection(i,true);// 默认选中项
                    break;
                }
            }
        }
    }
}
