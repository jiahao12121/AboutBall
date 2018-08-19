package com.example.cjh.aboutball.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cjh.aboutball.R;
import com.example.cjh.aboutball.db.Contract;
import com.example.cjh.aboutball.listview.MyContractListItem;
import com.example.cjh.aboutball.listview.MyContractListItemAdapter;
import com.githang.statusbar.StatusBarCompat;

import java.util.ArrayList;
import java.util.List;

public class MyContractActivity extends AppCompatActivity {

    private List<MyContractListItem> itemList = new ArrayList<>();

    private TextView titleText;

    private Button titleBack;

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
        setContentView(R.layout.activity_my_contract);
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.orange));
        titleText = (TextView) findViewById(R.id.title_text);
        titleBack = (Button) findViewById(R.id.title_back);
        titleText.setText("我的约单");
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyContractActivity.this.finish();
                MainActivity.setFlag("create");
            }
        });
        initItem();
        Intent intent = getIntent();
        setNowUserId(intent.getStringExtra("user_id"));
        Log.d("MyContractActivity", getNowUserId());
        MyContractListItemAdapter adapter = new MyContractListItemAdapter(MyContractActivity.this,
                R.layout.my_contract_list_item, itemList);
        ListView listView = (ListView) findViewById(R.id.list_view_my_contract);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyContractListItem myContractListItem = itemList.get(position);
                Intent intent;
                switch (myContractListItem.getName()){
                    case "我创建的":
                        intent = new Intent(view.getContext(), MyCreateActivity.class);
                        intent.putExtra("user_id", getNowUserId());
                        startActivity(intent);
                        break;
                    case "我参与的":
                        intent = new Intent(view.getContext(), MyEnteredActivity.class);
                        intent.putExtra("user_id", getNowUserId());
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    private void initItem(){
        MyContractListItem myCreate = new MyContractListItem("我创建的", R.drawable.ic_right);
        itemList.add(myCreate);
        MyContractListItem myEntered = new MyContractListItem("我参与的", R.drawable.ic_right);
        itemList.add(myEntered);
    }
}
