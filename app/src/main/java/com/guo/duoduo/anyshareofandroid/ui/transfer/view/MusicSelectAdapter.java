package com.guo.duoduo.anyshareofandroid.ui.transfer.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guo.duoduo.anyshareofandroid.R;
import com.guo.duoduo.anyshareofandroid.sdk.cache.Cache;
import com.guo.duoduo.anyshareofandroid.ui.uientity.IInfo;
import com.guo.duoduo.p2pmanager.p2pentity.P2PFileInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by longsky on 17-4-18.
 */

public class MusicSelectAdapter extends RecyclerView.Adapter<MusicSelectAdapter.MyViewHolder> {

    private final Context context;
    private final List<IInfo> list = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private MusicSelectAdapter.OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(MusicSelectAdapter.OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public void setMusics(final List<IInfo> list){
        this.list.clear();
        this.list.addAll(list);
    }

    public MusicSelectAdapter(final Context context, final List<IInfo> list) {
        this.context = context;
        this.list.addAll(list);
    }

    @Override
    public void onBindViewHolder(final MusicSelectAdapter.MyViewHolder holder, final int position) {
        Glide.with(context).load(list.get(position).getFilePath()).into(holder.imageView);

        final IInfo info = list.get(position);
        final P2PFileInfo fileInfo = new P2PFileInfo();
        fileInfo.name = info.getFileName();
        fileInfo.type = info.getFileType();
        fileInfo.size = new File(info.getFilePath()).length();
        fileInfo.path = info.getFilePath();

        if (Cache.selectedList.contains(fileInfo))
            holder.music_choice.setVisibility(View.VISIBLE);
        else
            holder.music_choice.setVisibility(View.INVISIBLE);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(holder.imageView,
                            holder.getLayoutPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public MusicSelectAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final MusicSelectAdapter.MyViewHolder holder = new MusicSelectAdapter.MyViewHolder(LayoutInflater.from(context).inflate(
                R.layout.view_app_item, null));
        return holder;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView music_choice;
        TextView appName;
        TextView appSize;
        LinearLayout itemLayout;

        public MyViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.AppIcon);
            appName = (TextView) view.findViewById(R.id.AppName);
            appSize = (TextView) view.findViewById(R.id.AppSize);
            music_choice = (ImageView) view.findViewById(R.id.app_choice);
            itemLayout = (LinearLayout) view.findViewById(R.id.app_item_layout);
        }
    }

}
