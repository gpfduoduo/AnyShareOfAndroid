package com.guo.duoduo.anyshareofandroid.ui.transfer.fragment;


import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

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
import com.guo.duoduo.anyshareofandroid.ui.transfer.view.ImageSelectAdapter;
import com.guo.duoduo.anyshareofandroid.ui.uientity.IInfo;
import com.guo.duoduo.anyshareofandroid.ui.uientity.PictureInfo;
import com.guo.duoduo.anyshareofandroid.ui.view.MyWindowManager;
import com.guo.duoduo.anyshareofandroid.utils.DeviceUtils;
import com.guo.duoduo.anyshareofandroid.utils.ViewUtils;
import com.guo.duoduo.p2pmanager.p2pconstant.P2PConstant;
import com.guo.duoduo.p2pmanager.p2pentity.P2PFileInfo;


/**
 * Created by 郭攀峰 on 2015/9/16.
 */
public class PictureFragment extends BasicFragment
    implements
        ImageSelectAdapter.OnItemClickListener,
        OnSelectItemClickListener
{

    private static final String tag = PictureFragment.class.getSimpleName();

    private View view;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ImageSelectAdapter adapter;

    private final List<IInfo> picList = new ArrayList<>();
    private PictureHandler handler;
    private QueryHandler queryHandler;

    private OnSelectItemClickListener clickListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(tag, "PictureFragment onCreateView function");
        if (view == null)
        {
            handler = new PictureHandler(this);
            view = inflater.inflate(R.layout.view_select, container, false);
            recyclerView = (RecyclerView) view.findViewById(R.id.recycleview);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
            progressBar = (ProgressBar) view.findViewById(R.id.loading);
            adapter = new ImageSelectAdapter(getActivity(), picList);
            adapter.setOnItemClickListener(this);
            recyclerView.setAdapter(adapter);

            requestReadExternalPermission();
        }

        return view;
    }

    @Override
    protected void onReadExternalPermissionPermit(){
        getPictures();
    }

    @Override
    protected void onReadExternalPermissionDenial(){

    }

    private void getPictures() {
        if (queryHandler == null) {
            queryHandler = new QueryHandler(getActivity());
        }
        queryHandler.startQuery(1, null, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID},
            null, null, MediaStore.Images.Media.DATE_MODIFIED + " DESC");
    }

    @Override
    public void onAttach(Activity activity)
    {
        try {
            clickListener = (OnSelectItemClickListener) activity;
        }
        catch (Exception e) {
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

    @Override
    public void onItemClicked(int type) {

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
            final List<IInfo> picInfo = new ArrayList<>();

            if (cursor != null) {
                for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
                    final String str = cursor.getString(0);
                    if (str.endsWith(".jpg") || str.endsWith(".png") || str.endsWith(".jpeg")) {
                        final File file = new File(str);
                        if (file.exists()) {
                            PictureInfo info = new PictureInfo();
                            info.picPath = str;
                            info.picSize = DeviceUtils.getFileSize(file.length());
                            info.picName = DeviceUtils.getFileName(str);
                            if (!picInfo.contains(info))
                                picInfo.add(info);
                        }
                    }
                }
            }

            /*huawei honor android 7.0 cursor maybe null */
            if ((null != cursor)&&(!cursor.isClosed())) {
                cursor.close();
            }

            Log.d(tag, "pic size =" + picInfo.size());
            Message msg = Message.obtain();
            msg.what = Constant.MSG.PICTURE_OK;
            msg.obj = picInfo;
            handler.sendMessage(msg);
        }
    }

    private static class PictureHandler extends Handler {
        private WeakReference<PictureFragment> weakReference;

        public PictureHandler(PictureFragment fragment) {
            weakReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            final PictureFragment fragment = weakReference.get();
            if (fragment == null)
                return;
            if (fragment.getActivity() == null)
                return;
            if (fragment.getActivity().isFinishing())
                return;

            switch (msg.what) {
                case Constant.MSG.PICTURE_OK :
                    fragment.picList.clear();
                    fragment.picList.addAll((ArrayList<IInfo>) msg.obj);
                    fragment.progressBar.setVisibility(View.GONE);
                    fragment.adapter.notifyDataSetChanged();
                    break;
            }
        }
    }
}
