package com.ksfc.newfarmer.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.utils.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by CAI on 2016/1/8.
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
            ImageLoader.getInstance().displayImage(MsgID.IP + picture, photoView);
        }
        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                getActivity().finish();
            }
        });
        return view;
    }

    @Override
    public void onResponsed(Request req) {

    }
}
