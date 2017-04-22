package com.guo.duoduo.anyshareofandroid.ui.transfer.fragment;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.guo.duoduo.anyshareofandroid.R;
import com.guo.duoduo.anyshareofandroid.constant.Constant;
import com.guo.duoduo.anyshareofandroid.sdk.cache.Cache;
import com.guo.duoduo.anyshareofandroid.ui.transfer.view.MusicSelectAdapter;
import com.guo.duoduo.anyshareofandroid.ui.uientity.IInfo;
import com.guo.duoduo.anyshareofandroid.ui.uientity.MusicInfo;
import com.guo.duoduo.anyshareofandroid.ui.uientity.PictureInfo;
import com.guo.duoduo.anyshareofandroid.ui.view.MyWindowManager;
import com.guo.duoduo.anyshareofandroid.utils.DeviceUtils;
import com.guo.duoduo.anyshareofandroid.utils.ViewUtils;
import com.guo.duoduo.p2pmanager.p2pconstant.P2PConstant;
import com.guo.duoduo.p2pmanager.p2pentity.P2PFileInfo;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by longsky on 17-4-18.
 */

public class MusicFragment extends BasicFragment
        implements
        MusicSelectAdapter.OnItemClickListener,
        OnSelectItemClickListener {

    private MusicFragment.QueryHandler queryHandler;

    private View view;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private MusicSelectAdapter adapter;

    private final List<IInfo> musicList = new ArrayList<>();
    private OnSelectItemClickListener clickListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(tag, "MusicFragment onCreateView function");
        if (view == null) {
            handler = new MusicFragment.MusicHandler(this);
            view = inflater.inflate(R.layout.view_select, container, false);
            recyclerView = (RecyclerView) view.findViewById(R.id.recycleview);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
            progressBar = (ProgressBar) view.findViewById(R.id.loading);
            adapter = new MusicSelectAdapter(getActivity(), musicList);
            adapter.setOnItemClickListener(this);
            recyclerView.setAdapter(adapter);

            requestReadExternalPermission();
        }

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        try {
            clickListener = (OnSelectItemClickListener) activity;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        super.onAttach(activity);
    }

    @Override
    protected void onReadExternalPermissionPermit(){
        getMusics();
    }

    @Override
    protected void onReadExternalPermissionDenial(){

    }

    @Override
    public void onItemClicked(int type) {

    }

    @Override
    public void onItemClick(View view, int position) {
        final PictureInfo info = ((PictureInfo) adapter.getItem(position));

        final P2PFileInfo fileInfo = new P2PFileInfo();
        fileInfo.name = info.getFileName();
        fileInfo.type = P2PConstant.TYPE.PIC;
        fileInfo.size = new File(info.getFilePath()).length();
        fileInfo.path = info.getFilePath();

        if (Cache.selectedList.contains(fileInfo)) {
            Cache.selectedList.remove(fileInfo);
        } else {
            Cache.selectedList.add(fileInfo);

            startFloating(view, position);
        }
        adapter.notifyDataSetChanged();
        clickListener.onItemClicked(P2PConstant.TYPE.PIC);
    }

    private void startFloating(View view, int position) {
        if (!MyWindowManager.isWindowShowing()) {
            final int[] location = ViewUtils.getViewItemLocation(view);
            final int viewX = location[0];
            final int viewY = location[1];

            MyWindowManager.createSmallWindow(getActivity(), viewX, viewY, 0, 0,
                    ((ImageView) view).getDrawable());
        }
    }

    private void getMusics() {
        if (queryHandler == null) {
            queryHandler = new MusicFragment.QueryHandler(getActivity());
        }
        queryHandler.startQuery(1, null, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media.DATA, MediaStore.Audio.Media._ID},
                null, null, MediaStore.Audio.Media.DATE_MODIFIED + " DESC");
    }

    @Override
    protected String getFragmentTag() {
        return tag;
    }

    private class QueryHandler extends AsyncQueryHandler {
        public QueryHandler(Context context) {
            super(context.getContentResolver());
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            final List<IInfo> musicInfo = new ArrayList<>();

            if (cursor != null) {
                for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
                    String str = cursor.getString(0);
                    if (str.endsWith(".mp3") || str.endsWith(".wav") || str.endsWith(".mp4")) {
                        final File file = new File(str);
                        if (file.exists()) {
                            final MusicInfo info = new MusicInfo();
                            info.setFileName(DeviceUtils.getFileName(str));
                            info.setFilePath(str);
                            info.setFileSize(DeviceUtils.getFileSize(file.length()));
                            if (!musicInfo.contains(info)) {
                                musicInfo.add(info);
                            }
                        }
                    }
                }
            }

            /*huawei honor android 7.0 cursor maybe null */
            if ((null != cursor)&&(!cursor.isClosed())) {
                cursor.close();
            }

            Log.d(tag, "music size =" + musicInfo.size());
            final Message msg = Message.obtain();
            msg.what = Constant.MSG.MUSIC_OK;
            msg.obj = musicInfo;
            handler.sendMessage(msg);
        }
    }

    private static class MusicHandler extends Handler {
        private WeakReference<MusicFragment> weakReference;

        public MusicHandler(MusicFragment fragment) {
            weakReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            final MusicFragment fragment = weakReference.get();
            if (fragment == null)
                return;
            if (fragment.getActivity() == null)
                return;
            if (fragment.getActivity().isFinishing())
                return;

            switch (msg.what) {
                case Constant.MSG.MUSIC_OK :
                    fragment.musicList.clear();
                    fragment.musicList.addAll((ArrayList<IInfo>) msg.obj);
                    fragment.progressBar.setVisibility(View.GONE);
                    fragment.adapter.setMusics(fragment.musicList);
                    fragment.adapter.notifyDataSetChanged();
                    break;
            }
        }
    }


    private MusicFragment.MusicHandler handler;
    private static final String tag = PictureFragment.class.getSimpleName();
}
