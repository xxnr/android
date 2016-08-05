package com.ksfc.newfarmer.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import com.ksfc.newfarmer.widget.dialog.CustomDialog;
import com.readystatesoftware.systembartint.SystemBarTintManager;

public class Utils {
    private static long lastClickTime;


    /**
     * 设置状态栏颜色
     */
    public static void setBarTint(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setTintColor(activity.getResources().getColor(color));
        }
    }


    /**
     * 判断是否是手机号
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNum(String mobiles) {
//        Pattern p = Pattern.compile("^[1]([0-8]{1}[0-9]{1}|59|58|88|89)[0-9]{8}");
        Pattern p = Pattern.compile("1\\d{10}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }


    /**
     * 获取版本号
     *
     * @return
     */
    public static String getVersionInfo(Context context) {

        PackageManager manager;
        PackageInfo info = null;
        manager = context.getPackageManager();
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
            String versionName = info.versionName;
            if (StringUtil.checkStr(versionName)) {
                return versionName;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断是否是身份证
     */

    public static boolean isIDCardNum(String IDCards) {

        Pattern p = Pattern
                .compile("((11|12|13|14|15|21|22|23|31|32|33|34|35|36|37|41|42|43|44|45|46|50|51|52|53|54|61|62|63|64|65)[0-9]{4})" +
                        "(([1|2][0-9]{3}[0|1][0-9][0-3][0-9][0-9]{3}" +
                        "[Xx0-9])|([0-9]{2}[0|1][0-9][0-3][0-9][0-9]{3}))");
        Matcher m = p.matcher(IDCards);
        return m.matches();
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 防止连点
     *
     * @return
     */
    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }


    /**
     * 从Assets中判断文件是否存在
     *
     * @return
     */
    public static boolean copyApkFromAssets(Context context, String fileName, String path) {
        boolean copyIsFinish = false;
        try {
            InputStream is = context.getAssets().open(fileName);
            File file = new File(path);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            is.close();
            copyIsFinish = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return copyIsFinish;
    }

    /**
     * 从Assets中安装apk
     *
     * @return
     */
    public static void addApk(final Context mContext, final String fileName) {

        if (copyApkFromAssets(mContext, fileName, Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName)) {
            CustomDialog.Builder m = new CustomDialog.Builder(mContext)
                    .setMessage("请安装易POS支付插件进行支付,安装后请返回新新农人")
                    .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setDataAndType(Uri.parse("file://" + Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName),
                                    "application/vnd.android.package-archive");
                            mContext.startActivity(intent);
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            m.create().show();
        } else {
            RndLog.v("PkgInstalled", "not found pkg");
        }

    }

    /**
     * traversal bundle with string values.
     *
     * @param bundle
     * @return
     */
    public static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder("");
        Set<String> keys = bundle.keySet();
        for (String key : keys) {
            sb.append(key).append(":").append(bundle.get(key)).append(";\n");
        }
        return sb.toString();
    }


    /**
     * 将字母装换成数字
     *
     * @param letter
     * @param list
     * @return
     */
    public static int getNum(String letter, List<String> list) {

        for (int i = 0; i < list.size(); i++) {

            if (list.get(i).equals(letter)) {

                return i;
            }
        }
        return 0;
    }

    /**
     * 拨号
     *
     * @return
     */
    public static void dial(final Activity activity, final String phoneNum) {

        CustomDialog.Builder builder = new CustomDialog.Builder(
                activity);
        builder.setMessage(phoneNum)
                .setPositiveButton("拨打",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:"
                                        + phoneNum));
                                dialog.dismiss();
                                // 开启系统拨号器
                                activity.startActivity(intent);
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        });
        builder.create().show();

    }

    /**
     * 调用系统的DownloadManager 下载apk
     */

    public static long loadApk(Context context, String url, String path) {
        try {
            File folder = new File(path);
            if (!(folder.exists() && folder.isDirectory())) {
                folder.mkdirs();
            }
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDestinationInExternalPublicDir(path, "xxnr.apk");//设置下载的路径和名称
            return downloadManager.enqueue(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 得到手机上安装的软件集合
     *
     * @param context
     * @param
     * @return
     */
    private static List<String> getPackageNames(Context context) {
        // 获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        // 用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<>();
        // 从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        return packageNames;
    }

    /**
     * app是否安装
     *
     * @return
     */
    public static boolean isPkgInstalled(Context context, String pkgName) {
        return getPackageNames(context).contains(pkgName);
    }

    /**
     * 判断qq是否可用
     *
     * @param context
     * @return
     */
    public static boolean isQQClientAvailable(Context context) {
        List<String> packageNames = getPackageNames(context);
        for (int i = 0; i < packageNames.size(); i++) {
            String pn = packageNames.get(i);
            if (pn.equals("com.tencent.mobileqq")) {
                return true;
            }
            if (pn.equals("com.tencent.mobileqq1")) {
                return true;
            }
            if (pn.equals("com.tencent.qqlite")) {
                return true;
            }
        }
        return false;
    }
}
