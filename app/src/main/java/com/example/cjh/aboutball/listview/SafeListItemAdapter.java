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
 * Created by cjh on 2018/8/1.
 */

public class SafeListItemAdapter extends ArrayAdapter<SafeListItem> {

    private int resouceId;

    public SafeListItemAdapter(Context context, int textViewResouceId,
                               List<SafeListItem> objects){
        super(context, textViewResouceId, objects);
        resouceId = textViewResouceId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SafeListItem safeListItem = getItem(position);
        View view;
        SafeListItemAdapter.ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resouceId, parent, false);
            viewHolder = new SafeListItemAdapter.ViewHolder();
            viewHolder.safeListItemName = (TextView) view.findViewById(R.id.safe_list_item_name);
            viewHolder.safeListItemImageRight = (ImageView) view.findViewById(R.id.safe_list_item_image_right);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (SafeListItemAdapter.ViewHolder) view.getTag();
        }
        viewHolder.safeListItemName.setText(safeListItem.getName());
        viewHolder.safeListItemImageRight.setImageResource(safeListItem.getImageIdRight());
        return view;
    }

    class ViewHolder{

        TextView safeListItemName;

        ImageView safeListItemImageRight;
    }

}
