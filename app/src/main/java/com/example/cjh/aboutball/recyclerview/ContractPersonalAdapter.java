package com.example.cjh.aboutball.recyclerview;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cjh.aboutball.R;
import com.example.cjh.aboutball.activity.ContractDetailActivity;
import com.example.cjh.aboutball.activity.InforPersonalActivity;
import com.example.cjh.aboutball.db.Contract;

import java.util.List;


/**
 * Created by cjh on 2018/8/15.
 */

public class ContractPersonalAdapter extends RecyclerView.Adapter<ContractPersonalAdapter.ViewHolder> {

    private List<Contract> ContractList;

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView ballTime;

        TextView ballType;

        TextView ballAddress;

        TextView maxPum;

        View ContractView;      //保存RecyclerView子项，用于点击后跳转

        public ViewHolder(View view){
            super(view);
            ballTime = (TextView) view.findViewById(R.id.personal_ball_time);
            ballType = (TextView) view.findViewById(R.id.personal_ball_type);
            ballAddress = (TextView) view.findViewById(R.id.personal_ball_address);
            maxPum = (TextView) view.findViewById(R.id.personal_ball_pnum);
            ContractView = view;
        }
    }

    public ContractPersonalAdapter(List<Contract> contractList){
        ContractList = contractList;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.personal_contract_recycler_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.ContractView.setOnClickListener(new View.OnClickListener() {
            @Override
            /*点击某约单子项后跳转到约单详情界面*/
            public void onClick(View v) {
                InforPersonalActivity inforPersonalActivity = (InforPersonalActivity) v.getContext();
                int position = holder.getAdapterPosition();
                Contract contract = ContractList.get(position);
                Intent intent = new Intent(v.getContext(), ContractDetailActivity.class);
                intent.putExtra("contract_id", contract.getId()+"");
                intent.putExtra("user_id", inforPersonalActivity.getNowUserId());
                v.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Contract contract = ContractList.get(position);
        holder.ballTime.setText(contract.getBallTime().split(" ")[0]);
        holder.ballType.setText(contract.getBallType());
        holder.ballAddress.setText(contract.getBallAddress());
        holder.maxPum.setText(contract.getMaxPum() + "人");
    }

    @Override
    public int getItemCount() {
        return ContractList.size();
    }
}
