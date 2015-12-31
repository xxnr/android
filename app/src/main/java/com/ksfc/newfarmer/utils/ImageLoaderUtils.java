package com.ksfc.newfarmer.utils;

import android.content.Context;

import com.ksfc.newfarmer.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * Created by CAI on 2015/12/18.
 */
public class ImageLoaderUtils {

    public static DisplayImageOptions buildImageOptions(Context mContext) {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(null)
                .showImageForEmptyUri(R.drawable.error)
                .showImageOnFail(R.drawable.error).considerExifParams(true)
                .showImageOnLoading(R.drawable.zhanweitu_wide)
                .cacheInMemory(true).cacheOnDisc(true).build();

        return defaultOptions;
    }

    public static DisplayImageOptions buildImageOptionsBanner(Context mContext) {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(null)
                .showImageForEmptyUri(R.drawable.error)
                .showImageOnFail(R.drawable.error).considerExifParams(true)
                .showImageOnLoading(R.drawable.banner_load)
                .cacheInMemory(true).cacheOnDisc(true).build();
        return defaultOptions;
    }

}
