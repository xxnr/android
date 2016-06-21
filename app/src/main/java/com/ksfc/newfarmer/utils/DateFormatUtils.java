package com.ksfc.newfarmer.utils;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by HePeng on 2015/12/29.
 */

/**
 * 格式化时间
 */
public class DateFormatUtils {

    // 格式：年－月－日 小时：分钟：秒
    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    public static final SimpleDateFormat dspFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static final long mHourInMillis = 86400000L;


    public static String convertTime(String srcTime) {
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

    //返回UTC时间
    public static Date convertDate(String srcTime) {
        Date result_date = null;
        try { // 将输入时间字串转换为UTC时间
            sdf.setTimeZone(TimeZone.getTimeZone("GMT00:00"));
            result_date = sdf.parse(srcTime);
        } catch (Exception e) { // 出现异常时，返回空字符串
        }
        return result_date;
    }




}