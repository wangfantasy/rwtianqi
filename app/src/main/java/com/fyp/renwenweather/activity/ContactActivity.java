package com.fyp.renwenweather.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.fyp.renwenweather.R;
import com.fyp.renwenweather.datamanager.Utils;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class ContactActivity extends AppCompatActivity {
    @ViewInject(R.id.myqq)
    TextView qq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        x.view().inject(this);
    }


    @Event(R.id.myqq)
    private void copyQQClick(View view) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText(null, "250463913"));
        Utils.showToast("作者QQ已复制到剪贴板");
    }
}
