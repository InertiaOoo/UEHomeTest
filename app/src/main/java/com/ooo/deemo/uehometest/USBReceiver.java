package com.ooo.deemo.uehometest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class USBReceiver extends BroadcastReceiver{

    private static final String TAG = USBReceiver.class.getSimpleName();

    private  static  boolean  USBFLAG = false;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)||action.equals(UsbManager.ACTION_USB_ACCESSORY_ATTACHED)){

            USBFLAG = true;
            String mountPath = intent.getData().getPath();
            Log.e(TAG,"mountPath = "+mountPath);
            if (!TextUtils.isEmpty(mountPath)) {
                //读取到U盘路径再做其他业务逻辑
                Log.e("读取到U盘路径","读取到U盘路径");
            }
        } else if(action.equals(UsbManager.ACTION_USB_DEVICE_DETACHED)){
            USBFLAG = false;
        }
    }


    public  static  boolean getUSBFLAG(){

        return USBFLAG;
    }
}
