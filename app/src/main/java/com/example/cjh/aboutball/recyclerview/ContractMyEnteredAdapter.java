package com.example.cjh.aboutball.recyclerview;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cjh.aboutball.R;
import com.example.cjh.aboutball.activity.ContractDetailActivity;
import com.example.cjh.aboutball.activity.MyEnteredActivity;
import com.example.cjh.aboutball.db.Contract;
import com.example.cjh.aboutball.db.User;
import com.example.cjh.aboutball.db.UserEnterContract;
import com.example.cjh.aboutball.util.ImageLoaderApplication;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * enteredd by cjh on 2018/8/14.*/



public class ContractMyEnteredAdapter extends RecyclerView.Adapter<ContractMyEnteredAdapter.ViewHolder> {

    private List<Contract> ContractList;

    private boolean isDelete = true;

    static class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView userHeadIcon;

        TextView userName;

        TextView GroupName;

        TextView ballTime;

        TextView ballType;

        TextView ballAddress;

        TextView ballRemark;

        TextView nowPum;

        TextView maxPum;

        TextView contractStatus;

        Button contractExit;

        Button contractDetail;

        View ContractView;      //保存RecyclerView子项，用于点击后跳转

        public ViewHolder(View view){
            super(view);
            userHeadIcon = (CircleImageView) view.findViewById(R.id.my_entered_head_icon);
            userName = (TextView) view.findViewById(R.id.my_entered_user_name);
            GroupName = (TextView) view.findViewById(R.id.my_entered_group_name);
            ballTime = (TextView) view.findViewById(R.id.my_entered_ball_time);
            ballType = (TextView) view.findViewById(R.id.my_entered_ball_type);
            ballAddress = (TextView) view.findViewById(R.id.my_entered_ball_address);
            ballRemark = (TextView) view.findViewById(R.id.my_entered_ball_remark);
            nowPum = (TextView) view.findViewById(R.id.my_entered_ball_pnow);
            maxPum = (TextView) view.findViewById(R.id.my_entered_ball_pnum);
            contractStatus = (TextView) view.findViewById(R.id.my_entered_status);
            contractExit = (Button) view.findViewById(R.id.my_entered_exit);
            contractDetail = (Button) view.findViewById(R.id.my_entered_detail);
            ContractView = view;
        }
    }

    public ContractMyEnteredAdapter(List<Contract> contractList){
        ContractList = contractList;
    }

    @Override
    public ContractMyEnteredAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_entered_recycler_item, parent, false);
        final ContractMyEnteredAdapter.ViewHolder holder = new ContractMyEnteredAdapter.ViewHolder(view);
        MyEnteredActivity myEnteredActivity = (MyEnteredActivity) parent.getContext();
        holder.contractExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MyEnteredActivity myEnteredActivity = (MyEnteredActivity) v.getContext();
                AlertDialog dialog = new AlertDialog.Builder(parent.getContext())
                .setTitle("提示")
                .setMessage("您确认要退出该约单吗？")
                .setCancelable(false)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BmobQuery<User> query = new BmobQuery<User>();
                        query.getObject(myEnteredActivity.getNowUserId(), new QueryListener<User>() {
                            @Override
                            public void done(User user, BmobException e) {
                                if(e == null){
                                    final int newCredit = user.getUserCredit() - 20;
                                    Log.d("asd",newCredit + "");
                                    if(user.getUserCredit() < 20){
                                        AlertDialog dialog = new AlertDialog.Builder(parent.getContext())
                                        .setTitle("警告")
                                        .setMessage("退出后您的信用值将低于0，是否继续？")
                                        .setCancelable(false)
                                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                int position = holder.getAdapterPosition();
                                                exitContract(position, myEnteredActivity, newCredit);
                                            }
                                        })
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).create();
                                        dialog.show();
                                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(18);
                                        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextSize(18);
                                    }else{
                                        int position = holder.getAdapterPosition();
                                        exitContract(position, myEnteredActivity, newCredit);
                                    }
                                }else{
                                    Toast.makeText(parent.getContext(), "查询1失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(18);
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextSize(18);
            }
        });
        holder.contractDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyEnteredActivity myEnteredActivity = (MyEnteredActivity) v.getContext();
                /*获取当前约单*/
                int position = holder.getAdapterPosition();
                Contract contract = ContractList.get(position);
                Intent intent = new Intent(v.getContext(), ContractDetailActivity.class);
                intent.putExtra("user_id", myEnteredActivity.getNowUserId());
                intent.putExtra("contract_id", contract.getObjectId());
                intent.putExtra("is_hide_enter", "yes");
                v.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    private void exitContract(int position, final MyEnteredActivity activity, final int newCredit){
        final Contract contract = ContractList.get(position);
        /*先将当前单内的参与人数减1*/
        int pnow = contract.getNowPnum();
        Contract contract1 = new Contract();
        contract1.setNowPnum(pnow - 1);
        contract1.update(contract.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    Log.d("aaa","222");
                    //这里要删除对应的用户约单关系，需要先查询出具体的用户约单子项（与查询），再执行删除操作
                    BmobQuery<UserEnterContract> query1 = new BmobQuery<UserEnterContract>();
                    query1.addWhereEqualTo("userId", activity.getNowUserId());
                    BmobQuery<UserEnterContract> query2 = new BmobQuery<UserEnterContract>();
                    query2.addWhereEqualTo("contractId", contract.getObjectId());
                    List<BmobQuery<UserEnterContract>> queries = new ArrayList<BmobQuery<UserEnterContract>>();
                    queries.add(query1);
                    queries.add(query2);
                    BmobQuery<UserEnterContract> mainQuery = new BmobQuery<UserEnterContract>();
                    mainQuery.and(queries);
                    mainQuery.findObjects(new FindListener<UserEnterContract>() {
                        @Override
                        public void done(List<UserEnterContract> list, BmobException e) {
                            if(e == null){
                                Log.d("aaa","333");
                                UserEnterContract de_userEnterContract = new UserEnterContract();
                                de_userEnterContract.setObjectId(list.get(0).getObjectId());
                                de_userEnterContract.delete(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if(e == null){
                                            Log.d("aaa","444");
                                            User user1 = new User();
                                            user1.setUserCredit(newCredit);
                                            user1.update(activity.getNowUserId(), new UpdateListener() {
                                                @Override
                                                public void done(BmobException e) {
                                                    if(e == null){
                                                        activity.QueryMyEnteredContract();
                                                        Toast.makeText(activity, "退出约单成功!", Toast.LENGTH_SHORT).show();
                                                    }else{
                                                        Toast.makeText(activity, "查询2失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                        }else{
                                            Toast.makeText(activity, "查询3失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }else{
                                Toast.makeText(activity, "查询4失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(activity, "查询5失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onBindViewHolder(final ContractMyEnteredAdapter.ViewHolder holder, int position) {
        final Contract contract = ContractList.get(position);
        /*由用户ID到User表中获取用户头像和用户名*/
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
                    Log.d("ContractMainAdapter", "查询用户失败");
                }
            }
        });
        holder.GroupName.setText(contract.getGroupName());
        holder.ballTime.setText(contract.getBallTime());
        holder.ballType.setText(contract.getBallType());
        holder.ballAddress.setText(contract.getBallAddress());
        holder.ballRemark.setText(contract.getBallRemark());
        /*注意这里int型数据不能直接传递给TextView，转换为String型*/
        holder.nowPum.setText(String.valueOf(contract.getNowPnum()));
        holder.maxPum.setText(String.valueOf(contract.getMaxPnum()));
        if(contract.getStatus() == 0){
            holder.contractStatus.setText("未完成");
        }else{
            holder.contractStatus.setText("已完成");
        }
    }

    @Override
    public int getItemCount() {
        return ContractList.size();
    }

}
