package com.guo.duoduo.anyshareofandroid.ui.send.fragment;


import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.guo.duoduo.anyshareofandroid.MyApplication;
import com.guo.duoduo.anyshareofandroid.R;
import com.guo.duoduo.anyshareofandroid.constant.Constant;
import com.guo.duoduo.anyshareofandroid.ui.uientity.AppInfo;
import com.guo.duoduo.anyshareofandroid.ui.uientity.IInfo;
import com.guo.duoduo.anyshareofandroid.sdk.cache.Cache;
import com.guo.duoduo.anyshareofandroid.ui.send.view.AppSelectAdapter;
import com.guo.duoduo.anyshareofandroid.utils.DeviceUtils;


/**
 * Created by 郭攀峰 on 2015/9/16.
 */
public class AppFragment extends Fragment
    implements
        AdapterView.OnItemClickListener,
        OnSelectItemClickListener
{
    private static final String tag = AppFragment.class.getSimpleName();

    private View view = null;
    private List<IInfo> appList = new ArrayList<>();
    private PackageManager pkManager;
    private AppFragmentHandler handler;
    private AppSelectAdapter adapter;
    private GridView gridView;
    private ProgressBar progressBar;

    private OnSelectItemClickListener clickListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        if (view == null)
        {
            Log.d(tag, "AppFragment onCreateView function");

            view = inflater.inflate(R.layout.view_select, container, false);
            handler = new AppFragmentHandler(AppFragment.this);

            gridView = (GridView) view.findViewById(R.id.gridview);
            gridView.setOnItemClickListener(this);
            adapter = new AppSelectAdapter(getActivity(), appList);
            gridView.setAdapter(adapter);
            progressBar = (ProgressBar) view.findViewById(R.id.loading);

            getAppInfo();
        }

        return view;
    }

    private void getAppInfo()
    {
        new Thread()
        {
            public void run()
            {
                appList.clear();
                appList.addAll(getApp());
                Log.d(tag, "app list size =" + appList.size());
                Message msg = Message.obtain();
                msg.what = Constant.MSG.APP_OK;
                handler.sendMessage(msg);
            }
        }.start();
    }

    private List<IInfo> getApp()
    {
        pkManager = MyApplication.getInstance().getPackageManager();
        List<ApplicationInfo> listApp = pkManager
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(listApp, new ApplicationInfo.DisplayNameComparator(pkManager));
        List<IInfo> appInfo = new ArrayList<>();
        appInfo.clear();
        for (ApplicationInfo app : listApp)
        { // get the third APP
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0)
            {
                AppInfo info = getAppInfo(app);
                if (info == null)
                    continue;
                else if (!appInfo.contains(info))
                    appInfo.add(info);
            }
        }
        return appInfo;
    }

    private AppInfo getAppInfo(ApplicationInfo app)
    {
        AppInfo appInfo = new AppInfo();
        //此地方replace千万不能修改，不是空格，不知道什么东西！！！
        //String label = ((String)app.loadLabel(pkManager)).replace(" ","") + ".apk";
        String label = DeviceUtils.removeSpecial((String) app.loadLabel(pkManager))
            + ".apk";
        appInfo.appLabel = label;
        appInfo.appIcon = app.loadIcon(pkManager);
        appInfo.pkgName = app.packageName;

        String filepath;
        try
        {
            filepath = MyApplication.getInstance().getPackageManager()
                    .getApplicationInfo(app.packageName, 0).sourceDir;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
        if (filepath == null)
        {
            return null;
        }
        appInfo.appFilePath = filepath;
        File file = new File(filepath);
        long fileSize = file.length();
        if (fileSize <= 0)
            return null;
        String size = DeviceUtils.convertByte(fileSize);
        appInfo.appSize = size;

        return appInfo;
    }

    @Override
    public void onAttach(Activity activity)
    {
        try
        {
            clickListener = (OnSelectItemClickListener) activity;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        super.onAttach(activity);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        String filePath = ((AppInfo) adapter.getItem(position)).getFilePath();
        if (Cache.selectedList.contains(filePath))
        {
            Cache.selectedList.remove(filePath);
        }
        else
        {
            Cache.selectedList.add(filePath);
        }
        adapter.notifyDataSetChanged();
        clickListener.onItemClicked(Constant.MediaType.APP);
    }

    @Override
    public void onItemClicked(int type)
    {

    }

    private static class AppFragmentHandler extends Handler
    {
        private WeakReference<AppFragment> weakReference;

        public AppFragmentHandler(AppFragment fragment)
        {
            weakReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg)
        {
            AppFragment fragment = weakReference.get();
            if (fragment == null)
                return;
            if (fragment.getActivity() == null)
                return;

            if (fragment.getActivity().isFinishing())
                return;

            switch (msg.what)
            {
                case Constant.MSG.APP_OK :
                    fragment.adapter.notifyDataSetChanged();
                    fragment.progressBar.setVisibility(View.GONE);
                    break;
            }

        }
    }
}
