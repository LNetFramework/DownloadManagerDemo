package com.lewish.start.downloadmanagerdemo;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class DownloadActivity extends AppCompatActivity {
    public static final String DOWNLOAD_URL = "http://ucdl.25pp.com/fs08/2017/01/20/2/2_87a290b5f041a8b512f0bc51595f839a.apk";
    public static final String FILE_NAME = "testApp.apk";
    public static final String NOTIFICATION_TITLE = "大象投教";
    public static final String NOTIFICATION_DESC = "一个坑爹的app";

    private DownloadBroadcastReceiver mDownloadBroadcastReceiver;
    private TextView mTvFileName;
    private TextView mTvProgress;
    private ProgressBar mPbUpdate;

    private Button mBtnStart;
    private DownloadManager mDownloadManager;
    private DownloadManager.Request mDownloadManagerRequest;

    private Timer mTimer;
    private TimerTask mTimerTask;
    private long mDownLoadID;
    private boolean isDownLoadStart;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            int pct = bundle.getInt("pct");
            String name = bundle.getString("name");
            mPbUpdate.setProgress(pct);
            mTvProgress.setText(String.valueOf(pct) + "%");
            mTvFileName.setText(name);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        registBroadcastReceiver();
        initDownloadManager();
    }

    private void initDownloadManager() {
        mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
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
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            public void run() {
                Cursor cursor = mDownloadManager.query(query.setFilterById(mDownLoadID));
                if (cursor != null && cursor.moveToFirst()) {
                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        mPbUpdate.setProgress(100);
                        install(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/"+FILE_NAME);
                        mTimerTask.cancel();
                    }
                    String title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
                    String address = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    int bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    int pct = (bytes_downloaded * 100) / bytes_total;
                    Message msg = Message.obtain();
                    Bundle bundle = new Bundle();
                    bundle.putInt("pct", pct);
                    bundle.putString("name", title);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
                cursor.close();
            }
        };
    }

    private void initViews() {
        mTvFileName = (TextView) findViewById(R.id.mTvFileName);
        mTvProgress = (TextView) findViewById(R.id.mTvProgress);
        mPbUpdate = (ProgressBar) findViewById(R.id.mPbUpdate);
        mBtnStart = (Button) findViewById(R.id.mBtnStart);

        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mDownLoadID = mDownloadManager.enqueue(mDownloadManagerRequest);
//                mTimer.schedule(mTimerTask, 0, 1000);
//                mBtnStart.setClickable(false);
                DLManager.getInstance().initDLManager(DownloadActivity.this);
                DLManager.getInstance().download();
            }
        });
    }

    private void install(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//4.0以上系统弹出安装成功打开界面 startActivity(intent); }
    }

    public void registBroadcastReceiver(){
        mDownloadBroadcastReceiver = new DownloadBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(mDownloadBroadcastReceiver,intentFilter);
    }

    public void unRegistBroadcastReceiver(){
        unregisterReceiver(mDownloadBroadcastReceiver);
        mDownloadBroadcastReceiver = null;
    }

    @Override
    protected void onDestroy() {
        unRegistBroadcastReceiver();
        super.onDestroy();
    }

    public class DownloadBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(DownloadActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
        }
    }
}
