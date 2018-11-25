package com.example.cjh.aboutball.recyclerview;

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
import com.example.cjh.aboutball.activity.ContractDetailCreateActivity;
import com.example.cjh.aboutball.activity.MyCreateActivity;
import com.example.cjh.aboutball.db.Contract;
import com.example.cjh.aboutball.db.User;
import com.example.cjh.aboutball.db.UserEnterContract;
import com.example.cjh.aboutball.util.ImageLoaderApplication;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.litepal.crud.DataSupport;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by cjh on 2018/8/13.*/



public class ContractMyCreateAdapter extends RecyclerView.Adapter<ContractMyCreateAdapter.ViewHolder> {

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

        Button contractCancel;

        Button contractDetail;

        View ContractView;      //保存RecyclerView子项，用于点击后跳转

        public ViewHolder(View view){
            super(view);
            userHeadIcon = (CircleImageView) view.findViewById(R.id.my_create_head_icon);
            userName = (TextView) view.findViewById(R.id.my_create_user_name);
            GroupName = (TextView) view.findViewById(R.id.my_create_group_name);
            ballTime = (TextView) view.findViewById(R.id.my_create_ball_time);
            ballType = (TextView) view.findViewById(R.id.my_create_ball_type);
            ballAddress = (TextView) view.findViewById(R.id.my_create_ball_address);
            ballRemark = (TextView) view.findViewById(R.id.my_create_ball_remark);
            nowPum = (TextView) view.findViewById(R.id.my_create_ball_pnow);
            maxPum = (TextView) view.findViewById(R.id.my_create_ball_pnum);
            contractStatus = (TextView) view.findViewById(R.id.my_create_status);
            contractCancel = (Button) view.findViewById(R.id.my_create_cancel);
            contractDetail = (Button) view.findViewById(R.id.my_create_detail);
            ContractView = view;
        }
    }

    public ContractMyCreateAdapter(List<Contract> contractList){
        ContractList = contractList;
    }

    @Override
    public ContractMyCreateAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_create_recycler_item, parent, false);
        final ContractMyCreateAdapter.ViewHolder holder = new ContractMyCreateAdapter.ViewHolder(view);

        final MyCreateActivity myCreateActivity = (MyCreateActivity) parent.getContext();
        holder.contractCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(parent.getContext())
                .setTitle("提示")
                .setMessage("您确认要取消该约单吗？")
                .setCancelable(false)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        BmobQuery<User> query = new BmobQuery<User>();
                        query.getObject(myCreateActivity.getNowUserId(), new QueryListener<User>() {
                            @Override
                            public void done(User user, BmobException e) {
                                if(e == null){
                                    final int newCredit = user.getUserCredit() - 30;
                                    Log.d("asd",newCredit + "");
                                    //若当前信用值低于30（取消当前约单后会扣至0以下），发出警告
                                    if(user.getUserCredit() < 30){
                                        AlertDialog dialog = new AlertDialog.Builder(parent.getContext())
                                        .setTitle("警告")
                                        .setMessage("取消后您的信用值将低于0，是否继续？")
                                        .setCancelable(false)
                                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                int position = holder.getAdapterPosition();
                                                cancelContract(position, myCreateActivity, newCredit);
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
                                        cancelContract(position, myCreateActivity, newCredit);
                                    }
                                }else{
                                    Toast.makeText(myCreateActivity, "查询5失败！", Toast.LENGTH_SHORT).show();
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

            }
        });

        holder.contractDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyCreateActivity myCreateActivity =  (MyCreateActivity) v.getContext();
                int position = holder.getAdapterPosition();
                Contract contract = ContractList.get(position);
                Intent intent = new Intent(v.getContext(), ContractDetailCreateActivity.class);
                intent.putExtra("user_id", myCreateActivity.getNowUserId());
                intent.putExtra("contract_id", contract.getObjectId());
                intent.putExtra("contract_status", contract.getStatus()+"");
                v.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    private void cancelContract(int position, final MyCreateActivity activity, final int newCredit){
        final Contract contract = ContractList.get(position);
                                        /*删除约单表中对应约单，并解除用户约单表中该单和参与用户的关系*/
        Contract de_contract = new Contract();
        de_contract.setObjectId(contract.getObjectId());
        de_contract.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    Toast.makeText(activity, "取消约单成功!", Toast.LENGTH_SHORT).show();
                    //先删除约单表中该单信息
                    BmobQuery<UserEnterContract> query1 = new BmobQuery<UserEnterContract>();
                    query1.addWhereEqualTo("contractId", contract.getObjectId());
                    query1.findObjects(new FindListener<UserEnterContract>() {
                        @Override
                        public void done(List<UserEnterContract> list, BmobException e) {
                            if(e == null){
                                //获取用户约单表中所有参与该约单的子项信息，并依次删除
                                for(int i = 0; i < list.size(); i++){
                                    UserEnterContract de_userEnterContract = new UserEnterContract();
                                    de_userEnterContract.setObjectId(list.get(i).getObjectId());
                                    de_userEnterContract.delete(new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if(e == null){
                                                User user1 = new User();
                                                user1.setUserCredit(newCredit);
                                                user1.update(activity.getNowUserId(), new UpdateListener() {
                                                    @Override
                                                    public void done(BmobException e) {
                                                        if(e == null){
                                                            activity.QueryMyCreateContract();
                                                            Toast.makeText(activity, "取消约单成功!", Toast.LENGTH_SHORT).show();
                                                        }else{
                                                            Toast.makeText(activity, "查询1失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }else{
                                                Toast.makeText(activity, "查询2失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
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
    }
    @Override
    public void onBindViewHolder(final ContractMyCreateAdapter.ViewHolder holder, int position) {
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
