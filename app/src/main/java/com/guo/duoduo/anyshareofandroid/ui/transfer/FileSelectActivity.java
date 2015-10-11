package com.guo.duoduo.anyshareofandroid.ui.transfer;


import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.guo.duoduo.anyshareofandroid.R;
import com.guo.duoduo.anyshareofandroid.sdk.cache.Cache;
import com.guo.duoduo.anyshareofandroid.ui.common.BaseActivity;
import com.guo.duoduo.anyshareofandroid.ui.transfer.fragment.AppFragment;
import com.guo.duoduo.anyshareofandroid.ui.common.FragmentAdapter;
import com.guo.duoduo.anyshareofandroid.ui.transfer.fragment.OnSelectItemClickListener;
import com.guo.duoduo.anyshareofandroid.ui.transfer.fragment.PictureFragment;
import com.guo.duoduo.anyshareofandroid.utils.ToastUtils;


/**
 * Created by 郭攀峰 on 2015/9/15.
 */
public class FileSelectActivity extends BaseActivity implements OnSelectItemClickListener
{
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private String userName = Build.DEVICE;
    private Toolbar toolbar;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_file);
        toolbar = (Toolbar) findViewById(R.id.activity_file_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        title = toolbar.getTitle().toString();
        if (TextUtils.isEmpty(title))
            title = getString(R.string.file_select);

        Intent intent = getIntent();
        if (intent != null)
        {
            userName = intent.getStringExtra("name");
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.activity_file_fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (Cache.selectedList.size() > 0)
                    startActivity(new Intent(FileSelectActivity.this,
                        RadarScanActivity.class).putExtra("name", userName));
                else
                    ToastUtils.showTextToast(getApplicationContext(),
                        getString(R.string.please_select_file));
            }
        });

        List<String> titles = new ArrayList<>();
        titles.add(getString(R.string.app));
        titles.add(getString(R.string.picture));

        tabLayout = (TabLayout) findViewById(R.id.activity_file_tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText(titles.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(titles.get(1)));

        viewPager = (ViewPager) findViewById(R.id.activity_file_viewpager);
        List<android.support.v4.app.Fragment> fragments = new ArrayList<>();
        fragments.add(new AppFragment());
        fragments.add(new PictureFragment());

        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(),
            fragments, titles);
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(adapter);
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Cache.selectedList.clear();
    }

    @Override
    public void onItemClicked(int type)
    {
        updateTitle();
    }

    private void updateTitle()
    {
        toolbar.setTitle(title + " / " + Cache.selectedList.size());
    }
}
