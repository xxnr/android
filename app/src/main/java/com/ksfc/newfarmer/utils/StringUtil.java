package com.ksfc.newfarmer.utils;


import net.yangentao.util.StrBuilder;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringUtil {

    // 判断字符串的合法性
    public static boolean checkStr(String str) {
        if (null == str) {
            return false;
        }
        if ("".equals(str)) {
            return false;
        }
        if ("".equals(str.trim())) {
            return false;
        }
        if ("null".equals(str)) {
            return false;
        }

        return true;
    }

    // 判断字符串组的合法性 并拼接
    public static String checkBufferStr(String str1, String str2, String str3, String str4) {
        StringBuilder buffer = new StringBuilder();
        if (StringUtil.checkStr(str1)) {
            buffer.append(str1);
        }
        if (StringUtil.checkStr(str2)) {
            buffer.append(str2);
        }
        if (StringUtil.checkStr(str3)) {
            buffer.append(str3);
        }
        if (StringUtil.checkStr(str4)) {
            buffer.append(str4);
        }
        return buffer.toString();
    }

    // 判断字符串组的合法性 （带空格）
    public static String checkBufferStrWithSpace(String str1, String str2, String str3, String str4, String str5) {
        StringBuilder buffer = new StringBuilder();
        if (StringUtil.checkStr(str1)) {
            buffer.append(str1).append(" ");
        }
        if (StringUtil.checkStr(str2)) {
            buffer.append(str2).append(" ");
        }
        if (StringUtil.checkStr(str3)) {
            buffer.append(str3).append(" ");
        }
        if (StringUtil.checkStr(str4)) {
            if (!str4.equals("undefined")) {
                buffer.append(str4).append(" ");
            }
        }
        if (StringUtil.checkStr(str5)) {
            buffer.append(str5);
        }
        return buffer.toString();
    }


    // 将float转换成浮点型保留两位小数的字符串
    public static String toTwoString(String str) {
        try {
            Double v = Double.parseDouble(str);
            DecimalFormat df = new DecimalFormat("0.00");
            return String.valueOf(df.format(v));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }


    // 将元转换为分
    public static String unitToCent(String unit) {


        Double v = Double.parseDouble(unit);
        return (String.valueOf((int)(v * 100)));
    }

    // 正则判断一个字符串是否全是数字
    public static boolean isNumeric(String str) {
        if (!checkStr(str))
            return false;
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }


    // 判断密码格式是否正确 6-13位
    public static boolean isPassword(String password) {
        return password.length() >= 6 || password.length() <= 13;
    }


    /**
     * 去掉double类型尾部的0
     */
    public static String reduceDouble(double price) {
        int i = (int) price;
        if (i == price) {
            return i + "";
        } else {
            return StringUtil.toTwoString(price + "");
        }
    }

    /**
     * 不足前面补位0
     *
     * @param str
     * @param strLength
     * @return
     */
    public static String addZeroForNum(String str, int strLength) {
        int strLen = str.length();
        if (strLen < strLength) {
            while (strLen < strLength) {
                StrBuilder sb = new StrBuilder();
                sb.append("0").append(str);// 左补0
                str = sb.toString();
                strLen = str.length();
            }
        }
        return str;
    }


    /**
     * 验证字符串是否为空
     *
     * @param param
     * @return
     */
    public static boolean empty(String param) {
        return param == null || param.trim().length() < 1;
    }


}
