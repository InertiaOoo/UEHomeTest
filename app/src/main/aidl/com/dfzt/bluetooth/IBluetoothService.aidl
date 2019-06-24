// IBluetoothService.aidl
package com.dfzt.bluetooth;

// Declare any non-default types here with import statements
import com.dfzt.bluetooth.callback.IBluetoothStateCallback;
interface IBluetoothService {
    //上一曲
    void previous();

    //下一曲
    void next();

    //暂停
    void pause();

    //播放
    void play();

    //获取当前的播放状态
    boolean getPlayState();

    //获取蓝牙开关的状态
    boolean getOpenState();

    //打开蓝牙
    void openBluetooth();

    //关闭蓝牙
    void closeBluetooth();

    //注册获取播放状态接口，获取连接状态接口
    void registerBluetoothStateCallback(in IBluetoothStateCallback callback);

    //销毁获取播放状态接口，获取连接状态接口
    void unRegisterBluetoothStateCallback(in IBluetoothStateCallback callback);
}
