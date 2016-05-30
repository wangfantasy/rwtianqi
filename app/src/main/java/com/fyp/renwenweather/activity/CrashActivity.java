package com.fyp.renwenweather.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fyp.renwenweather.R;
import com.fyp.renwenweather.datamanager.Utils;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UploadFileListener;

public class CrashActivity extends AppCompatActivity {
    @ViewInject(R.id.textSending)
    TextView textSending;
    @ViewInject(R.id.progressBar)
    private ProgressBar progressBar;
    private String filePathName;
    private boolean uploading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);
        Bmob.initialize(this, "7b05e8fa4802dae149486f79e5cbddfe");
        x.view().inject(this);
        textSending.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        Intent intent = getIntent();
        filePathName = intent.getStringExtra("filePathName");
    }

    @Event(R.id.btnSendCrashReport)
    private void sendCrashReportClick(View view) {
        if (uploading) {
            return;
        }
        uploading = true;
        if (TextUtils.isEmpty(filePathName)) {
            Utils.showToast("未找到日志文件，可能是存储空间已满或没有相应权限");
        } else {
            progressBar.setVisibility(View.VISIBLE);
            textSending.setVisibility(View.VISIBLE);
            final File file = new File(filePathName);
            if (file.exists()) {
                BmobFile bmobFile = new BmobFile(file);
                bmobFile.upload(this, new UploadFileListener() {
                    @Override
                    public void onSuccess() {
                        Utils.showToast("上传成功，感谢您的反馈");
                        uploading = false;
                        exitActivityClick(null);
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Utils.showToast("上传失败，您可以再点一次重试");
                        progressBar.setVisibility(View.INVISIBLE);
                        textSending.setVisibility(View.INVISIBLE);
                        uploading = false;
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        uploading = false;
                    }
                });
            } else {
                Utils.showToast("未找到日志文件，可能是存储空间已满或没有相应权限");
            }
        }
    }

    @Event(R.id.btnExitActivity)
    private void exitActivityClick(View view) {
        finish();
    }
}
