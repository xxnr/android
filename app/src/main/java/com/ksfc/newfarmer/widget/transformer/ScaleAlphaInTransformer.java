package com.ksfc.newfarmer.widget.transformer;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.view.View;

public class ScaleAlphaInTransformer extends BasePageTransformer
{
    private static final float DEFAULT_MIN_SCALE = 0.85f;
    private float mMinScale = DEFAULT_MIN_SCALE;

    private static final float DEFAULT_MIN_ALPHA = 0.8f;
    private float mMinAlpha = DEFAULT_MIN_ALPHA;

    public ScaleAlphaInTransformer()
    {

    }

    public ScaleAlphaInTransformer(float minScale, float minAlpha)
    {
        this(minAlpha,minScale, NonPageTransformer.INSTANCE);
    }

    public ScaleAlphaInTransformer(ViewPager.PageTransformer pageTransformer)
    {
        this(DEFAULT_MIN_ALPHA,DEFAULT_MIN_SCALE, pageTransformer);
    }


    public ScaleAlphaInTransformer(float minAlpha, float minScale, ViewPager.PageTransformer pageTransformer)
    {
        mMinAlpha = minAlpha;
        mMinScale = minScale;
        mPageTransformer = pageTransformer;
    }




    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void pageTransform(View view, float position)
    {
        if (position < -1)
        { // [-Infinity,-1)
            view.setAlpha(mMinAlpha);
        } else if (position <= 1)
        { // [-1,1]

            if (position < 0) //[0，-1]
            {           //[1,min]
                float factor = mMinAlpha + (1 - mMinAlpha) * (1 + position);
                view.setAlpha(factor);
            } else//[1，0]
            {
                //[min,1]
                float factor = mMinAlpha + (1 - mMinAlpha) * (1 - position);
                view.setAlpha(factor);
            }
        } else
        { // (1,+Infinity]
            view.setAlpha(mMinAlpha);
        }


        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();

        view.setPivotY(pageHeight / 2);
        view.setPivotX(pageWidth / 2);
        if (position < -1)
        { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setScaleX(mMinScale);
            view.setScaleY(mMinScale);
            view.setPivotX(pageWidth);
        } else if (position <= 1)
        { // [-1,1]
            // Modify the default slide transition to shrink the page as well
            if (position < 0) //1-2:1[0,-1] ;2-1:1[-1,0]
            {

                float scaleFactor = (1 + position) * (1 - mMinScale) + mMinScale;
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                view.setPivotX(pageWidth * (DEFAULT_CENTER + (DEFAULT_CENTER * -position)));

            } else //1-2:2[1,0] ;2-1:2[0,1]
            {
                float scaleFactor = (1 - position) * (1 - mMinScale) + mMinScale;
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
                view.setPivotX(pageWidth * ((1 - position) * DEFAULT_CENTER));
            }


        } else
        { // (1,+Infinity]
            view.setPivotX(0);
            view.setScaleX(mMinScale);
            view.setScaleY(mMinScale);
        }
    }
}
