// IBluetoothStateCallback.aidl
package com.dfzt.bluetooth.callback;

// Declare any non-default types here with import statements

interface IBluetoothStateCallback {

    //播放状态的回调
    void onBluetoothPlayState(boolean isPlaying);

    //蓝牙连接状态的回调  0已连接 1已断开 2正在连接
    void onBluetoothConnectState(int state,String name);

    //蓝牙打开关闭的状态  true蓝牙已打开 false蓝牙已关闭
    void onBluetoothOpenState(boolean opened);
}
