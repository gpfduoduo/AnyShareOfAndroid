package com.guo.duoduo.anyshareofandroid.ui.transfer.fragment;


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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.guo.duoduo.anyshareofandroid.MyApplication;
import com.guo.duoduo.anyshareofandroid.R;
import com.guo.duoduo.anyshareofandroid.constant.Constant;
import com.guo.duoduo.anyshareofandroid.sdk.cache.Cache;
import com.guo.duoduo.anyshareofandroid.ui.transfer.view.AppSelectAdapter;
import com.guo.duoduo.anyshareofandroid.ui.uientity.AppInfo;
import com.guo.duoduo.anyshareofandroid.ui.uientity.IInfo;
import com.guo.duoduo.anyshareofandroid.ui.view.MyWindowManager;
import com.guo.duoduo.anyshareofandroid.utils.DeviceUtils;
import com.guo.duoduo.anyshareofandroid.utils.ViewUtils;
import com.guo.duoduo.p2pmanager.p2pconstant.P2PConstant;
import com.guo.duoduo.p2pmanager.p2pentity.P2PFileInfo;


/**
 * Created by 郭攀峰 on 2015/9/16.
 */
public class AppFragment extends Fragment
    implements
        AppSelectAdapter.OnItemClickListener,
        OnSelectItemClickListener
{
    private static final String tag = AppFragment.class.getSimpleName();

    public static final int ANIMATION_DURATION = 800;

    private View view = null;
    private List<IInfo> appList = new ArrayList<>();
    private PackageManager pkManager;
    private AppFragmentHandler handler;
    private AppSelectAdapter adapter;
    private RecyclerView recyclerView;
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

            recyclerView = (RecyclerView) view.findViewById(R.id.recycleview);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
            adapter = new AppSelectAdapter(getActivity(), appList);
            adapter.setOnItemClickListener(this);
            recyclerView.setAdapter(adapter);
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
    public void onItemClick(View view, int position)
    {
        AppInfo info = ((AppInfo) adapter.getItem(position));
        P2PFileInfo fileInfo = new P2PFileInfo();
        fileInfo.name = info.getFileName();
        fileInfo.type = P2PConstant.TYPE.APP;
        fileInfo.size = new File(info.getFilePath()).length();
        fileInfo.path = info.getFilePath();

        if (Cache.selectedList.contains(fileInfo))
        {
            Cache.selectedList.remove(fileInfo);
        }
        else
        {
            Cache.selectedList.add(fileInfo);
            startFloating(view, position);
        }
        adapter.notifyDataSetChanged();
        clickListener.onItemClicked(P2PConstant.TYPE.APP);
    }

    @Override
    public void onItemClicked(int type)
    {

    }

    private void startFloating(View view, int position)
    {
        if (!MyWindowManager.isWindowShowing())
        {
            int[] location = ViewUtils.getViewItemLocation(view);
            int viewX = location[0];
            int viewY = location[1];

            MyWindowManager.createSmallWindow(getActivity(), viewX, viewY, 0, 0, adapter
                    .getItem(position).getFileIcon());
        }
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
