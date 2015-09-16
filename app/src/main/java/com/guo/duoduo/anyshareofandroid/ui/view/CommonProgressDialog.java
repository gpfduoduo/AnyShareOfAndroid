package com.guo.duoduo.anyshareofandroid.ui.view;


import android.app.Dialog;
import android.content.Context;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.guo.duoduo.anyshareofandroid.R;


public class CommonProgressDialog extends Dialog
{
    private TextView mMessage;
    private ProgressBar mProgress;

    public CommonProgressDialog(Context context)
    {
        super(context, R.style.progressDialogTheme);
        setContentView(R.layout.view_common_progress_dialog);
        init();
    }

    public void init()
    {
        mMessage = (TextView) findViewById(R.id.comm_progress_dialog_msg);
        mProgress = (ProgressBar) findViewById(R.id.comm_progress_dialog_progress);
    }

    public void setMessage(CharSequence msg)
    {
        mMessage.setText(msg);
    }

    public void setProgress(int progress)
    {
        mProgress.setProgress(progress);
    }

}
