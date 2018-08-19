
package com.example.cjh.aboutball.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.cjh.aboutball.R;
import com.example.cjh.aboutball.activity.CreateContractActivity;
import com.example.cjh.aboutball.activity.MainActivity;


public class FragmentYue extends Fragment {

    private Button createContract;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_yue, container, false);
        createContract = (Button) view.findViewById(R.id.create_contract);
        createContract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) getActivity();
                Intent intent = new Intent(view.getContext(), CreateContractActivity.class);
                intent.putExtra("user_id", activity.getNowUserId());
                startActivity(intent);
            }
        });
        return view;
    }
}