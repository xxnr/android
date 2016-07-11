package com.ksfc.newfarmer.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.activitys.MainActivity;
import com.ksfc.newfarmer.utils.IntentUtil;


/**
 * 启动页的Fragment
 *
 * @author Bruce.wang
 */
public class GuideFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private int index;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_PARAM1);
        }
    }

    public static GuideFragment newInstance(int param1) {
        GuideFragment fragment = new GuideFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide, null, false);

        TextView tv_enter1 = (TextView) view.findViewById(R.id.textView_guide_enter1);
        TextView tv_enter2 = (TextView) view.findViewById(R.id.textView_guide_enter2);

        switch (index) {
            case 0:
                tv_enter1.setVisibility(View.GONE);
                tv_enter2.setVisibility(View.GONE);
                view.setBackgroundResource(R.drawable.yindaoye1);
                break;
            case 1:
                tv_enter1.setVisibility(View.GONE);
                tv_enter2.setVisibility(View.GONE);
                view.setBackgroundResource(R.drawable.yindaoye2);
                break;
            case 2:
                tv_enter1.setVisibility(View.VISIBLE);
                tv_enter2.setVisibility(View.VISIBLE);
                view.setBackgroundResource(R.drawable.yindaoye3);
                tv_enter2.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        IntentUtil.activityForward(getActivity(), MainActivity.class, null, true);
                    }
                });
                break;
            default:
                break;
        }
        return view;
    }

}
