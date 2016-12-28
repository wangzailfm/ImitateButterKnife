package com.wangzai.imitatebutterknife;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import com.wangzai.BindViewId;
import com.wangzai.OnClick;
import com.wangzai.api.GenerateCode;

public class MainActivity extends AppCompatActivity {

    @BindViewId(R.id.toolbar)
    Toolbar mToolbar;
    @BindViewId(R.id.content_main)
    RelativeLayout mContentMain;
    @BindViewId(R.id.fab)
    FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GenerateCode.bind(this);
        setSupportActionBar(mToolbar);
    }

    @OnClick({R.id.toolbar, R.id.content_main, R.id.fab})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar:
                break;
            case R.id.content_main:
                break;
            case R.id.fab:
                Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
        }
    }
}
