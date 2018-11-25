package com.example.cjh.aboutball.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cjh.aboutball.R;
import com.example.cjh.aboutball.activity.MainActivity;
import com.example.cjh.aboutball.activity.MyContractActivity;
import com.example.cjh.aboutball.activity.MyInformationActivity;
import com.example.cjh.aboutball.activity.PhotoWallActivity;

import com.example.cjh.aboutball.activity.SafeCenterActivity;
import com.example.cjh.aboutball.db.User;
import com.example.cjh.aboutball.listview.UserListItem;
import com.example.cjh.aboutball.listview.UserListItemAdapter;
import com.example.cjh.aboutball.util.ImageLoaderApplication;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import de.hdodenhof.circleimageview.CircleImageView;


public class FragmentUser extends Fragment {

    private CircleImageView headIcon;
    private TextView titleText;
    private Button titleBack;
    private TextView userName;
    private TextView userSex;
    private TextView userAge;
    private TextView userIntro;

    private List<UserListItem> itemList = new ArrayList<>();

    private static final int QUERY_SUCCESS = 0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_user, container, false);
        titleText = (TextView) view.findViewById(R.id.title_text);
        titleBack = (Button) view.findViewById(R.id.title_back);
        userName = (TextView) view.findViewById(R.id.user_person_name);
        userSex = (TextView) view.findViewById(R.id.user_sex);
        userAge = (TextView) view.findViewById(R.id.user_age);
        userIntro = (TextView) view.findViewById(R.id.user_intro);
        headIcon = (CircleImageView) view.findViewById(R.id.head_icon);
        titleText.setText("个人中心");
        titleBack.setVisibility(View.INVISIBLE);
        initItem();
        QueryUser();
        //加载listview以及处理点击事件
        UserListItemAdapter adapter = new UserListItemAdapter(view.getContext(),
                R.layout.user_list_item, itemList);
        ListView listView = (ListView) view.findViewById(R.id.list_view_user);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserListItem userListItem = itemList.get(position);
                MainActivity activity = (MainActivity) getActivity();
                Intent intent;
                switch (userListItem.getName()){
                    case "我的信息":
                        intent = new Intent(view.getContext(), MyInformationActivity.class);
                        intent.putExtra("user_id", activity.getNowUserId());
                        startActivity(intent);
                        break;
                    case "我的约单":
                        intent = new Intent(view.getContext(), MyContractActivity.class);
                        intent.putExtra("user_id", activity.getNowUserId());
                        startActivity(intent);
                        break;
                    /*case "照片墙":
                        intent = new Intent(view.getContext(), PhotoWallActivity.class);
                        startActivity(intent);
                        break;*/
                    case "设置":
                        intent = new Intent(view.getContext(), SafeCenterActivity.class);
                        intent.putExtra("user_id", activity.getNowUserId());
                        startActivity(intent);
                        break;
                }
            }
        });
        return view;
    }

    private void QueryUser(){    /* 通过传来的用户id到服务器查询对应用户对象*/
        MainActivity activity = (MainActivity) getActivity();
        BmobQuery<User> query = new BmobQuery<User>();
        query.getObject(activity.getNowUserId(), new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if(e == null){
                    userName.setText(user.getUserName());
                    userSex.setText(user.getUserSex());
                    if(user.getUserAge() == 0){
                        userAge.setText("");
                    }else{
                        String age = user.getUserAge() + "岁";
                        userAge.setText(age);
                    }
                    userIntro.setText(user.getUserIntro());
                    //设置头像
                    if(user.getHeadIcon() != null){
                        ImageLoader imageLoader = ImageLoader.getInstance();
                        imageLoader.displayImage(user.getHeadIcon().getFileUrl(), headIcon, ImageLoaderApplication.options);
                    }
                }else{
                    Toast.makeText(getActivity(), "查询失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("FragmentUser", e.getMessage());
                }
            }
        });
    }
    private void initItem(){
        UserListItem myInfo = new UserListItem(R.drawable.ic_myinfo, "我的信息", R.drawable.ic_right);
        itemList.add(myInfo);
        UserListItem myContract = new UserListItem(R.drawable.ic_recent_exercise, "我的约单", R.drawable.ic_right);
        itemList.add(myContract);
        /*UserListItem photoWall = new UserListItem(R.drawable.ic_photo_wall, "照片墙", R.drawable.ic_right);
        itemList.add(photoWall);*/
        UserListItem safeSetting = new UserListItem(R.drawable.ic_safe_setting, "设置", R.drawable.ic_right);
        itemList.add(safeSetting);
    }
}