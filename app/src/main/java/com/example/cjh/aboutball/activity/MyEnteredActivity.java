package com.example.cjh.aboutball.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.example.cjh.aboutball.R;
import com.example.cjh.aboutball.db.Contract;
import com.example.cjh.aboutball.db.UserEnterContract;
import com.example.cjh.aboutball.recyclerview.ContractMyEnteredAdapter;
import com.githang.statusbar.StatusBarCompat;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class MyEnteredActivity extends AppCompatActivity {


    private List<Contract> contractList = new ArrayList<>();

    private SwipeRefreshLayout swipeRefresh;

    private TextView titleText;

    private RecyclerView recyclerView;

    private ContractMyEnteredAdapter adapter;

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
        setContentView(R.layout.activity_my_entered);
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.orange));
        titleText = (TextView) findViewById(R.id.title_text);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.my_entered_swipe_refresh);
        recyclerView = (RecyclerView) findViewById(R.id.my_entered_recycler_view);
        swipeRefresh.setColorSchemeResources(R.color.orange);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        titleText.setText("我参与的");
        Intent intent = getIntent();
        setNowUserId(intent.getStringExtra("user_id"));
        Log.d("MyEnteredActivity", getNowUserId());
        List<UserEnterContract> result = DataSupport.where("userId = ?", getNowUserId()).find(UserEnterContract.class);
        for(int i = 0; i < result.size(); i++){
            Contract contract = DataSupport.where("id = ?", result.get(i).getContractId() + "").find(Contract.class).get(0);
            contractList.add(contract);
        }
        /*设置我创建的约单RecyclerView*/
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ContractMyEnteredAdapter(contractList);
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
                        contractList.clear();
                        List<UserEnterContract> result = DataSupport.where("userId = ?", getNowUserId()).find(UserEnterContract.class);
                        for(int i = 0; i < result.size(); i++){
                            Contract contract = DataSupport.where("id = ?", result.get(i).getContractId() + "").find(Contract.class).get(0);
                            contractList.add(contract);
                        }
                        adapter = new ContractMyEnteredAdapter(contractList);
                        recyclerView.setAdapter(adapter);
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }
}
