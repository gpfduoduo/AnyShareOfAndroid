package com.guo.duoduo.anyshareofandroid.ui.send;


import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.guo.duoduo.anyshareofandroid.R;
import com.guo.duoduo.anyshareofandroid.ui.uientity.IInfo;
import com.guo.duoduo.anyshareofandroid.sdk.cache.Cache;
import com.guo.duoduo.anyshareofandroid.ui.send.fragment.AppFragment;
import com.guo.duoduo.anyshareofandroid.ui.send.fragment.FragmentAdapter;
import com.guo.duoduo.anyshareofandroid.ui.send.fragment.OnSelectItemClickListener;
import com.guo.duoduo.anyshareofandroid.ui.send.fragment.PictureFragment;


/**
 * Created by 郭攀峰 on 2015/9/15.
 */
public class FileActivity extends ActionBarActivity implements OnSelectItemClickListener
{
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private List<IInfo> selectedList = new ArrayList<>();
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
                startActivity(new Intent(FileActivity.this, RadarScanActivity.class)
                        .putExtra("name", userName));
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
    protected void onDestroy()
    {
        super.onDestroy();
        Cache.selectedList.clear();
    }

    @Override
    public void onItemClicked(int type)
    {
        toolbar.setTitle(title + " / " + Cache.selectedList.size());
    }
}
