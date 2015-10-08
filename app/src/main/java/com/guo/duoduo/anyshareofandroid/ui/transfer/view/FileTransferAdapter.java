package com.guo.duoduo.anyshareofandroid.ui.transfer.view;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.guo.duoduo.anyshareofandroid.R;
import com.guo.duoduo.anyshareofandroid.sdk.cache.Cache;
import com.guo.duoduo.anyshareofandroid.utils.DeviceUtils;
import com.guo.duoduo.p2pmanager.p2pconstant.P2PConstant;
import com.guo.duoduo.p2pmanager.p2pentity.P2PFileInfo;


/**
 * Created by 郭攀峰 on 2015/9/24.
 */
public class FileTransferAdapter extends BaseAdapter
{

    Context context;
    LayoutInflater layoutInflater;

    public FileTransferAdapter(Context context)
    {
        this.context = context;
        layoutInflater = layoutInflater.from(context);
    }

    @Override
    public int getCount()
    {
        return Cache.selectedList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return Cache.selectedList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;

        if (convertView == null)
        {
            convertView = layoutInflater.inflate(R.layout.view_file_transfer_item, null);
            holder = new ViewHolder();
            holder.file_image = (ImageView) convertView.findViewById(R.id.file_image);
            holder.file_name = (TextView) convertView.findViewById(R.id.file_name);
            holder.file_size = (TextView) convertView.findViewById(R.id.file_size);
            holder.file_trans_speed = (TextView) convertView
                    .findViewById(R.id.file_trans_speed);
            holder.trans_progress = (ProgressBar) convertView
                    .findViewById(R.id.trans_progress);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        P2PFileInfo file = Cache.selectedList.get(position);
        if (file != null)
        {
            if (file.type == P2PConstant.TYPE.APP)
            {
                holder.file_image.setImageResource(R.mipmap.icon_apk);
            }
            else if (file.type == P2PConstant.TYPE.PIC)
            {
                holder.file_image.setImageResource(R.mipmap.icon_image);
            }

            holder.file_name.setText(file.name);
            holder.file_size.setText(DeviceUtils.convertByte(file.size));
            holder.file_trans_speed.setText(file.percent + "%");
            holder.trans_progress.setProgress(file.percent);
            if (file.percent >= 100)
            {
                holder.trans_progress.setVisibility(View.INVISIBLE);
                holder.file_trans_speed.setText(context
                        .getString(R.string.file_has_completed));
                holder.file_trans_speed.setTextColor(context.getResources().getColor(
                    R.color.blue));
            }
            else
            {
                holder.trans_progress.setVisibility(View.VISIBLE);
                if (file.percent == 0)
                {
                    holder.file_trans_speed
                            .setText(context.getString(R.string.file_wait));
                    holder.file_trans_speed.setTextColor(context.getResources().getColor(
                        R.color.green));
                }
            }
        }

        return convertView;
    }

    private static class ViewHolder
    {
        public ImageView file_image;
        public TextView file_name;
        public TextView file_size;
        public TextView file_trans_speed;
        public ProgressBar trans_progress;
    }
}
