package com.lewish.start.downloadmanagerdemo;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * author: sundong
 * created at 2017/3/30 17:03
 */
public class DLManager {
    //常量
    public static final String DOWNLOAD_URL = "http://ucdl.25pp.com/fs08/2017/01/20/2/2_87a290b5f041a8b512f0bc51595f839a.apk";
    public static final String FILE_NAME = "testApp.apk";
    public static final String NOTIFICATION_TITLE = "大象投教";
    public static final String NOTIFICATION_DESC = "一个坑爹的app";

    private Context mContext;
    private DownloadManager mDownloadManager;
    private DownloadManager.Request mDownloadManagerRequest;
    private DownloadBroadcastReceiver mDownloadBroadcastReceiver;
    private long mDownLoadID;

    private static class SingletonHolder{
        private static final DLManager INSTANCE = new DLManager();
    }

    public static DLManager getInstance(){
        return SingletonHolder.INSTANCE;
    }

    public void initDLManager(Context context){
        mContext = context.getApplicationContext();
        initDownloadManager();
    }
    private void initDownloadManager() {
        mDownloadManager = (DownloadManager) mContext.getSystemService(DOWNLOAD_SERVICE);
        mDownloadManagerRequest = new DownloadManager.Request(Uri.parse(DOWNLOAD_URL));

        //设置Notification标题和描述
        mDownloadManagerRequest.setTitle(NOTIFICATION_TITLE);
        mDownloadManagerRequest.setDescription(NOTIFICATION_DESC);
        mDownloadManagerRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        //指定网络下载类型
        //指定在WIFI状态下，执行下载操作。
        mDownloadManagerRequest.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        //指定在MOBILE状态下，执行下载操作
//        mDownloadManagerRequest.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);
        //是否允许漫游状态下，执行下载操作
        mDownloadManagerRequest.setAllowedOverRoaming(true);
        //是否允许“计量式的网络连接”执行下载操作
        mDownloadManagerRequest.setAllowedOverMetered(true); //默认是允许的。
        //设置下载文件类型
        mDownloadManagerRequest.setMimeType("application/vnd.android.package-archive");

        //创建目录
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdir();
        mDownloadManagerRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, FILE_NAME);

        final DownloadManager.Query query = new DownloadManager.Query();
    }

    public void download(){
        registBroadcastReceiver();
        mDownLoadID = mDownloadManager.enqueue(mDownloadManagerRequest);
    }

    public class DownloadBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                //下载完成
                unRegistBroadcastReceiver();
            }
        }
    }

    public void registBroadcastReceiver(){
        mDownloadBroadcastReceiver = new DownloadBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        mContext.registerReceiver(mDownloadBroadcastReceiver,intentFilter);
    }

    public void unRegistBroadcastReceiver(){
        mContext.unregisterReceiver(mDownloadBroadcastReceiver);
        mDownloadBroadcastReceiver = null;
    }

}
