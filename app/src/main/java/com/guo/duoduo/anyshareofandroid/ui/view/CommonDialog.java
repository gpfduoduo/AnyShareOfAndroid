package com.guo.duoduo.anyshareofandroid.ui.view;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.guo.duoduo.anyshareofandroid.MyApplication;
import com.guo.duoduo.anyshareofandroid.R;

public class CommonDialog extends Dialog implements DialogInterface
{
    private Context mContext;
    private TextView mTitle;
    private ImageView mIcon;
    private LinearLayout mContentView;
    private Button mPositiveButton;
    private Button mNegativeButton;
    private LinearLayout mTitleParent;
    private LinearLayout mButtonParent;

    public CommonDialog(Context context)
    {
        super(context, R.style.myDialogTheme);
        super.setContentView(R.layout.view_common_dialog);
        mContext = context;
        init();
    }

    private void init()
    {
        mTitleParent = (LinearLayout) this.findViewById(R.id.comm_dialog_title_parent);
        mButtonParent = (LinearLayout) this.findViewById(R.id.comm_dialog_bottom);
        mTitle = (TextView) this.findViewById(R.id.comm_dialog_title);
        mIcon = (ImageView) this.findViewById(R.id.comm_dialog_icon);
        mContentView = (LinearLayout) this.findViewById(R.id.comm_dialog_content);
        mPositiveButton = (Button) this.findViewById(R.id.comm_dialog_positive_button);
        mNegativeButton = (Button) this.findViewById(R.id.comm_dialog_negative_button);
    }

    @Override
    public void setTitle(CharSequence title)
    {
        mTitle.setText(title);
        mTitleParent.setVisibility(View.VISIBLE);
    }

    @Override
    public void setTitle(int titleId)
    {
        mTitle.setText(titleId);
        mTitleParent.setVisibility(View.VISIBLE);
    }

    @Override
    public void setContentView(int layoutResID)
    {
        mContentView.removeAllViewsInLayout();
        mContentView.addView(LayoutInflater.from(mContext).inflate(layoutResID, null));
    }

    @Override
    public void setContentView(View view)
    {
        mContentView.removeAllViewsInLayout();
        mContentView.addView(view);
    }

    public void setPositiveButton(int textId,
            final OnClickListener listener)
    {
        mButtonParent.setVisibility(View.VISIBLE);
        mPositiveButton.setVisibility(View.VISIBLE);
        mPositiveButton.setText(textId);
        mPositiveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (listener != null)
                {
                    listener.onClick(CommonDialog.this, mPositiveButton.getId());
                }
                CommonDialog.this.dismiss();
            }
        });
    }

    public void setNegativeButton(int textId,
            final OnClickListener listener)
    {
        mButtonParent.setVisibility(View.VISIBLE);
        mNegativeButton.setVisibility(View.VISIBLE);
        mNegativeButton.setText(textId);
        mNegativeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (listener != null)
                {
                    listener.onClick(CommonDialog.this, mNegativeButton.getId());
                }
                CommonDialog.this.dismiss();
            }
        });
    }

    public void setIcon(int iconId)
    {
        mIcon.setImageResource(iconId);
        mTitleParent.setVisibility(View.VISIBLE);
    }

    public void setIcon(Bitmap bm)
    {
        mIcon.setImageBitmap(bm);
        mTitleParent.setVisibility(View.VISIBLE);
    }

    public void setIcon(Drawable drawbale)
    {
        mIcon.setImageDrawable(drawbale);
        mTitleParent.setVisibility(View.VISIBLE);
    }

    public void setMessage(CharSequence message)
    {
        TextView textView = (TextView) LayoutInflater.from(mContext).inflate(
            R.layout.view_common_dialog_message, null);
        textView.setText(message);
        textView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT));
        setContentView(textView);
    }

    public void setMessage(int textId)
    {
        setMessage(mContext.getResources().getString(textId));
    }

    @Override
    public void show()
    {
        if (mPositiveButton.getVisibility() == View.VISIBLE
            && mNegativeButton.getVisibility() == View.VISIBLE)
        {
            mPositiveButton
                    .setBackgroundResource(R.drawable.common_dlg_rightbtn_selector);
            mNegativeButton.setBackgroundResource(R.drawable.common_dlg_leftbtn_selector);
            this.findViewById(R.id.devider_positive_negative).setVisibility(View.VISIBLE);
        }
        else if (mPositiveButton.getVisibility() == View.VISIBLE)
        {
            mPositiveButton.setBackgroundResource(R.drawable.common_dlg_fullbtn_selector);
        }
        else if (mNegativeButton.getVisibility() == View.VISIBLE)
        {
            mNegativeButton.setBackgroundResource(R.drawable.common_dlg_fullbtn_selector);
        }

        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = MyApplication.SCREEN_WIDTH;
        window.setAttributes(lp);

        super.show();
    }

}
