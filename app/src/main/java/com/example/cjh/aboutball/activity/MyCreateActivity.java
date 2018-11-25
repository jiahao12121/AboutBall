package com.example.cjh.aboutball.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.example.cjh.aboutball.R;
import com.example.cjh.aboutball.db.Contract;
import com.example.cjh.aboutball.recyclerview.ContractMyCreateAdapter;
import com.githang.statusbar.StatusBarCompat;


import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MyCreateActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefresh;
    private TextView titleText;
    private RecyclerView recyclerView;
    private ContractMyCreateAdapter adapter;
    private TextView contracTip;

    private List<Contract> contractList = new ArrayList<>();
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
        titleText.setText("我创建的");
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.my_create_swipe_refresh);
        recyclerView = (RecyclerView) findViewById(R.id.my_create_recycler_view);
        contracTip = (TextView) findViewById(R.id.no_create_contract);
        swipeRefresh.setColorSchemeResources(R.color.orange);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        Intent intent = getIntent();
        setNowUserId(intent.getStringExtra("user_id"));


        QueryMyCreateContract();

    }

    public void QueryMyCreateContract(){
        contractList.clear();
        BmobQuery<Contract> query = new BmobQuery<Contract>();
        query.addWhereEqualTo("userId", getNowUserId());
        query.findObjects(new FindListener<Contract>() {
            @Override
            public void done(List<Contract> list, BmobException e) {
                if(e == null){
                    if(list.size() == 0){   //若没有创建的约单
                        bindCreateRecycleView();
                        contracTip.setVisibility(View.VISIBLE);
                    }else{
                        contractList = list;
                /*设置我创建的约单RecyclerView*/
                        bindCreateRecycleView();
                    }
                }else{
                    Toast.makeText(MyCreateActivity.this, "查询失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                QueryMyCreateContract();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    private void bindCreateRecycleView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(MyCreateActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ContractMyCreateAdapter(contractList);
        recyclerView.setAdapter(adapter);
    }

}
