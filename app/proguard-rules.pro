  #指定代码的压缩级别
    -optimizationpasses 5
    
    #包明不混合大小写
    -dontusemixedcaseclassnames
    
    #不去忽略非公共的库类
    -dontskipnonpubliclibraryclasses
    
     #优化  不优化输入的类文件
    -dontoptimize
    
     #预校验
    -dontpreverify
    
     #混淆时是否记录日志
    -verbose
    
     # 混淆时所采用的算法
    -optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
    
    #保护注解
    -keepattributes *Annotation*
    
    # 保持哪些类不被混淆
    -keep public class * extends android.app.Fragment
    -keep public class * extends android.app.Activity
    -keep public class * extends android.app.Application
    -keep public class * extends android.app.Service

    -keep public class * extends android.content.BroadcastReceiver
    -keep public class * extends android.content.ContentProvider
    -keep public class * extends android.app.backup.BackupAgentHelper
    -keep public class * extends android.preference.Preference
    -keep public class com.android.vending.licensing.ILicensingService
    -keep public class * extends android.support.v4.app.Fragment



    #忽略警告
    -ignorewarning
    ##记录生成的日志数据,gradle build时在本项目根目录输出##
    #apk 包内所有 class 的内部结构
    -dump class_files.txt
    #未混淆的类和成员
    -printseeds seeds.txt
    #列出从 apk 中删除的代码
    -printusage unused.txt
    #混淆前后的映射
    -printmapping mapping.txt
    #如果不想混淆 keep 掉
    -keep class com.lippi.recorder.iirfilterdesigner.** {*; }

    #项目特殊处理代码
    
    #忽略警告
    -dontwarn com.lippi.recorder.utils**
    #保留一个完整的包
    -keep class com.lippi.recorder.utils.** {
        *;
     }
    
    -keep class  com.lippi.recorder.utils.AudioRecorder{*;}
    
    
    #如果引用了v4或者v7包
    -dontwarn android.support.**
    
    ####混淆保护自己项目的部分代码以及引用的第三方jar包library-end####
    
    -keep public class * extends android.view.View {
        public <init>(android.content.Context);
        public <init>(android.content.Context, android.util.AttributeSet);
        public <init>(android.content.Context, android.util.AttributeSet, int);
        public void set*(...);
    }
    #保证实体类不混淆
     -dontwarn com.ksfc.newfarmer.beans.**
     -keep class com.ksfc.newfarmer.beans.** { *;}
     -dontwarn com.ksfc.newfarmer.beans.dbbeans.**
     -keep class com.ksfc.newfarmer.beans.dbbeans.** { *;}

    #保持 native 方法不被混淆
    -keepclasseswithmembernames class * {
        native <methods>;
    }
    
    #保持自定义控件类不被混淆
    -keepclasseswithmembers class * {
        public <init>(android.content.Context, android.util.AttributeSet);
    }
    
    #保持自定义控件类不被混淆
    -keepclassmembers class * extends android.app.Activity {
       public void *(android.view.View);
    }
    
    #保持 Parcelable 不被混淆
    -keep class * implements android.os.Parcelable {
      public static final android.os.Parcelable$Creator *;
    }
    
    #保持 Serializable 不被混淆
    -keepnames class * implements java.io.Serializable
    
    #保持 Serializable 不被混淆并且enum 类也不被混淆
    -keepclassmembers class * implements java.io.Serializable {
        static final long serialVersionUID;
        private static final java.io.ObjectStreamField[] serialPersistentFields;
        !static !transient <fields>;
        !private <fields>;
        !private <methods>;
        private void writeObject(java.io.ObjectOutputStream);
        private void readObject(java.io.ObjectInputStream);
        java.lang.Object writeReplace();
        java.lang.Object readResolve();
    }
    
    #保持枚举 enum 类不被混淆 如果混淆报错，建议直接使用上面的 -keepclassmembers class * implements java.io.Serializable即可
    -keepclassmembers enum * {
      public static **[] values();
      public static ** valueOf(java.lang.String);
    }
    
    -keepclassmembers class * {
        public void *ButtonClicked(android.view.View);
    }
    
    #不混淆资源类
    -keepclassmembers class **.R$* {
        public static final int *;
        public static <fields>;
    }
    #不混淆 泛型
     #gson
    -keepattributes Signature
    -keep class sun.misc.Unsafe { *; }
    -keep class com.google.gson.examples.android.model.** { *; }
    #okhttp
    -keepattributes Signature
    -keepattributes Annotation
    -keep class okhttp3.** {*; }
    -keep interface okhttp3.* {*; }
    -dontwarn okhttp3.*
    #okio
    -dontwarn okio.**
    -keep interface okio.** {*; }
    -keep class okio.** {*; }

    #Pulltoreflash
    -dontwarn com.handmark.pulltorefresh.library.**
    -keep class com.handmark.pulltorefresh.library.** { *;}
    -dontwarn com.handmark.pulltorefresh.library.extras.**
    -keep class com.handmark.pulltorefresh.library.extras.** { *;}
    -dontwarn com.handmark.pulltorefresh.library.internal.**
    -keep class com.handmark.pulltorefresh.library.internal.** { *;}
    #httpmine
    -keep class org.apache.http.entity.mime.** {*;}
    #支付宝
    -dontwarn com.alipay.**
    -keep class com.alipay.** {*;}
    #银联支付
    -keep class org.simalliance.openmobileapi.** {*;}
    -keep class org.simalliance.openmobileapi.service.** {*;}
    -keep class com.unionpay.** {*;}
     #Photo
    -dontwarn uk.co.senab.photoview.**
    -keep class uk.co.senab.photoview.** { *; }
    #js
    -keep class com.ksfc.newfarmer.jsinterface.** { *;
          public <methods>;
    }
    #swipwLayout
    -dontwarn com.daimajia.swipe.**
    -keep class com.daimajia.swipe.** { *; }
    -keep interface com.daimajia.swipe.** { *; }
    #systembartint
    -dontwarn com.readystatesoftware.systembartint.**
    -keep class com.readystatesoftware.systembartint.** { *; }
    #ptr
    -keep class in.srain.cube.** { *; }
    -keep interface in.srain.cube.** { *; }
    -dontwarn in.srain.cube.**
    #butterknife
    -keep class butterknife.*
    -dontwarn butterknife.internal.**
    -keep class **$$ViewBinder { *; }
    -keepclasseswithmembernames class * { @butterknife.* <methods>; }
    -keepclasseswithmembernames class * { @butterknife.* <fields>; }
    #Rx
    -dontwarn rx.**
    -keep class rx.** { *; }
    -keep interface rx.** { *; }
    #rxbinding
    -dontwarn com.jakewharton.rxbinding.**
    -keep class com.jakewharton.rxbinding.** { *; }
     #rxlifecycle
    -dontwarn com.trello.rxlifecycle.**
    -keep class com.trello.rxlifecycle.** { *; }
    -keep interface com.trello.rxlifecycle.** { *; }
    #retrofit
    -dontwarn retrofit2.**
    -keep class retrofit2.** { *; }
    -keepattributes Signature
    -keepattributes Exceptions
   -keepattributes *Annotation*
   #eventBus
   -keepclassmembers class ** {
       @org.greenrobot.eventbus.Subscribe <methods>;
   }
   -keep enum org.greenrobot.eventbus.ThreadMode { *; }
   -keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
       <init>(java.lang.Throwable);
   }
    #greendao
   -keep class org.greenrobot.greendao.** {*;}
   #保持greenDao的方法不被混淆  #用来保持生成的表名不被混淆
   -keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
   public static java.lang.String TABLENAME;
   }
   -keep class **$Properties
   #友盟统计
    -keepclassmembers class * {
        public <init> (org.json.JSONObject);
    }
  #友盟分享
    -dontshrink
    -dontoptimize
    -dontwarn com.google.android.maps.**
    -dontwarn android.webkit.WebView
    -dontwarn com.umeng.**
    -keep class com.umeng.**{ *; }
    -keep interface com.umeng.**{ *; }
    -dontwarn com.tencent.weibo.sdk.**
    -dontwarn com.facebook.**
    -keep enum com.facebook.**
    -keepattributes Exceptions,InnerClasses,Signature
    -keepattributes *Annotation*
    -keepattributes SourceFile,LineNumberTable
    -keep public interface com.facebook.**
    -keep public interface com.tencent.**
    -keep public interface com.umeng.socialize.**
    -keep public interface com.umeng.socialize.sensor.**
    -keep public interface com.umeng.scrshot.**
    -keep public class com.umeng.socialize.* {*;}
    -keep public class javax.**
    -keep public class android.webkit.**
    -keep class com.facebook.**
    -keep class com.umeng.scrshot.**
    -keep public class com.tencent.** {*;}
    -keep class com.umeng.socialize.sensor.**
    -keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
    -keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}
    -keep class im.yixin.sdk.api.YXMessage {*;}
    -keep class im.yixin.sdk.api.** implements im.yixin.sdk.api.YXMessage$YXMessageData{*;}
    #友盟推送

    -keep class com.umeng.message.* {
        public <fields>;
        public <methods>;
    }

    -keep class com.umeng.message.protobuffer.* {
        public <fields>;
        public <methods>;
    }

    -keep class com.squareup.wire.* {
        public <fields>;
        public <methods>;
    }

    -keep class com.umeng.message.local.* {
        public <fields>;
        public <methods>;
    }
    -keep class org.android.agoo.impl.*{
        public <fields>;
        public <methods>;
    }

    -dontwarn com.xiaomi.**

    -dontwarn com.ut.mini.**

    -keep class org.android.agoo.service.* {*;}

    -keep class org.android.spdy.**{*;}

    -keep public class com.ksfc.newfarmer.R$*{
        public static final int *;
    }










