package com.lewish.start.downloadmanagerdemo;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * author: sundong
 * created at 2017/3/30 17:03
 */
public class DLManager {

    private String downloadUrl;
    private String fileName;
    private String notificationTitle;
    private String notificationDesc;
    private long queryInterval;

    private Context mContext;
    private DownloadManager mDownloadManager;
    private DownloadManager.Request mDownloadManagerRequest;
    private DownloadBroadcastReceiver mDownloadBroadcastReceiver;

    private long mDownLoadID;

    private Timer mTimer;
    private QueryTask mQueryTask;
    private Handler mDeliveryHandler;
    private DownLoadListener mDownLoadListener;

    private static class SingletonHolder {
        private static final DLManager INSTANCE = new DLManager();
    }

    public static DLManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private void initDownloadManager(DLManagerConfig dlManagerConfig) {
        downloadUrl = dlManagerConfig.getDownloadUrl();
        fileName = dlManagerConfig.getFileName();
        notificationTitle = dlManagerConfig.getNotificationTitle();
        notificationDesc = dlManagerConfig.getNotificationDesc();
        queryInterval = dlManagerConfig.getQueryInterval();

        mDownloadManager = (DownloadManager) mContext.getSystemService(DOWNLOAD_SERVICE);
        mDownloadManagerRequest = new DownloadManager.Request(Uri.parse(downloadUrl));

        //添加请求头
        HashMap<String, String> downLoadRequestHeaders = dlManagerConfig.getDownLoadRequestHeaders();
        if(downLoadRequestHeaders!=null&&downLoadRequestHeaders.isEmpty()) {
            for (Map.Entry<String,String> downLoadRequestHeader : downLoadRequestHeaders.entrySet()) {
                mDownloadManagerRequest.addRequestHeader(downLoadRequestHeader.getKey(),downLoadRequestHeader.getValue());
            }
        }
        
        //设置Notification标题和描述
        mDownloadManagerRequest.setTitle(notificationTitle);
        mDownloadManagerRequest.setDescription(notificationDesc);
        mDownloadManagerRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        //指定网络下载类型
        //指定在WIFI状态下，执行下载操作。
        mDownloadManagerRequest.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        //指定在MOBILE状态下，执行下载操作
        //mDownloadManagerRequest.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);
        //是否允许漫游状态下，执行下载操作
        mDownloadManagerRequest.setAllowedOverRoaming(true);
        //是否允许“计量式的网络连接”执行下载操作
        mDownloadManagerRequest.setAllowedOverMetered(true); //默认是允许的。
        //设置下载文件类型
        mDownloadManagerRequest.setMimeType("application/vnd.android.package-archive");

        //创建目录
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdir();
        mDownloadManagerRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
    }

    /**
     * 查询任务
     */
    private static class QueryTask extends TimerTask {
        private DownloadManager downloadManager;
        private long downloadID;
        private Handler deliveryHandler;
        private DownLoadListener downLoadListener;
        private DownloadManager.Query query;

        private QueryTask(@NonNull DownloadManager downloadManager, long downloadID, @NonNull Handler deliveryHandler, @NonNull DownLoadListener downLoadListener) {
            this.downloadManager = downloadManager;
            this.downloadID = downloadID;
            this.deliveryHandler = deliveryHandler;
            this.downLoadListener = downLoadListener;
            query = new DownloadManager.Query();
        }

        @Override
        public void run() {
            Cursor cursor = downloadManager.query(query.setFilterById(downloadID));
            if (cursor != null && cursor.moveToFirst()) {
//                String title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
//                String address = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                int bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                final int pct = (bytes_downloaded * 100) / bytes_total;

                deliveryHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (downLoadListener != null) {
                            downLoadListener.onProgressUpdate(pct);
                        }
                    }
                });

            }
            cursor.close();
        }
    }

    public void download(Context context,DLManagerConfig dlManagerConfig,DownLoadListener downLoadListener){
        mContext = context.getApplicationContext();
        mDeliveryHandler = new Handler(Looper.getMainLooper()) {};
        initDownloadManager(dlManagerConfig);
        this.mDownLoadListener = downLoadListener;

        registBroadcastReceiver();
        mTimer = new Timer();
        mDownLoadID = mDownloadManager.enqueue(mDownloadManagerRequest);
        mQueryTask = new QueryTask(mDownloadManager, mDownLoadID, mDeliveryHandler, mDownLoadListener);
        mTimer.schedule(mQueryTask, 0, queryInterval);
    }

    public class DownloadBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                //下载完成
                if (mDownLoadListener != null) {
                    mDownLoadListener.onProgressUpdate(100);
                    mDownLoadListener.onComplete(new File(getDownloadPath(mDownLoadID)));
                }
                unRegistBroadcastReceiver();
                mTimer.cancel();
                mQueryTask.cancel();
            }
        }
    }

    public void registBroadcastReceiver() {
        mDownloadBroadcastReceiver = new DownloadBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        mContext.registerReceiver(mDownloadBroadcastReceiver, intentFilter);
    }

    public void unRegistBroadcastReceiver() {
        mContext.unregisterReceiver(mDownloadBroadcastReceiver);
        mDownloadBroadcastReceiver = null;
    }

    public interface DownLoadListener {
        public void onStart();

        public void onProgressUpdate(int progress);

        public void onComplete(File file);
    }

    /**
     * 获取文件保存的路径
     */
    public String getDownloadPath(long downloadId) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor c = mDownloadManager.query(query);
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    return c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI));
                }
            } finally {
                c.close();
            }
        }
        return null;
    }


    /**
     * 获取下载状态
     *
     * @param downloadId an ID for the download, unique across the system.
     *                   This ID is used to make future calls related to this download.
     * @return int
     * @see DownloadManager#STATUS_PENDING
     * @see DownloadManager#STATUS_PAUSED
     * @see DownloadManager#STATUS_RUNNING
     * @see DownloadManager#STATUS_SUCCESSFUL
     * @see DownloadManager#STATUS_FAILED
     */
    public int getDownloadStatus(long downloadId) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor c = mDownloadManager.query(query);
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    return c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));

                }
            } finally {
                c.close();
            }
        }
        return -1;
    }


    /**
     * 下载的apk和当前程序版本比较
     *
     * @param apkInfo apk file's packageInfo
     * @param context Context
     * @return 如果当前应用版本小于apk的版本则返回true
     */
    private static boolean compare(PackageInfo apkInfo, Context context) {
        if (apkInfo == null) {
            return false;
        }
        String localPackage = context.getPackageName();
        if (apkInfo.packageName.equals(localPackage)) {
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(localPackage, 0);
                if (apkInfo.versionCode > packageInfo.versionCode) {
                    return true;
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
