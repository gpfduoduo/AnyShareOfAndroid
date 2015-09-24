package com.guo.duoduo.anyshareofandroid.ui.main;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.guo.duoduo.anyshareofandroid.R;
import com.guo.duoduo.anyshareofandroid.sdk.cache.Cache;
import com.guo.duoduo.anyshareofandroid.ui.setting.SettingActivity;
import com.guo.duoduo.anyshareofandroid.ui.transfer.FileSelectActivity;
import com.guo.duoduo.anyshareofandroid.ui.transfer.ReceiveActivity;
import com.guo.duoduo.anyshareofandroid.utils.PreferenceUtils;


public class MainActivity extends AppCompatActivity implements View.OnClickListener
{

    private EditText nameEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.activity_main_fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view,
                    getResources().getString(R.string.setting_illustrate),
                    Snackbar.LENGTH_LONG)
                        .setAction(getResources().getString(R.string.setting_action),
                            new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View view)
                                {
                                    startActivity(new Intent(MainActivity.this,
                                        SettingActivity.class));
                                }
                            }).show();
            }
        });

        Button send = (Button) findViewById(R.id.activity_main_i_send);
        send.setOnClickListener(this);
        Button receive = (Button) findViewById(R.id.activity_main_i_receive);
        receive.setOnClickListener(this);

        nameEdit = (EditText) findViewById(R.id.activity_main_name_edit);
        nameEdit.setText((String) PreferenceUtils.getParam(MainActivity.this, "String",
            Build.DEVICE));
    }

    @Override
    public void onPause()
    {
        super.onPause();
        //记住用户修改的名字
        PreferenceUtils.setParam(MainActivity.this, "String", nameEdit.getText()
                .toString());
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.activity_main_i_receive :
                Cache.selectedList.clear();
                startActivity(new Intent(MainActivity.this, ReceiveActivity.class)
                        .putExtra("name", nameEdit.getText().toString()));
                break;
            case R.id.activity_main_i_send :
                Cache.selectedList.clear();
                startActivity(new Intent(MainActivity.this, FileSelectActivity.class)
                        .putExtra("name", nameEdit.getText().toString()));
                break;
        }
    }

}
