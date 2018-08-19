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
import com.example.cjh.aboutball.listview.UserListItem;

import java.util.List;

/**
 * Created by cjh on 2018/7/31.
 */

public class UserListItemAdapter extends ArrayAdapter<UserListItem> {

    private int resouceId;

    public UserListItemAdapter(Context context, int textViewResouceId,
                               List<UserListItem> objects){
        super(context, textViewResouceId, objects);
        resouceId = textViewResouceId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserListItem userListItem = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resouceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.userListItemImageLeft = (ImageView) view.findViewById(R.id.user_list_item_image_left);
            viewHolder.userListItemName = (TextView) view.findViewById(R.id.user_list_item_name);
            viewHolder.userListItemImageRight = (ImageView) view.findViewById(R.id.user_list_item_image_right);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.userListItemImageLeft.setImageResource(userListItem.getImageIdLeft());
        viewHolder.userListItemName.setText(userListItem.getName());
        viewHolder.userListItemImageRight.setImageResource(userListItem.getImageIdRight());
        return view;
    }

    class ViewHolder{

        ImageView userListItemImageLeft;

        TextView userListItemName;

        ImageView userListItemImageRight;
    }
}
