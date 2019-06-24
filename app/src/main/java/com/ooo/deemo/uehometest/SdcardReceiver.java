package com.ooo.deemo.uehometest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

public class SdcardReceiver extends BroadcastReceiver {

    private static boolean strmsg = false;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(Intent.ACTION_MEDIA_MOUNTED)){

            strmsg=true;
            Log.e("tag","sdcard mounted");
        }else if(action.equals(Intent.ACTION_MEDIA_UNMOUNTED)){


            strmsg=false;
            Log.e("tag","sdcard unmounted");
        }
    }



    public static boolean getStrMsg(){
        return strmsg;
    }





}