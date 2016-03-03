package com.ksfc.newfarmer.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    // 判断字符串的合法性
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

    // 判断字符串的合法性
    public static String checkBufferStrWithSpace(String str1, String str2, String str3, String str4,String str5) {
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
            if (!str4.equals("undefined")){
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
        float v = Float.parseFloat(str);
        DecimalFormat df = new DecimalFormat("0.00");
        return String.valueOf(df.format(v));
    }

    // 正则判断一个字符串是否全是数字
    public static boolean isNumeric(String str) {
        if (!checkStr(str))
            return false;
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    // 判断电话号码格式是否正确
    public static boolean isMobileNO(String mobiles) {

		/*
         * Pattern p = Pattern
		 * 
		 * .compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		 */

        if (!checkStr(mobiles))
            return false;
        Pattern p = Pattern.compile("[0-9]*");
        Matcher m = p.matcher(mobiles);

        return m.matches() & mobiles.trim().length() == 11;

    }

    // 判断密码格式是否正确
    public static boolean isPassword(String password) {
        if (password.length() >= 6) {
            return true;
        }
        if (password.length() <= 13) {
            return true;
        }
        return false;

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

    // <!-- 吐司提示 -->
    // <string name="acountnull">号码不能为空</string>
    // <string name="passwordnull">密码不能为空</string>
    // <string name="acountformat">号码格式有误,请输入正确的电话号码</string>
    // <string name="passwordformat">密码格式有误,请输入6到13位数字</string>

    // 判断密码账号格式正确性
    // public static boolean checkPassword(String password, Context context) {
    // if (!StringUtil.checkStr(password)) {
    // ToastUtil.showToast(context, R.string.passwordnull, null, false);
    // return false;
    // } else if (!StringUtil.isPassword(password)) {
    // ToastUtil.showToast(context, R.string.passwordformat, null, false);
    // return false;
    // }
    //
    // return true;
    //
    // }

    // public static boolean checkAcount(String acount, Context context) {
    // if (!StringUtil.checkStr(acount)) {
    // ToastUtil.showToast(context, R.string.acountnull, null, false);
    // return false;
    // } else if (!StringUtil.isMobileNO(acount)) {
    // ToastUtil.showToast(context, R.string.acountformat, null, false);
    // return false;
    // }
    // return true;
    //
    // }

    /**
     * 验证字符串是否为空
     *
     * @param param
     * @return
     */
    public static boolean empty(String param) {
        return param == null || param.trim().length() < 1;
    }

    public static String getDateToString(long time) {
        Date date = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        return simpleDateFormat.format(date);
    }

}
