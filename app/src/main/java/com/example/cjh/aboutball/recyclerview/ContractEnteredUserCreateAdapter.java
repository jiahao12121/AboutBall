package com.example.cjh.aboutball.recyclerview;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cjh.aboutball.R;
import com.example.cjh.aboutball.activity.ContractDetailActivity;

import com.example.cjh.aboutball.activity.ContractDetailCreateActivity;
import com.example.cjh.aboutball.activity.InforPersonalActivity;
import com.example.cjh.aboutball.db.User;
import com.example.cjh.aboutball.db.UserEnterContract;
import com.example.cjh.aboutball.util.ImageLoaderApplication;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by cjh on 2018/8/7.*/



public class ContractEnteredUserCreateAdapter extends RecyclerView.Adapter<ContractEnteredUserCreateAdapter.ViewHolder>{

    private List<UserEnterContract> UserEnteredList;

    static class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView userHeadIcon;

        TextView userName;

        TextView signOn;

        public ViewHolder(View view){
            super(view);
            userHeadIcon = (CircleImageView) view.findViewById(R.id.user_c_enter_head_icon);
            userName = (TextView) view.findViewById(R.id.user_c_enter_name);
            signOn = (TextView) view.findViewById(R.id.sign_on);
        }
    }

    public ContractEnteredUserCreateAdapter(List<UserEnterContract> userEnteredList){
        UserEnteredList = userEnteredList;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_c_entered_recycler_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        final ContractDetailCreateActivity contractDetailCreateActivity = (ContractDetailCreateActivity) parent.getContext();
        holder.userHeadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            /*点击某参与用户子项后跳转到该用户个人信息界面*/
            public void onClick(View v) {
                ContractDetailCreateActivity contractDetailCreateActivity = (ContractDetailCreateActivity) v.getContext();
                int position = holder.getAdapterPosition();
                UserEnterContract uec = UserEnteredList.get(position);
                Intent intent = new Intent(v.getContext(), InforPersonalActivity.class);
                intent.putExtra("personal_id", uec.getUserId() + "");
                intent.putExtra("user_id", contractDetailCreateActivity.getNowUserId());
                v.getContext().startActivity(intent);
            }
        });
        holder.signOn.setOnClickListener(new View.OnClickListener() {
            @Override
            //点击给他签到按钮后显示已签到，且字体变绿
            public void onClick(View v) {
                if(contractDetailCreateActivity.getNowContractStatus().equals("0")){
                    int position = holder.getAdapterPosition();
                    UserEnterContract uec = UserEnteredList.get(position);
                    if(holder.signOn.getText().equals("给他签到")){
                        holder.signOn.setText("已签到");
                        holder.signOn.setTextColor(Color.parseColor("#32CD32"));
                        UserEnterContract userEnterContract = new UserEnterContract();
                        userEnterContract.setUserStatus(1);
                        userEnterContract.update(uec.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e == null){
                                }else{
                                    Toast.makeText(contractDetailCreateActivity, "查询失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }else{
                    Toast.makeText(contractDetailCreateActivity, "该约单已完成，签到不可用!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final UserEnterContract uec = UserEnteredList.get(position);
        Log.d("ContractUserCAdapter", uec.getUserId() + "");
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
                    if(uec.getUserStatus() == 1){
                        holder.signOn.setText("已签到");
                        holder.signOn.setTextColor(Color.parseColor("#32CD32"));
                    }
                }else{
                    Log.d("ContractEnteredCUser", "error");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return UserEnteredList.size();
    }

}
