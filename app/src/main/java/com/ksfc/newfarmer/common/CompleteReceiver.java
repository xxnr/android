package com.ksfc.newfarmer.common;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by CAI on 2016/6/21.
 */
//下载完成的BroadcastReceiver
public class CompleteReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        // get complete download id
        long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        // 打开下载好的文件
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uriForDownloadedFile = downloadManager.getUriForDownloadedFile(completeDownloadId);
        Intent intentAddApk = new Intent(Intent.ACTION_VIEW);
        intentAddApk.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentAddApk.setDataAndType(uriForDownloadedFile, "application/vnd.android.package-archive");
        context.startActivity(intentAddApk);

    }
}