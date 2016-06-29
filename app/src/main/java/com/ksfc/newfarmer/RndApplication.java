package com.ksfc.newfarmer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ksfc.newfarmer.db.DBManager;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.http.beans.LoginResult;
import com.ksfc.newfarmer.utils.CrashHandler;
import com.ksfc.newfarmer.utils.RndLog;
import com.ksfc.newfarmer.utils.thrid.UmengPush;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.umeng.message.PushAgent;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.utils.Log;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.Toast;


public class RndApplication extends Application {
    private static final String TAG = "RndApplication";
    public static Context applicationContext;
    private static RndApplication instance;

    private static final String dbname = "xnrdb.db";
    public static final String DB_NAME = dbname;

    public static List<Activity> unDestroyActivityList = new ArrayList<>();
    public static List<Activity> tempDestroyActivityList = new ArrayList<>();

    // login user name
    private String uid = "";
    private String pwd = "";


    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        instance = this;
        App.setApp(this);
        //是否显示log
        RndLog.DEBUG_MODE = true;
        //初始化图片加载器
        initImageLoader(this, null);
        //初始化数据库管理器
        DBManager.getInstance().Init(this);
        //初始化CrashHandler
        CrashHandler.getInstance().init(this);
        //初始化推送
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setDebugMode(false);
        mPushAgent.enable();
        //初始化推送接入后台
        UmengPush.lunchActivity(this);
        //初始化社会化分享
        initSocialShare();
        //初初始化全局的UID
        LoginResult.UserInfo userInfo = Store.User.queryMe();
        if (userInfo != null) {
            setUid(userInfo.userid);
        }
    }


    //社会化分享初始化
    public void initSocialShare() {
        //微信 appid appsecret
        PlatformConfig.setWeixin("wx46173e821f28d05a", "919a7e2cb7e1483393797f15bf53dcb9");
        // QQ和Qzone appid appkey
        PlatformConfig.setQQZone("1104752635", "giwjla40jiDQNuzI");
        Log.LOG = false;
        Config.IsToastTip = false;
    }


    /**
     * 初始化图片加载器
     */
    public static void initImageLoader(Context mContext,
                                       DisplayImageOptions defaultOptions) {

        if (defaultOptions == null) {
            defaultOptions = buildImageOptions();
        }
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
     */
    private static DisplayImageOptions buildImageOptions() {
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

    public Toast toast;

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


    /**
     * 局部退出
     */
    public void partQuit() {
        for (Activity activity : RndApplication.tempDestroyActivityList) {
            if (null != activity) {
                activity.finish();
            }
        }
        RndApplication.tempDestroyActivityList.clear();
    }


    //设置全局的用户名和密码
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


}
