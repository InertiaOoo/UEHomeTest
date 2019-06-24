package com.ooo.deemo.uehometest;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BlueToothReceiver extends BroadcastReceiver {
private static boolean BLUETFLAG = false;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
            BLUETFLAG = true;

            //连接上了
        } else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
            //蓝牙连接被切断
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String name = device.getName();
            Log.e(name + "的连接被断开", "");
            BLUETFLAG = false;
            return;
        }
    }



    public static boolean getBLUETFLAG(){
        return BLUETFLAG;
    }
}
