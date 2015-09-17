package com.guo.duoduo.anyshareofandroid.ui.send;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.guo.duoduo.anyshareofandroid.R;
import com.guo.duoduo.randomtextview.RandomTextView;


/**
 * Created by 郭攀峰 on 2015/9/12.
 */
public class RadarScanActivity extends AppCompatActivity
{
    private static final String tag = RadarScanActivity.class.getSimpleName();

    private RandomTextView randomTextView;

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

        randomTextView = (RandomTextView) findViewById(R.id.activity_radar_rand_textview);
        randomTextView
                .setOnTextViewClickListener(new RandomTextView.OnTextViewClickListener() {
                    @Override
                    public void onTextViewClicked(View view) {
                        Log.d(tag, "clicked TextView name = "
                                + ((TextView) view).getText().toString());
                    }
                });
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}
