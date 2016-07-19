package com.ksfc.newfarmer.common;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.utils.StringUtil;

/**
 * Created by CAI on 2016/7/15.
 */
public class GlideHelper {


    public static void setBroadImageRes(BaseActivity activity, String imgUrl, ImageView imageView) {
        if (StringUtil.checkStr(imgUrl)) {
            if (imgUrl.contains("http")) {
                Glide.with(activity)
                        .load(imgUrl)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.zhanweitu_wide)
                        .error(R.drawable.error)
                        .into(imageView);
            } else {
                Glide.with(activity)
                        .load(MsgID.IP + imgUrl)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.zhanweitu_wide)
                        .error(R.drawable.error)
                        .into(imageView);
            }
        } else {
            imageView.setImageResource(R.drawable.zhanweitu);
        }
    }



    public static void setImageRes(Context context, String imgUrl, ImageView imageView) {
        if (StringUtil.checkStr(imgUrl)) {
            if (imgUrl.contains("http")) {
                Glide.with(context)
                        .load(imgUrl)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.zhanweitu)
                        .error(R.drawable.error)
                        .into(imageView);
            } else {
                Glide.with(context)
                        .load(MsgID.IP + imgUrl)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.zhanweitu)
                        .error(R.drawable.error)
                        .into(imageView);
            }
        } else {
            imageView.setImageResource(R.drawable.zhanweitu);
        }
    }

    public static void setImageRes(Fragment fragment, String imgUrl, ImageView imageView) {
        if (StringUtil.checkStr(imgUrl)) {
            if (imgUrl.contains("http")) {
                Glide.with(fragment)
                        .load(imgUrl)
                        .crossFade()
                        .placeholder(R.drawable.zhanweitu)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.drawable.error)
                        .into(imageView);
            } else {
                Glide.with(fragment)
                        .load(MsgID.IP + imgUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.zhanweitu)
                        .error(R.drawable.error)
                        .crossFade()
                        .into(imageView);
            }
        } else {
            imageView.setImageResource(R.drawable.zhanweitu);
        }
    }

    public static void setImageRes(BaseActivity activity, String imgUrl, ImageView imageView) {
        if (StringUtil.checkStr(imgUrl)) {
            if (imgUrl.contains("http")) {
                Glide.with(activity)
                        .load(imgUrl)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.zhanweitu)
                        .error(R.drawable.error)
                        .into(imageView);
            } else {
                Glide.with(activity)
                        .load(MsgID.IP + imgUrl)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.zhanweitu)
                        .error(R.drawable.error)
                        .into(imageView);
            }
        } else {
            imageView.setImageResource(R.drawable.zhanweitu);
        }
    }
}