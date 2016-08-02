package com.ksfc.newfarmer.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;

import net.yangentao.util.XLog;

import com.ksfc.newfarmer.App;

public class IoUtils {

    private static String imageCacheDir = "images";

    /**
     * 得到图片的缓存目录
     *
     * @return
     */
    @SuppressLint("NewApi")
    public static File getImageCacheDir() {
        File imageDir = null;
        String appName = "foodie";
        if (Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            File dir = Environment.getExternalStorageDirectory();
            String path = dir.getAbsolutePath() + File.separator + appName
                    + File.separator + imageCacheDir;
            imageDir = new File(path);
            if (!imageDir.exists()) {
                imageDir.mkdirs();
            }
        } else {
            imageDir = App.getApp().getCacheDir();
        }
        return imageDir;
    }

    /**
     * 将bitmap保存到指定路径
     *
     * @param map
     * @param path
     * @return
     */
    public static void saveBitmap(Bitmap map, String path) {

        FileOutputStream out = null;
        try {

            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
            CompressFormat format = getFormat(path);
            if (null != format) {
                out = new FileOutputStream(file);
                if (map.compress(format, 100, out)) {
                    out.flush();
                    out.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (map != null && !map.isRecycled()) {
            map.recycle();
            map = null;
        }
    }

    private static CompressFormat getFormat(String path) {

        String type = getType(path);
        CompressFormat format = null;
        if (!TextUtils.isEmpty(type)) {
            if (type.equalsIgnoreCase("png")) {
                format = CompressFormat.PNG;
            } else if (type.equalsIgnoreCase("jpg")
                    || type.equalsIgnoreCase("jpe")
                    || type.equalsIgnoreCase("jpeg")) {
                format = CompressFormat.JPEG;
            }
        }

        return format;
    }

    private static String getType(String path) {

        String type;
        type = "";
        try {
            int pos = path.lastIndexOf(".");
            if (pos != -1) {
                type = path.substring(pos + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return type;
    }

    // decodefile 并进行内存溢出的判断
    public static Bitmap decodeFile(File f) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f));

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 100;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = 1;
            return BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (Exception e) {
            e.printStackTrace();
            XLog.e("获取文件出错.");
        }
        return null;
    }

    /**
     * 将图片压缩到100k以下  质量压缩
     *
     * @param image
     */
    public static void compressImageAndSave(Bitmap image, String path) {

        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 80;
        while (baos.toByteArray().length / 1024 > 200) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            options -= 10;// 每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            baos.writeTo(out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void comp(Bitmap image,String path) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if( baos.toByteArray().length / 1024>1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 480f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;//降低图片从ARGB888到RGB565
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        compressImageAndSave(bitmap,path);//压缩好比例大小后再进行质量压缩
    }

}
