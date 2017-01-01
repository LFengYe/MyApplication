package com.cn.carigps.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.cn.carigps.R;

/**
 * Created by fuyzh on 16/8/15.
 */
public class NoticeDetail extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);
        init();
    }
    private void init() {
        String title = getIntent().getStringExtra("noticeTitle");
        String content = getIntent().getStringExtra("noticeContent");
        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView titleView = (TextView) findViewById(R.id.titleText);
        titleView.setText(title);

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDefaultTextEncodingName("UTF-8");
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webView.loadUrl(content);
    }
}
