package com.lewish.start.downloadmanagerdemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class DownloadActivity extends AppCompatActivity {

//    private DownloadBroadcastReceiver mDownloadBroadcastReceiver;
    private TextView mTvFileName;
    private TextView mTvProgress;
    private ProgressBar mPbUpdate;

    private Button mBtnStart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }


    private void initViews() {
        mTvFileName = (TextView) findViewById(R.id.mTvFileName);
        mTvProgress = (TextView) findViewById(R.id.mTvProgress);
        mPbUpdate = (ProgressBar) findViewById(R.id.mPbUpdate);
        mBtnStart = (Button) findViewById(R.id.mBtnStart);

        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DLManager.getInstance().download(DownloadActivity.this,
                        new DLManagerConfig.Builder()
                                .downLoadUrl("http://ucdl.25pp.com/fs08/2017/01/20/2/2_87a290b5f041a8b512f0bc51595f839a.apk")
                                .fileName("testApp.apk")
                                .notificationTitle("大象投教")
                                .notificationDesc("一个坑爹App")
                                .queryInterval(800)
                                .build()
                        , new DLManager.DownLoadListener() {
                            @Override
                            public void onStart() {
                                Toast.makeText(DownloadActivity.this, "开始下载", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onProgressUpdate(int progress) {
                                mPbUpdate.setProgress(progress);
                                mTvProgress.setText(String.valueOf(progress) + "%");
                            }

                            @Override
                            public void onComplete(File file) {
                                Toast.makeText(DownloadActivity.this, "完成", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void install(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//4.0以上系统弹出安装成功打开界面 startActivity(intent); }
    }
}
