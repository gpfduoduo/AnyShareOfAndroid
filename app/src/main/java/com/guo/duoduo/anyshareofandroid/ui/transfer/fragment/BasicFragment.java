package com.guo.duoduo.anyshareofandroid.ui.transfer.fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by longsky on 2017/4/19.
 */

public abstract class BasicFragment extends Fragment {

    protected abstract String getFragmentTag();

    protected void onReadExternalPermissionPermit(){

    }

    protected void onReadExternalPermissionDenial(){

    }

    /*from api 23 Android 6.0 require Runtime permission request*/
    @SuppressLint("NewApi")
    protected void requestReadExternalPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getContext().checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Log.d(getFragmentTag(), "READ permission IS NOT granted...");

                if (shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    onReadExternalPermissionPermit();
                } else {
                    // 0 是自己定义的请求coude
                    requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, requestReadExternalPermissionCode);
                }
            } else {
                Log.d(getFragmentTag(), "READ permission is granted...");
                onReadExternalPermissionPermit();
            }
        }else{
            onReadExternalPermissionPermit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d(getFragmentTag(), "requestCode=" + requestCode + "; --->" + permissions.toString()
                + "; grantResult=" + grantResults.toString());
        if(requestReadExternalPermissionCode == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                // permission was granted
                // request successfully, handle you transactions
                onReadExternalPermissionPermit();
            } else {
                // permission denied
                // request failed
                onReadExternalPermissionDenial();
            }
        }
    }

    private final static int requestReadExternalPermissionCode = 0;
}
