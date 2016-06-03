package com.ksfc.newfarmer.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.utils.StringUtil;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * Created by HePeng on 2016/1/8.
 */
public class BigImageFragment extends BaseFragment {

    @Override
    public View InItView() {
        View view = inflater.inflate(R.layout.pic_layout_scale, null);
        final PhotoView photoView = (PhotoView) view.findViewById(R.id.photoView);

        Bundle bundle = getArguments();
        final String originalUrl = bundle.getString("originalUrl");
        //先加载缓存里的图片
        if (StringUtil.checkStr(originalUrl)) {
            Picasso.with(activity).load(MsgID.IP + originalUrl)
                    .config(Bitmap.Config.RGB_565).into(photoView);
        }
        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {

                activity.finish();
                int version = Integer.valueOf(android.os.Build.VERSION.SDK);
                if (version > 5) {
                    activity.overridePendingTransition(R.anim.animation_none, R.anim.zoom_exit);
                }
            }

            @Override
            public void onOutsidePhotoTap() {

            }
        });


        return view;
    }

    @Override
    public void onResponsed(Request req) {

    }

    @Override
    public void OnViewClick(View v) {

    }
}
