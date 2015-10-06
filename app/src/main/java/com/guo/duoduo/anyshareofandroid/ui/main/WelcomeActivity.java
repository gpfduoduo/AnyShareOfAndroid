package com.guo.duoduo.anyshareofandroid.ui.main;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.guo.duoduo.anyshareofandroid.R;
import com.guo.duoduo.p2pmanager.p2ptimer.OSTimer;
import com.guo.duoduo.p2pmanager.p2ptimer.Timeout;


public class WelcomeActivity extends Activity
{

    private ImageView melon1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        initWidget();
        Timeout timeout = new Timeout()
        {
            @Override
            public void onTimeOut()
            {
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                finish();
            }
        };

        new OSTimer(null, timeout, 2 * 1000).start();
    }

    private void initWidget()
    {
        melon1 = (ImageView) findViewById(R.id.activity_welcome_melon1);
    }
}
