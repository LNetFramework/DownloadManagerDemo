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
            public void onClick(View view) {
                DLManager.getInstance().initDLManager(DownloadActivity.this);
                DLManager.getInstance().download(new DLManager.DownLoadListener() {
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
                    public void onComplete() {
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
