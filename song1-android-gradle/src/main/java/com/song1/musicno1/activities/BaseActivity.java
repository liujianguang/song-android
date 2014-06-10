package com.song1.musicno1.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * User: windless
 * Date: 13-12-11
 * Time: PM7:21
 */
public abstract class BaseActivity extends ActionBarActivity {
    public static final String FINISH_INTENT = "com.song1.musicno1.baseactivity.finish";

    private BroadcastReceiver finishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(finishReceiver, new IntentFilter(FINISH_INTENT));
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(finishReceiver);
    }
}
