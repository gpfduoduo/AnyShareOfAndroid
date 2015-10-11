package com.guo.duoduo.anyshareofandroid.ui.setting.fragment;


import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guo.duoduo.anyshareofandroid.R;
import com.guo.duoduo.anyshareofandroid.ui.setting.view.ReceivedAppAdapter;
import com.guo.duoduo.anyshareofandroid.ui.uientity.AppInfo;
import com.guo.duoduo.anyshareofandroid.ui.uientity.IInfo;
import com.guo.duoduo.anyshareofandroid.utils.ApkTools;
import com.guo.duoduo.anyshareofandroid.utils.DeviceUtils;
import com.guo.duoduo.p2pmanager.p2pconstant.P2PConstant;
import com.guo.duoduo.p2pmanager.p2pcore.P2PManager;


/**
 * show the received app files, click the item to install
 */
public class ReceivedApp extends Fragment
{

    private static final String tag = ReceivedApp.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private ReceivedAppAdapter mAdapter;
    private ArrayList<IInfo> mAppList;
    private TextView mNoContentTextView;
    private View mView;

    public static ReceivedApp newInstance()
    {
        ReceivedApp fragment = new ReceivedApp();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        if (mView == null)
        {
            mView = inflater.inflate(R.layout.fragment_received, container, false);
            mRecyclerView = (RecyclerView) mView.findViewById(R.id.received_recyclerview);
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
            mRecyclerView.setVisibility(View.GONE);
            mNoContentTextView = (TextView) mView.findViewById(R.id.received_textview);
            initData();
        }
        return mView;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }

    private void initData()
    {
        String appDir = P2PManager.getSavePath(P2PConstant.TYPE.APP);
        Log.d(tag, "app dir = " + appDir);

        if (!TextUtils.isEmpty(appDir))
        {
            File appFile = new File(appDir);
            if (appFile.exists() && appFile.isDirectory())
            {
                File[] appFileArray = appFile.listFiles();
                if (appFileArray != null && appFileArray.length > 0)
                {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mNoContentTextView.setVisibility(View.GONE);
                    mAppList = new ArrayList<>();
                    for (File app : appFileArray)
                    {
                        AppInfo appInfo = new AppInfo();
                        if (app.isFile() && app.getAbsolutePath().endsWith(".apk"))
                        {
                            appInfo.appLabel = app.getName();
                            appInfo.appSize = DeviceUtils.convertByte(app.length());
                            appInfo.appIcon = ApkTools.geTApkIcon(getActivity(),
                                app.getAbsolutePath());
                            appInfo.appFilePath = app.getAbsolutePath();

                            if (!mAppList.contains(appInfo))
                                mAppList.add(appInfo);
                        }
                    }

                    mAdapter = new ReceivedAppAdapter(getActivity(), mAppList);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
        }
    }

}
