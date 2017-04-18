package com.guo.duoduo.anyshareofandroid.ui.transfer.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guo.duoduo.anyshareofandroid.R;
import com.guo.duoduo.anyshareofandroid.ui.uientity.IInfo;

import java.util.List;

/**
 * Created by longsky on 17-4-18.
 */

public class MusicSelectAdapter extends RecyclerView.Adapter<MusicSelectAdapter.MyViewHolder> {

    private Context context;
    private List<IInfo> list;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private AppSelectAdapter.OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(AppSelectAdapter.OnItemClickListener listener)
    {
        onItemClickListener = listener;
    }

    public MusicSelectAdapter(Context context, List<IInfo> list)
    {
        this.context = context;
        this.list = list;
    }

    @Override
    public void onBindViewHolder(MusicSelectAdapter.MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public MusicSelectAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        MusicSelectAdapter.MyViewHolder holder = new MusicSelectAdapter.MyViewHolder(LayoutInflater.from(context).inflate(
                R.layout.view_app_item, null));
        return holder;
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imageView;
        ImageView app_choice;
        TextView appName;
        TextView appSize;
        LinearLayout itemLayout;

        public MyViewHolder(View view)
        {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.AppIcon);
            appName = (TextView) view.findViewById(R.id.AppName);
            appSize = (TextView) view.findViewById(R.id.AppSize);
            app_choice = (ImageView) view.findViewById(R.id.app_choice);
            itemLayout = (LinearLayout) view.findViewById(R.id.app_item_layout);
        }
    }

}
