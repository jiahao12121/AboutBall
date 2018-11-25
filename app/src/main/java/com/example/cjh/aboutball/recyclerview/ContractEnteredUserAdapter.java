package com.example.cjh.aboutball.recyclerview;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cjh.aboutball.R;
import com.example.cjh.aboutball.activity.ContractDetailActivity;

import com.example.cjh.aboutball.activity.InforPersonalActivity;
import com.example.cjh.aboutball.db.User;
import com.example.cjh.aboutball.db.UserEnterContract;
import com.example.cjh.aboutball.util.ImageLoaderApplication;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by cjh on 2018/8/7.*/



public class ContractEnteredUserAdapter extends RecyclerView.Adapter<ContractEnteredUserAdapter.ViewHolder>{

    private List<UserEnterContract> UserEnteredList;

    static class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView userHeadIcon;

        TextView userName;

        View ContractView;      //保存RecyclerView子项，用于点击后跳转

        public ViewHolder(View view){
            super(view);
            userHeadIcon = (CircleImageView) view.findViewById(R.id.user_enter_head_icon);
            userName = (TextView) view.findViewById(R.id.user_enter_name);
            ContractView = view;
        }
    }

    public ContractEnteredUserAdapter(List<UserEnterContract> userEnteredList){
        UserEnteredList = userEnteredList;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_entered_recycler_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.ContractView.setOnClickListener(new View.OnClickListener() {
            @Override
            /*点击某参与用户子项后跳转到该用户个人信息界面*/
            public void onClick(View v) {
                ContractDetailActivity contractDetailActivity = (ContractDetailActivity) v.getContext();
                int position = holder.getAdapterPosition();
                UserEnterContract uec = UserEnteredList.get(position);
                Intent intent = new Intent(v.getContext(), InforPersonalActivity.class);
                intent.putExtra("personal_id", uec.getUserId() + "");
                intent.putExtra("user_id", contractDetailActivity.getNowUserId());
                v.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        UserEnterContract uec = UserEnteredList.get(position);
        Log.d("ContractUserAdapter", uec.getUserId() + "");
        /*由用户Id找到对应头像*/
        BmobQuery<User> query = new BmobQuery<User>();
        query.getObject(uec.getUserId(), new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if(e == null){
                    holder.userName.setText(user.getUserName());
                    if(user.getHeadIcon() != null){
                        ImageLoader imageLoader = ImageLoader.getInstance();
                        imageLoader.displayImage(user.getHeadIcon().getFileUrl(), holder.userHeadIcon, ImageLoaderApplication.options);
                    }
                }else{
                    Log.d("ContractEnteredUser", "error");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return UserEnteredList.size();
    }

}
