package com.example.cjh.aboutball.listview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cjh.aboutball.R;

import java.util.List;

/**
 * Created by cjh on 2018/8/13.
 */

public class MyContractListItemAdapter extends ArrayAdapter<MyContractListItem> {

    private int resouceId;

    public MyContractListItemAdapter(Context context, int textViewResouceId,
                               List<MyContractListItem> objects){
        super(context, textViewResouceId, objects);
        resouceId = textViewResouceId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyContractListItem myContractListItem = getItem(position);
        View view;
        MyContractListItemAdapter.ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resouceId, parent, false);
            viewHolder = new MyContractListItemAdapter.ViewHolder();
            viewHolder.myContractListItemName = (TextView) view.findViewById(R.id.my_contract_list_item_name);
            viewHolder.myContractListItemImageRight = (ImageView) view.findViewById(R.id.my_contract_list_item_image_right);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (MyContractListItemAdapter.ViewHolder) view.getTag();
        }
        viewHolder.myContractListItemName.setText(myContractListItem.getName());
        viewHolder.myContractListItemImageRight.setImageResource(myContractListItem.getImageIdRight());
        return view;
    }

    class ViewHolder{

        TextView myContractListItemName;

        ImageView myContractListItemImageRight;
    }

}
