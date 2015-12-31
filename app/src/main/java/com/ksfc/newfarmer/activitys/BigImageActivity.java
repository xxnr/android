package com.ksfc.newfarmer.activitys;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.protocol.Request;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by CAI on 2015/12/7.
 */
public class BigImageActivity extends BaseActivity {
    private PhotoView photoView;

    @Override
    public int getLayout() {
        return R.layout.big_image_detail_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        photoView = ((PhotoView) findViewById(R.id.photoView));
        String imageUrl = getIntent().getStringExtra("image");
        if (!TextUtils.isEmpty(imageUrl)) {
            ImageLoader.getInstance().displayImage(MsgID.IP + imageUrl, photoView);
        }
        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                finish();
            }
        });
        setViewClick(R.id.image_activity_back);
    }

    @Override
    public void OnViewClick(View v) {

        switch (v.getId()) {
            case R.id.image_activity_back:
                finish();
                break;
        }
    }

    @Override
    public void onResponsed(Request req) {

    }
}
