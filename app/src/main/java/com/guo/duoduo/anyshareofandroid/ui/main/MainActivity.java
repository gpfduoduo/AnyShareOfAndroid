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
import com.guo.duoduo.anyshareofandroid.ui.receive.ReceiveActivity;
import com.guo.duoduo.anyshareofandroid.ui.send.FileActivity;
import com.guo.duoduo.anyshareofandroid.ui.setting.SettingActivity;


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
        nameEdit.setText(Build.DEVICE);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.activity_main_i_receive :
                startActivity(new Intent(MainActivity.this, ReceiveActivity.class));
                break;
            case R.id.activity_main_i_send :
                startActivity(new Intent(MainActivity.this, FileActivity.class).putExtra(
                    "name", nameEdit.getText().toString()));
                break;
        }
    }

}
