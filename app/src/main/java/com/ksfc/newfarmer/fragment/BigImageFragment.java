package com.ksfc.newfarmer.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.utils.StringUtil;
import com.squareup.picasso.Picasso;


import net.yangentao.util.app.App;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by HePeng on 2016/1/8.
 */
public class BigImageFragment extends BaseFragment {

    @Override
    public View InItView() {
        View view = inflater.inflate(R.layout.pic_layout_scale, null);
        PhotoView photoView = (PhotoView) view.findViewById(R.id.photoView);
        Bundle bundle = getArguments();
        String picture = bundle.getString("picture");
        //可以自由放大缩小图片的控键
        if (StringUtil.checkStr(picture)) {
            Picasso.with(activity).load(MsgID.IP + picture).config(Bitmap.Config.RGB_565).error(R.drawable.error).skipMemoryCache().placeholder(R.drawable.zhanweitu).into(photoView);
        }
        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                activity.finish();
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
