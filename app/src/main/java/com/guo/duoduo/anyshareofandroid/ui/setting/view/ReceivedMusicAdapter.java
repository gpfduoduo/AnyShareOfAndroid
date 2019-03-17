package com.guo.duoduo.anyshareofandroid.ui.setting.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guo.duoduo.anyshareofandroid.R;
import com.guo.duoduo.anyshareofandroid.ui.uientity.AppInfo;
import com.guo.duoduo.anyshareofandroid.ui.uientity.IInfo;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by longsky on 2017/4/25.
 */

public class ReceivedMusicAdapter extends RecyclerView.Adapter<ReceivedMusicAdapter.MyHolder> {
    private Context mContext;
    private ArrayList<IInfo> mMusics;

    public ReceivedMusicAdapter(Context context, ArrayList<IInfo> appInfoList) {
        mContext = context;
        mMusics = appInfoList;
    }

    @Override
    public ReceivedMusicAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ReceivedMusicAdapter.MyHolder myHolder = new ReceivedMusicAdapter.MyHolder(LayoutInflater.from(mContext).inflate(
                R.layout.view_received_app_item, null));

        return myHolder;
    }

    @Override
    public void onBindViewHolder(ReceivedMusicAdapter.MyHolder holder, final int position) {
        final AppInfo app = (AppInfo) mMusics.get(position);

        if (app == null)
            return;

        final Drawable drawable = app.getFileIcon();
        if(drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            holder.mIcon.setImageBitmap(bitmapDrawable.getBitmap());
        }

        holder.mName.setText(app.getFileName());
        holder.mSize.setText(app.getFileSize());

        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playVideo(mContext,app.getFilePath());
            }
        });
    }

    private static void playVideo(Context context,String videoPath){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String strend="";
        if(videoPath.toLowerCase().endsWith(".mp4")){
            strend="mp4";
        }
        else if(videoPath.toLowerCase().endsWith(".3gp")){
            strend="3gp";
        }
        else if(videoPath.toLowerCase().endsWith(".mov")){
            strend="mov";
        }
        else if(videoPath.toLowerCase().endsWith(".wmv")){
            strend="wmv";
        }

        intent.setDataAndType(Uri.parse(videoPath), "video/"+strend);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return mMusics.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder
    {
        ImageView mIcon;
        TextView mName;
        TextView mSize;
        LinearLayout mLayout;

        public MyHolder(View view)
        {
            super(view);
            mIcon = (ImageView) view.findViewById(R.id.received_app_icon);
            mName = (TextView) view.findViewById(R.id.received_app_name);
            mSize = (TextView) view.findViewById(R.id.received_app_size);
            mLayout = (LinearLayout) view.findViewById(R.id.received_app_layout);
        }

    }
}