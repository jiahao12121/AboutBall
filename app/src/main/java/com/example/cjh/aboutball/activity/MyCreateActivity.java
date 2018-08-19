package com.example.cjh.aboutball.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cjh.aboutball.R;
import com.example.cjh.aboutball.db.Contract;
import com.example.cjh.aboutball.recyclerview.ContractMyCreateAdapter;
import com.githang.statusbar.StatusBarCompat;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class MyCreateActivity extends AppCompatActivity {

    private List<Contract> contractList = new ArrayList<>();

    private SwipeRefreshLayout swipeRefresh;

    private TextView titleText;

    private RecyclerView recyclerView;

    private ContractMyCreateAdapter adapter;

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
        setContentView(R.layout.activity_my_create);
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.orange));
        titleText = (TextView) findViewById(R.id.title_text);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.my_create_swipe_refresh);
        recyclerView = (RecyclerView) findViewById(R.id.my_create_recycler_view);
        swipeRefresh.setColorSchemeResources(R.color.orange);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        titleText.setText("我创建的");
        Intent intent = getIntent();
        setNowUserId(intent.getStringExtra("user_id"));
        Log.d("MyCreateActivity", getNowUserId());
        contractList = DataSupport.where("userId = ?", getNowUserId()).find(Contract.class);
        /*设置我创建的约单RecyclerView*/
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ContractMyCreateAdapter(contractList);
        recyclerView.setAdapter(adapter);
    }

    private void refresh(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(1500);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        contractList = DataSupport.where("userId = ?", getNowUserId()).find(Contract.class);
                        adapter = new ContractMyCreateAdapter(contractList);
                        recyclerView.setAdapter(adapter);
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

}
