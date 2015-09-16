package com.guo.duoduo.anyshareofandroid.ui.send;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.guo.duoduo.anyshareofandroid.R;


/**
 * Created by 郭攀峰 on 2015/9/12.
 */
public class RadarScanActivity extends AppCompatActivity
{
    private static final String tag = RadarScanActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radarscan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_radar_toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        if (intent != null)
        {
            TextView radar_scan_name = (TextView) findViewById(R.id.activity_radar_scan_name);
            radar_scan_name.setText(intent.getStringExtra("name"));
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}
