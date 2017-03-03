package com.lewish.start.downloadmanagerdemo;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DownloadActivity extends AppCompatActivity {
    private TextView mTvFileName;
    private TextView mTvProgress;
    private ProgressBar mPbUpdate;

    private DownloadManager mDownloadManager;
    private DownloadManager.Request request;
    public static final String DOWNLOAD_URL = "http://ucdl.25pp.com/fs08/2017/01/20/2/2_87a290b5f041a8b512f0bc51595f839a.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        request = new DownloadManager.Request(Uri.parse(DOWNLOAD_URL));

        //设置Notification标题和描述
        request.setTitle("标题");
        request.setDescription("描述");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        //指定网络下载类型
        //指定在WIFI状态下，执行下载操作。
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        //指定在MOBILE状态下，执行下载操作
//        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);
        //是否允许漫游状态下，执行下载操作
        request.setAllowedOverRoaming(true);
        //是否允许“计量式的网络连接”执行下载操作
        request.setAllowedOverMetered(true); //默认是允许的。
    }

    private void initViews() {
        mTvFileName = (TextView)findViewById(R.id.mTvFileName);
        mTvProgress = (TextView)findViewById(R.id.mTvProgress);
        mPbUpdate = (ProgressBar)findViewById(R.id.mPbUpdate);

    }
}
