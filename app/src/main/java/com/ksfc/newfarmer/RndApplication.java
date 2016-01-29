package com.ksfc.newfarmer;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.ksfc.newfarmer.utils.CrashHandler;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.umeng.message.PushAgent;
import com.umeng.update.UmengUpdateAgent;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.SoundPool;
import android.os.Environment;
import android.widget.Toast;

import net.yangentao.util.app.App;

public class RndApplication extends Application {

    public static Context applicationContext;
    private static RndApplication instance;
    // login user name
    private String uid = "";
    private String pwd = "";
    public static boolean isLogined = false;
    private static final String dbname = "xnrdb.db";
    public static final String DB_NAME = dbname;
    public final String PREF_USERNAME = "username";

    public static List<Activity> unDestroyActivityList = new ArrayList<Activity>();
    public static List<Activity> tempDestroyActivityList = new ArrayList<Activity>();
    private static final String TAG = "RndApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        instance = this;
        App.setApp(this);
        initImageLoader(this, null);
        CrashHandler.getInstance().init(this);
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.enable();

    }


    /**
     * 初始化图片加载器
     *
     * @param mContext
     * @param defaultOptions
     */
    public static void initImageLoader(Context mContext,
                                       DisplayImageOptions defaultOptions) {

        if (defaultOptions == null)
            defaultOptions = buildImageOptions(mContext);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                mContext)
                .threadPoolSize(5)
                .defaultDisplayImageOptions(defaultOptions)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .memoryCache(new WeakMemoryCache())
                .memoryCacheSize(20 * 1024 * 1024)
                .discCache(
                        new UnlimitedDiscCache(new File(Environment
                                .getExternalStorageDirectory()
                                + "/haoapp/.imagecache")))
                ./* writeDebugLogs(). */build();
        ImageLoader.getInstance().init(config);
    }

    /**
     * 创建图片参数
     *
     * @param mContext
     * @return
     */
    private static DisplayImageOptions buildImageOptions(Context mContext) {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(null)
                .showImageForEmptyUri(R.drawable.error).bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnFail(R.drawable.error).considerExifParams(true)
                .showImageOnLoading(R.drawable.zhanweitu)
                .cacheInMemory(true).cacheOnDisc(true).build();
        return defaultOptions;
    }

    public static RndApplication getInstance() {
        return instance;
    }


    private Toast toast;

    /**
     * 短时间显示Toast 作用:不重复弹出Toast,如果当前有toast正在显示，则先取消
     *
     * @param info 显示的内容
     */
    public void showToast(String info) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, info, Toast.LENGTH_SHORT);
        toast.setText(info);
        toast.show();
    }

    /**
     * 退出应用
     */
    public void quit() {
        for (Activity activity : unDestroyActivityList) {
            if (null != activity) {
                activity.finish();
            }
        }
        tempDestroyActivityList.clear();
        for (Activity activity : tempDestroyActivityList) {
            if (null != activity) {
                activity.finish();
            }
        }
        unDestroyActivityList.clear();

    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {

        return uid;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getPwd() {
        return this.pwd;
    }

    /**
     * 局部退出
     */

    private List<Activity> activityList = new LinkedList<Activity>();

    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public void exit() {
        if (activityList != null) {
            Activity activity;
            for (int i = 0; i < activityList.size(); i++) {
                activity = activityList.get(i);
                if (activity != null) {
                    if (!activity.isFinishing()) {
                        activity.finish();
                    }
                    activity = null;
                }
                activityList.remove(i);
                i--;
            }
        }
    }
}
