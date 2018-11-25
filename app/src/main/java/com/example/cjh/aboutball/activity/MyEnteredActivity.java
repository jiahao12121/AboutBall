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
import com.example.cjh.aboutball.db.UserEnterContract;
import com.example.cjh.aboutball.recyclerview.ContractMyEnteredAdapter;
import com.githang.statusbar.StatusBarCompat;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

public class MyEnteredActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefresh;
    private TextView titleText;
    private RecyclerView recyclerView;
    private TextView contractTip;

    private ContractMyEnteredAdapter adapter;

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
        setContentView(R.layout.activity_my_entered);
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.orange));
        titleText = (TextView) findViewById(R.id.title_text);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.my_entered_swipe_refresh);
        recyclerView = (RecyclerView) findViewById(R.id.my_entered_recycler_view);
        contractTip = (TextView) findViewById(R.id.no_entered_contract);
        swipeRefresh.setColorSchemeResources(R.color.orange);
        titleText.setText("我参与的");
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        Intent intent = getIntent();
        setNowUserId(intent.getStringExtra("user_id"));
        QueryMyEnteredContract();
    }


    public void QueryMyEnteredContract(){
        contractList.clear();
        BmobQuery<UserEnterContract> query = new BmobQuery<UserEnterContract>();
        query.addWhereEqualTo("userId", getNowUserId());
        query.findObjects(new FindListener<UserEnterContract>() {
            @Override
            public void done(List<UserEnterContract> list, BmobException e) {
                if(e == null){
                    if(list.size() == 0){
                        bindEnteredRecycleView();
                        contractTip.setVisibility(View.VISIBLE);
                    }else{
                        for(int i = 0; i < list.size(); i++){
                            BmobQuery<Contract> query1 = new BmobQuery<Contract>();
                            query1.getObject(list.get(i).getContractId(), new QueryListener<Contract>() {
                                @Override
                                public void done(Contract contract, BmobException e) {
                                    if(e == null){
                                        contractList.add(contract);
                                        /*设置我参与的约单RecyclerView*/
                                        bindEnteredRecycleView();
                                    }else{
                                        Toast.makeText(MyEnteredActivity.this, "查询1失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }else{
                    Toast.makeText(MyEnteredActivity.this, "查询2失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                QueryMyEnteredContract();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    private void bindEnteredRecycleView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(MyEnteredActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ContractMyEnteredAdapter(contractList);
        recyclerView.setAdapter(adapter);
    }
}
