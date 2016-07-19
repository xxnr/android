package com.ksfc.newfarmer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.bumptech.glide.Glide;
import com.ksfc.newfarmer.BaseFragment;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.utils.StringUtil;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * Created by HePeng on 2016/1/8.
 */
public class BigImageFragment extends BaseFragment implements PhotoViewAttacher.OnPhotoTapListener {

    private static final String ARG_PARAM1 = "param1";

    private String originalUrl;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            originalUrl = getArguments().getString(ARG_PARAM1);
        }
    }

    public static BigImageFragment newInstance(String param1) {
        BigImageFragment fragment = new BigImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View InItView() {
        View view = inflater.inflate(R.layout.activity_big_pic, null);
        final PhotoView photoView = (PhotoView) view.findViewById(R.id.photoView);
        //先加载缓存里的图片

        if (StringUtil.checkStr(originalUrl)) {
            Glide.with(BigImageFragment.this)
                    .load(MsgID.IP + originalUrl)
                    .placeholder(R.drawable.zhanweitu)
                    .error(R.drawable.zhanweitu)
                    .into(photoView);
        }
        photoView.setOnPhotoTapListener(this);
        return view;
    }

    @Override
    public void onResponsed(Request req) {

    }

    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public void onPhotoTap(View view, float x, float y) {
        activity.finish();
    }

    @Override
    public void onOutsidePhotoTap() {

    }
}
