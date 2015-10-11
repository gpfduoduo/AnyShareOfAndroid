package com.guo.duoduo.anyshareofandroid.ui.setting.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.guo.duoduo.anyshareofandroid.R;


/**
 * show the received images, click item to browse the big one
 */
public class ReceivedPicture extends Fragment
{
    public static ReceivedPicture newInstance()
    {
        ReceivedPicture fragment = new ReceivedPicture();
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
        return inflater.inflate(R.layout.fragment_received, container, false);
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

}
