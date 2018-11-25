package com.example.cjh.aboutball.recyclerview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cjh.aboutball.R;

import com.example.cjh.aboutball.activity.ContractDetailActivity;
import com.example.cjh.aboutball.activity.MainActivity;
import com.example.cjh.aboutball.db.Contract;
import com.example.cjh.aboutball.db.User;
import com.example.cjh.aboutball.util.ImageLoaderApplication;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.litepal.crud.DataSupport;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by cjh on 2018/8/2.*/



public class ContractMainAdapter extends RecyclerView.Adapter<ContractMainAdapter.ViewHolder>{

    private List<Contract> ContractList;

    static class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView userHeadIcon;

        TextView userName;

        TextView GroupName;

        TextView ballTime;

        TextView ballType;

        TextView ballAddress;

        TextView ballRemark;

        TextView nowPum;

        TextView xieGang;

        TextView maxPum;

        View ContractView;      //保存RecyclerView子项，用于点击后跳转

        public ViewHolder(View view){
            super(view);
            userHeadIcon = (CircleImageView) view.findViewById(R.id.contract_head_icon);
            userName = (TextView) view.findViewById(R.id.contract_user_name);
            GroupName = (TextView) view.findViewById(R.id.contract_group_name);
            ballTime = (TextView) view.findViewById(R.id.contract_ball_time);
            ballType = (TextView) view.findViewById(R.id.contract_ball_type);
            ballAddress = (TextView) view.findViewById(R.id.contract_ball_address);
            ballRemark = (TextView) view.findViewById(R.id.contract_ball_remark);
            nowPum = (TextView) view.findViewById(R.id.contract_ball_pnow);
            xieGang = (TextView) view.findViewById(R.id.xie_gang);
            maxPum = (TextView) view.findViewById(R.id.contract_ball_pnum);
            ContractView = view;
        }
    }

    public ContractMainAdapter(List<Contract> contractList){
        ContractList = contractList;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contract_recycler_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.ContractView.setOnClickListener(new View.OnClickListener() {
            @Override
            /*点击某约单子项后跳转到约单详情界面*/
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Contract contract = ContractList.get(position);
                MainActivity activity = (MainActivity) v.getContext();
                Intent intent = new Intent(v.getContext(), ContractDetailActivity.class);
                intent.putExtra("contract_id", contract.getObjectId());
                intent.putExtra("user_id", activity.getNowUserId());
                intent.putExtra("is_hide_enter", "no");
                v.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Contract contract = ContractList.get(position);
        Log.d("ContractMainAdapter", contract.getUserId());
        BmobQuery<User> query = new BmobQuery<User>();
        query.getObject(contract.getUserId(), new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    holder.userName.setText(user.getUserName());
                    if(user.getHeadIcon() != null){
                        ImageLoader imageLoader = ImageLoader.getInstance();
                        imageLoader.displayImage(user.getHeadIcon().getFileUrl(), holder.userHeadIcon, ImageLoaderApplication.options);
                    }
                } else {
                    Log.d("ContractMainAdapter", "查询1失败");
                }
            }
        });
        holder.GroupName.setText(contract.getGroupName());
        holder.ballTime.setText(contract.getBallTime());
        holder.ballType.setText(contract.getBallType());
        holder.ballAddress.setText(contract.getBallAddress());
        holder.ballRemark.setText(contract.getBallRemark());
        /*注意这里int型数据不能直接传递给TextView，转换为String型*/
        holder.nowPum.setText(contract.getNowPnum() + "");
        holder.maxPum.setText(contract.getMaxPnum() + "");
        if(contract.getNowPnum() == contract.getMaxPnum()){
            holder.nowPum.setTextColor(Color.parseColor("#FF0000"));
            holder.maxPum.setTextColor(Color.parseColor("#FF0000"));
            holder.xieGang.setTextColor(Color.parseColor("#FF0000"));
        }
    }

    @Override
    public int getItemCount() {
        return ContractList.size();
    }

}
