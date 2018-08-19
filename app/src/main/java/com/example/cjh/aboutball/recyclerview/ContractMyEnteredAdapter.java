package com.example.cjh.aboutball.recyclerview;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
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

import org.litepal.crud.DataSupport;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * enteredd by cjh on 2018/8/14.
 */

public class ContractMyEnteredAdapter extends RecyclerView.Adapter<ContractMyEnteredAdapter.ViewHolder> {

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
        holder.contractExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyEnteredActivity myEnteredActivity = (MyEnteredActivity) v.getContext();
                final String id = myEnteredActivity.getNowUserId();
                AlertDialog.Builder dialog = new AlertDialog.Builder(parent.getContext());
                dialog.setTitle("提示");
                dialog.setMessage("您确认要退出该约单吗？");
                dialog.setCancelable(false);
                dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(parent.getContext(), "退出约单成功!", Toast.LENGTH_SHORT).show();
                        /*获取当前约单*/
                        int position = holder.getAdapterPosition();
                        Contract contract = ContractList.get(position);
                        /*当前参与人数减1，并删除用户约单表相关数据*/
                        int pnow = DataSupport.where("id = ?", contract.getId() + "").find(Contract.class).get(0).getNowPum();
                        Contract contract1 = new Contract();
                        contract1.setNowPum(pnow - 1);
                        contract1.updateAll("id = ?", contract.getId() + "");
                        DataSupport.deleteAll(UserEnterContract.class, "userId = ? and contractId = ?", id, contract.getId() + "");
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
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
                intent.putExtra("contract_id", contract.getId() + "");
                v.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ContractMyEnteredAdapter.ViewHolder holder, int position) {
        final Contract contract = ContractList.get(position);
        /*由用户ID到User表中获取用户头像和用户名*/
        List<User> result = DataSupport.where("id = ?", contract.getUserId() + "").find(User.class);
        holder.userHeadIcon.setImageResource(result.get(0).getHeadIcon());
        holder.userName.setText(result.get(0).getUserName());
        holder.GroupName.setText(contract.getGroupName());
        holder.ballTime.setText(contract.getBallTime());
        holder.ballType.setText(contract.getBallType());
        holder.ballAddress.setText(contract.getBallAddress());
        holder.ballRemark.setText(contract.getBallRemark());
        /*注意这里int型数据不能直接传递给TextView，转换为String型*/
        holder.nowPum.setText(contract.getNowPum() + "");
        holder.maxPum.setText(contract.getMaxPum() + "");
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
