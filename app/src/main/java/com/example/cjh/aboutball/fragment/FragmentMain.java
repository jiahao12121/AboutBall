package com.example.cjh.aboutball.fragment;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.cjh.aboutball.R;
import com.example.cjh.aboutball.db.Contract;
import com.example.cjh.aboutball.recyclerview.ContractMainAdapter;
import com.example.cjh.aboutball.util.SpaceItemDecoration;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class FragmentMain extends Fragment{

    private List<Contract> contractList = new ArrayList<>();

    private SwipeRefreshLayout swipeRefresh;

    private RecyclerView recyclerView;

    private ContractMainAdapter adapter;

    private Button titleMenu;

    private Button titleBack;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.main_swipe_refresh);
        titleMenu = (Button) view.findViewById(R.id.title_menu);
        titleMenu.setVisibility(view.VISIBLE);
        titleBack = (Button) view.findViewById(R.id.title_back);
        titleBack.setVisibility(view.INVISIBLE);
        swipeRefresh.setColorSchemeResources(R.color.orange);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        titleMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(view.getContext(),"aaa",Toast.LENGTH_SHORT).show();
            }
        });
        contractList = DataSupport.findAll(Contract.class);
        /*设置约单的RecyclerView*/
        recyclerView = (RecyclerView) view.findViewById(R.id.contract_recycler_view);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ContractMainAdapter(contractList);
        recyclerView.setAdapter(adapter);
        return view;
    }
    private void refresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        contractList = DataSupport.findAll(Contract.class);
                        adapter = new ContractMainAdapter(contractList);
                        recyclerView.setAdapter(adapter);
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }
}