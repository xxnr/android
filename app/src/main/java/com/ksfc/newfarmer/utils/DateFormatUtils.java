package com.ksfc.newfarmer.utils;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by HePeng on 2015/12/29.
 */

/**
 * 格式化时间
 */
public class DateFormatUtils {

    public static String convertTime(String srcTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        SimpleDateFormat dspFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String convertTime;
        Date result_date;
        long result_time = 0;
        // 如果传入参数异常，使用本地时间
        if (null == srcTime)// 没有传入时间时,返回空字符串
            return "";
        else {
            try { // 将输入时间字串转换为UTC时间
                sdf.setTimeZone(TimeZone.getTimeZone("GMT00:00"));
                result_date = sdf.parse(srcTime);
                result_time = result_date.getTime();
            } catch (Exception e) { // 出现异常时，返回空字符串

                return "";
            }
        }
        // 设定时区
        dspFmt.setTimeZone(TimeZone.getDefault());
        convertTime = dspFmt.format(result_time);
        return convertTime;

    }
}