package com.guo.duoduo.anyshareofandroid.ui.setting.fragment;

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
import com.guo.duoduo.anyshareofandroid.ui.setting.view.ReceivedMusicAdapter;
import com.guo.duoduo.anyshareofandroid.ui.uientity.AppInfo;
import com.guo.duoduo.anyshareofandroid.ui.uientity.IInfo;
import com.guo.duoduo.anyshareofandroid.utils.ApkTools;
import com.guo.duoduo.anyshareofandroid.utils.DeviceUtils;
import com.guo.duoduo.p2pmanager.p2pconstant.P2PConstant;
import com.guo.duoduo.p2pmanager.p2pcore.P2PManager;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by longsky on 2017/4/23.
 */

public class ReceivedMusic extends Fragment {
    public static ReceivedMusic newInstance() {
        ReceivedMusic fragment = new ReceivedMusic();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
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

    private void initData() {
        final String musicDir = P2PManager.getSavePath(P2PConstant.TYPE.MUSIC);
        Log.d(tag, "music dir = " + musicDir);

        if (!TextUtils.isEmpty(musicDir)) {
            File musicFile = new File(musicDir);
            if (musicFile.exists() && musicFile.isDirectory()) {
                File[] musicFileArray = musicFile.listFiles();
                if (musicFileArray != null && musicFileArray.length > 0) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mNoContentTextView.setVisibility(View.GONE);
                    mMusics = new ArrayList<>();
                    for (File music : musicFileArray) {
                        AppInfo appInfo = new AppInfo();
                        if (music.isFile() && (music.getAbsolutePath().endsWith(".mp3") || music.getAbsolutePath().endsWith(".mp4") || music.getAbsolutePath().endsWith(".wav") )) {
                            appInfo.appLabel = music.getName();
                            appInfo.appSize = DeviceUtils.convertByte(music.length());
                            appInfo.appIcon = ApkTools.geTApkIcon(getActivity(),
                                    music.getAbsolutePath());
                            appInfo.appFilePath = music.getAbsolutePath();

                            if (!mMusics.contains(appInfo))
                                mMusics.add(appInfo);
                        }
                    }

                    mAdapter = new ReceivedMusicAdapter(getActivity(), mMusics);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
        }
    }

    private static final String tag = ReceivedMusic.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private ReceivedMusicAdapter mAdapter;
    private ArrayList<IInfo> mMusics;
    private TextView mNoContentTextView;
    private View mView;

}
