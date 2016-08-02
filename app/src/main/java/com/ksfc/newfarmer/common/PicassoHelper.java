package com.ksfc.newfarmer.common;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.BaseFragment;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.utils.Utils;
import com.squareup.picasso.Picasso;

/**
 * Created by CAI on 2016/7/15.
 */
public class PicassoHelper {



    public static void setImageRes(BaseFragment fragment, String imgUrl, ImageView imageView) {



        if (StringUtil.checkStr(imgUrl)) {
            if (imgUrl.contains("http")) {
                Picasso.with(fragment.activity)
                        .load(imgUrl)
                        .noFade()
                        .placeholder(R.drawable.zhanweitu)
                        .error(R.drawable.error)
                        .config(Bitmap.Config.RGB_565)
                        .resize(Utils.dip2px(fragment.activity,100),Utils.dip2px(fragment.activity,100))
                        .into(imageView);
            } else {
                Picasso.with(fragment.activity)
                        .load(MsgID.IP + imgUrl)
                        .noFade()
                        .placeholder(R.drawable.zhanweitu)
                        .error(R.drawable.error)
                        .config(Bitmap.Config.RGB_565)
                        .resize(Utils.dip2px(fragment.activity,100),Utils.dip2px(fragment.activity,100))
                        .into(imageView);
            }
        } else {
            imageView.setImageResource(R.drawable.zhanweitu);
        }
    }

    public static void setImageRes(BaseActivity activity, String imgUrl, ImageView imageView) {

        if (StringUtil.checkStr(imgUrl)) {
            if (imgUrl.contains("http")) {
                Picasso.with(activity)
                        .load(imgUrl)
                        .noFade()
                        .placeholder(R.drawable.zhanweitu)
                        .error(R.drawable.error)
                        .config(Bitmap.Config.RGB_565)
                        .into(imageView);
            } else {
                Picasso.with(activity)
                        .load(MsgID.IP + imgUrl)
                        .noFade()
                        .placeholder(R.drawable.zhanweitu)
                        .error(R.drawable.error)
                        .config(Bitmap.Config.RGB_565)
                        .into(imageView);
            }
        } else {
            imageView.setImageResource(R.drawable.zhanweitu);
        }
    }
}