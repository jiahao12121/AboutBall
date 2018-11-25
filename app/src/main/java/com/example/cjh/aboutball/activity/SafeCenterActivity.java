package com.example.cjh.aboutball.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cjh.aboutball.R;
import com.example.cjh.aboutball.listview.SafeListItem;
import com.example.cjh.aboutball.listview.SafeListItemAdapter;
import com.githang.statusbar.StatusBarCompat;


import java.util.ArrayList;
import java.util.List;

public class SafeCenterActivity extends AppCompatActivity {

    private TextView titleText;
    private Button titleBack;
    private LinearLayout logOff;

    private String nowUserId;
    private List<SafeListItem> itemList = new ArrayList<>();

    public String getNowUserId() {
        return nowUserId;
    }

    public void setNowUserId(String nowUserId) {
        this.nowUserId = nowUserId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_center);
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.orange));
        titleText = (TextView) findViewById(R.id.title_text);
        titleBack = (Button) findViewById(R.id.title_back);
        logOff = (LinearLayout) findViewById(R.id.log_off);
        titleText.setText("设置");
        titleBack.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        setNowUserId(intent.getStringExtra("user_id"));
        initItem();
        SafeListItemAdapter adapter = new SafeListItemAdapter(SafeCenterActivity.this,
                R.layout.safe_list_item, itemList);
        ListView listView = (ListView) findViewById(R.id.list_view_safe);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SafeListItem safeListItem = itemList.get(position);
                Intent intent1;
                switch (safeListItem.getName()){
                    case "修改密码":
                        intent1 = new Intent(view.getContext(), ChangePwdActivity.class);
                        intent1.putExtra("user_id", getNowUserId());
                        startActivity(intent1);
                        break;
                    case "修改手机号":
                        intent1 = new Intent(view.getContext(), ChangeTelActivity.class);
                        intent1.putExtra("user_id", getNowUserId());
                        startActivity(intent1);
                        break;
                }
            }
        });
        logOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到登录界面时销毁其他所有活动
                Intent intent2 = new Intent(SafeCenterActivity.this, LoginActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent2);
            }
        });
    }

    private void initItem(){
        SafeListItem changePwd = new SafeListItem("修改密码", R.drawable.ic_right);
        itemList.add(changePwd);
        SafeListItem changeTel = new SafeListItem("修改手机号", R.drawable.ic_right);
        itemList.add(changeTel);
    }
}
