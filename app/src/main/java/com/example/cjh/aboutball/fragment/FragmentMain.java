package com.example.cjh.aboutball.fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cjh.aboutball.R;

import com.example.cjh.aboutball.activity.CreateContractActivity;
import com.example.cjh.aboutball.activity.MainActivity;
import com.example.cjh.aboutball.db.Contract;
import com.example.cjh.aboutball.db.User;
import com.example.cjh.aboutball.recyclerview.ContractMainAdapter;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class FragmentMain extends Fragment{

    private EditText searchByGroupname;
    private Spinner searchByType;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private ImageButton searchContract;
    private Button titleBack;
    private Button titleCreate;

    private String ballType = "全部";
    private String groupName;
    private List<Contract> contractList = new ArrayList<>();
    private ContractMainAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);
        searchByGroupname = (EditText) view.findViewById(R.id.search_by_groupname);
        searchByType = (Spinner) view.findViewById(R.id.search_by_type);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.main_swipe_refresh);
        searchContract = (ImageButton) view.findViewById(R.id.search_contract);
        titleBack = (Button) view.findViewById(R.id.title_back);
        titleBack.setVisibility(view.INVISIBLE);
        titleCreate = (Button) view.findViewById(R.id.title_create);
        titleCreate.setVisibility(View.VISIBLE);
        swipeRefresh.setColorSchemeResources(R.color.orange);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        recyclerView = (RecyclerView) view.findViewById(R.id.contract_recycler_view);
        QueryByType();  //查询约单，默认为查询所有未完成约单（类型默认全部）
        searchByGroupname.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.search|| id == EditorInfo.IME_NULL) {
                    QueryByGroupname();
                    return true;
                }
                return false;
            }
        });
        searchContract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueryByGroupname();
            }
        });
        searchByType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ballType = parent.getItemAtPosition(position).toString();
                QueryByType();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        titleCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MainActivity activity = (MainActivity) getActivity();
                BmobQuery<User> query = new BmobQuery<User>();
                query.getObject(activity.getNowUserId(), new QueryListener<User>() {
                    @Override
                    public void done(User user, BmobException e) {
                        if(e == null){
                            if(user.getUserCredit() < 0){
                                Toast.makeText(getActivity(), "您的信用值过低，无法创建约单!", Toast.LENGTH_SHORT).show();
                            }else{
                                Intent intent = new Intent(view.getContext(), CreateContractActivity.class);
                                intent.putExtra("user_id", activity.getNowUserId());
                                startActivity(intent);
                            }
                        }else{
                            Toast.makeText(getActivity(), "查询1失败!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        return view;
    }

    //通过运动类型查询约单
    private void QueryByType(){
        if(ballType.equals("全部")){
            QueryAllContract();
        }else{
            BmobQuery<Contract> query1 = new BmobQuery<Contract>();
            query1.addWhereEqualTo("ballType", ballType);
            BmobQuery<Contract> query2 = new BmobQuery<Contract>();
            query2.addWhereEqualTo("status", 0);
            List<BmobQuery<Contract>> queries = new ArrayList<BmobQuery<Contract>>();
            queries.add(query1);
            queries.add(query2);
            BmobQuery<Contract> mainQuery = new BmobQuery<Contract>();
            mainQuery.and(queries);
            mainQuery.findObjects(new FindListener<Contract>() {
                @Override
                public void done(List<Contract> list, BmobException e) {
                    if(e == null){
                        contractList = list;
                        bindRecyclerView();
                    }else{
                        Toast.makeText(getActivity(), "查询2失败!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    //通过队名查询约单
    private void QueryByGroupname(){
        if(TextUtils.isEmpty(searchByGroupname.getText())){
            QueryByType();
        }else{
            groupName = searchByGroupname.getText().toString();
            BmobQuery<Contract> query1 = new BmobQuery<Contract>();
            query1.addWhereEqualTo("groupName", groupName);
            BmobQuery<Contract> query2 = new BmobQuery<Contract>();
            query2.addWhereEqualTo("status", 0);
            List<BmobQuery<Contract>> queries = new ArrayList<BmobQuery<Contract>>();
            queries.add(query1);
            queries.add(query2);
            BmobQuery<Contract> mainQuery = new BmobQuery<Contract>();
            mainQuery.and(queries);
            mainQuery.findObjects(new FindListener<Contract>() {
                @Override
                public void done(List<Contract> list, BmobException e) {
                    if(e == null){
                        contractList = list;
                        bindRecyclerView();
                    }else{
                        Toast.makeText(getActivity(), "查询2失败!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    //查询所有约单
    private void QueryAllContract(){
        BmobQuery<Contract> query = new BmobQuery<Contract>();
        query.addWhereEqualTo("status", 0);
        query.findObjects(new FindListener<Contract>() {
            @Override
            public void done(List<Contract> list, BmobException e) {
                if(e == null){
                    contractList = list;
                    //绑定RecyclerView显示约单
                    bindRecyclerView();
                }else{
                    Toast.makeText(getActivity(), "查询3失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void bindRecyclerView(){
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ContractMainAdapter(contractList);
        recyclerView.setAdapter(adapter);
    }
    //下拉刷新
    private void refresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                QueryByType();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }
}