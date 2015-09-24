package com.guo.duoduo.anyshareofandroid.ui.transfer.view;


import java.io.File;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.guo.duoduo.anyshareofandroid.R;
import com.guo.duoduo.anyshareofandroid.sdk.cache.Cache;
import com.guo.duoduo.anyshareofandroid.sdk.p2p.p2pconstant.P2PConstant;
import com.guo.duoduo.anyshareofandroid.sdk.p2p.p2pentity.P2PFileInfo;
import com.guo.duoduo.anyshareofandroid.ui.uientity.AppInfo;
import com.guo.duoduo.anyshareofandroid.ui.uientity.IInfo;


/**
 * Created by 郭攀峰 on 2015/9/15.
 */
public class AppSelectAdapter extends FileSelectAdapter
{
    private Context context;
    private List<IInfo> list;

    public AppSelectAdapter(Context context, List<IInfo> list)
    {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public Object getItem(int i)
    {
        return list.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        View view = convertView;
        GetView getView;
        if (view == null)
        {
            view = LayoutInflater.from(context).inflate(R.layout.view_app_item, null);
            getView = new GetView();
            getView.imageView = (ImageView) view.findViewById(R.id.AppIcon);
            getView.appName = (TextView) view.findViewById(R.id.AppName);
            getView.appSize = (TextView) view.findViewById(R.id.AppSize);
            getView.app_choice = (ImageView) view.findViewById(R.id.app_choice);
            view.setTag(getView);
        }
        else
        {
            getView = (GetView) view.getTag();
        }

        getView.imageView.setImageBitmap(((BitmapDrawable) list.get(position)
                .getFileIcon()).getBitmap());

        IInfo info = list.get(position);
        P2PFileInfo fileInfo = new P2PFileInfo();
        fileInfo.name = info.getFileName();
        fileInfo.type = info.getFileType();
        fileInfo.size = new File(info.getFilePath()).length();
        fileInfo.path = info.getFilePath();

        if (Cache.selectedList.contains(fileInfo))
        {
            getView.app_choice.setVisibility(View.VISIBLE);
        }
        else
        {
            getView.app_choice.setVisibility(View.GONE);
        }

        getView.appName.setText(list.get(position).getFileName());
        getView.appSize.setText(list.get(position).getFileSize());

        return view;
    }

    private static class GetView
    {
        ImageView imageView;
        ImageView app_choice;
        TextView appName;
        TextView appSize;
    }
}
