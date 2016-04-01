package com.ksfc.newfarmer;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 启动页的Fragment
 *
 * @author Bruce.wang
 */
public class GuideFragment extends Fragment {

    private int index;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        index = bundle.getInt("index");
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
                        startActivity(new Intent(getActivity(), MainActivity.class));
                        getActivity().finish();
                    }
                });
                break;
            default:
                break;
        }
        return view;
    }

}
