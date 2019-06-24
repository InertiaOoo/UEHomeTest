package com.ooo.deemo.uehometest;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.input.InputManager;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.InputDevice;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dfzt.bluetooth.IBluetoothService;
import com.dfzt.bluetooth.callback.IBluetoothStateCallback;
import com.dfzt.dfzt_radio.RadioAidl;
import com.dfzt.dfzt_radio.callback.IESTRadioServiceCallBack;
import com.dfzt.music.aidl.IPlayerProvider;
import com.dfzt.music.aidl.Music;
import com.dfzt.music.aidl.callback.IPlayMusicStateListener;
import com.dfzt.olinemusic.IOnlineMusicAidl;
import com.dfzt.olinemusic.callback.ISearchMusicCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    private static Context context;
    //本地

    private MyService myService;
    private static WifiManager wifiManager;
    private UsbManager usbManager;
    private HashMap<String, UsbDevice> deviceList;
    private UsbDevice mUsbDevice;
    private static BluetoothAdapter blueadapter;
    private static final String ACTION_USB_PERMISSION = "com.github.mjdev.libaums.USB_PERMISSION";
    private static final String TAG = MainActivity.class.getSimpleName();
    private FileSystem currentFs;

    private static BlueToothReceiver blueToothReceiver;
    private SdcardReceiver sdcardReceiver;

    private static USBReceiver usbReceiver;


    private ResultReceiver mResultReceiver;

    //权限
    private String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
            , Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS};
    private AlertDialog dialog;

    //日志
    private static RecyclerView rv_log;
    private static List<TestLog> tl_List = new ArrayList<>();
    private static RecycleAdapter rAdapter;

    //关键信息
    private RecyclerView rv_show;
    private static List<ShowContent> sc_list = new ArrayList<>();
    private static ShowListAdapter slAdapter;

    private List<ShowContent> showContentList = new ArrayList<>();
    private ListView listView;
    private LVShowAdapter lvShowAdapter;

    private static int order = 1;
    private int MILLIS = 900;
    private Handler handler = new Handler();
    private Button bt_try;
    private static Button bt_go;
    private Button bt_check;
    private Button exit_bt;
    private ImageView exit_iv;

    //ForDialog
    View getlistview;
    String[] mlistText = {"全选", "WIFI", "USB", "SD", "蓝牙", "本地音乐", "在线音乐", "有声内容", "本地蓝牙"};
    ArrayList<Map<String, Object>> mData = new ArrayList<>();

    AlertDialog testdialog;
    AlertDialog.Builder builder;

    SimpleAdapter adapter;
    private static Boolean[] bl = {false, true, true, true, true, false, false, false, false};

    //MyDialog

    private MyDialog myDialog;


    //AIDL接口
    private static IBluetoothService iBluetoothService;
    private static IPlayerProvider iPlayerProvider;
    private static IOnlineMusicAidl iOnlineMusicAidl;
    private static RadioAidl radioAidl;

    //ForAIDLCALLBACK

    private static List<com.dfzt.olinemusic.entity.Music> musicList = new ArrayList<>();
    private com.dfzt.olinemusic.entity.Music currentMusic;

    private static Album album;

    private String omId;


    private static List<Album> albumList = new ArrayList<>();

    private static List<Album> albumList2 = new ArrayList<>();

    //线程池
    final ExecutorService executor = Executors.newSingleThreadExecutor();

    //状态指示
    private static boolean USBFLAG = false;
    private boolean SDFLAG = false;
    private boolean BTFLAG = false;
    private static boolean NETWORKSTATE = false;
    private static boolean BTLOCALFLAG = false;
    private static int netNum = 0;

    private boolean BTSYSFLAG = false;

    private static boolean hasTest = false;

    protected static final int STD_USB_REQUEST_GET_DESCRIPTOR = 0x06;
    protected static final int LIBUSB_DT_STRING = 0x03;


    //getmessage from interface


    private static final String TAB = "\t\t";

    private static boolean isThread = false;

    private static boolean LocalMusicFlag = true;
    private static boolean OnlineMusicFlag = true;
    private static boolean RadioContentFlag = true;


    private static ProgressBar pBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        ButterKnife.bind(this);

        getPermission();

//        initShowContents();

        initView();

        initList();
        initFlag();
        exit_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FileUtils.saveFile(tl_List);
//                FileUtils.saveFile(screenShotWholeScreen(), "testPic.jpeg", MainActivity.this);
//                Log.e("保存截图到", "//sdcard//UTest//testPic.jpeg");
//                Log.e("关闭应用", "");

                Intent i = new Intent(Intent.ACTION_MAIN);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);


                System.exit(0);

            }
        });

        bt_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                CreateMyDialog();// 点击创建Dialog
            }
        });

        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        blueadapter = BluetoothAdapter.getDefaultAdapter();


        bt_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
            }
        });

        bt_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isHasTest();
                if (hasTest == false) {
                    Toast.makeText(MainActivity.this, "当前无测试项目，请选择测试项目", Toast.LENGTH_SHORT).show();


                } else if (isThread) {
                    Toast.makeText(MainActivity.this, "当前正在测试", Toast.LENGTH_SHORT).show();

                } else {


                    order = 1;
                    tl_List.clear();
//
                    sc_list.clear();
                    Log.e("", Environment.getExternalStorageDirectory().getAbsolutePath());
                    albumList.clear();
                    slAdapter.notifyDataSetChanged();

                    rAdapter.notifyDataSetChanged();

//

                    bt_go.setText("正在进行测试");

                    pBar.setVisibility(View.VISIBLE);
                    Log.e("开始测试", "");
                    testAll();

                }
            }
        });
        hideBottomUIMenu();
    }


    private void initService() {
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("isService", true);

        startService(intent);

    }

    /*
    初始化标志
     */
    private static void initFlag() {
        LocalMusicFlag = true;
        OnlineMusicFlag = true;
        RadioContentFlag = true;

    }


    //注册组件
    private void initView() {
//        tv_show = findViewById(R.id.tv_show);

//        rv_show = findViewById(R.id.rv_show);
        rv_log = findViewById(R.id.rv_log);

        bt_go = findViewById(R.id.bt_go);
//        tv_show.setText("");
        exit_iv = findViewById(R.id.exit_iv);

        bt_check = findViewById(R.id.bt_check);

        bt_try = findViewById(R.id.bt_try);
//        listView = findViewById(R.id.lv_show);
//        rv_show.setLayoutManager(new LinearLayoutManager(this));
//        slAdapter = new ShowListAdapter(sc_list);
//        rv_show.setAdapter(slAdapter);


        pBar = findViewById(R.id.pbar);

        rv_show = findViewById(R.id.rv_show);

        rv_show.setLayoutManager(new LinearLayoutManager(this));
        slAdapter = new ShowListAdapter(sc_list);
        rv_show.setAdapter(slAdapter);


        rv_log.setLayoutManager(new LinearLayoutManager(this));
        rAdapter = new RecycleAdapter(tl_List);
        rv_log.setAdapter(rAdapter);

        MainActivity.context = getApplicationContext();


//         lvShowAdapter = new LVShowAdapter(this,R.layout.lv_show_layout,showContentList);
//        listView.setAdapter(lvShowAdapter);

        //开启AIDL连接服务
        starBlueToothService();

        startOnlineService();
        startSoundService();

        NETWORKSTATE = ping();

        registerBroadcastReceiver();

    }

    //注册广播监听
    private void registerBroadcastReceiver() {
        //USB
        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        filter1.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter1.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(usbStateReceiver, filter1);
//
        //SD
//        IntentFilter filter2 = new IntentFilter();
//        filter2.addAction(Intent.ACTION_MEDIA_MOUNTED);
//        filter2.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
//        filter2.addDataScheme("file");
//        registerReceiver(sdstateReceiver, filter2);


        //蓝牙
//        IntentFilter stateChangeFilter = new IntentFilter(
//                BluetoothAdapter.ACTION_STATE_CHANGED);
//        IntentFilter connectedFilter = new IntentFilter(
//                BluetoothDevice.ACTION_ACL_CONNECTED);
//        IntentFilter disConnectedFilter = new IntentFilter(
//                BluetoothDevice.ACTION_ACL_DISCONNECTED);
//        registerReceiver(btstateReceiver, stateChangeFilter);
//        registerReceiver(btstateReceiver, connectedFilter);
//        registerReceiver(btstateReceiver, disConnectedFilter);


    }

    //蓝牙广播监听
//    private BroadcastReceiver btstateReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
//                BTFLAG = true;
//                //连接上了
//            } else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
//                //蓝牙连接被切断
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                String name = device.getName();
//                Log.e(name + "的连接被断开", String.valueOf(getApplicationContext()));
//                BTFLAG = false;
//            }
//        }
//    };

//    sd广播监听
//    private final BroadcastReceiver sdstateReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) {
//
//                SDFLAG = true;
//
//
//            } else if (intent.getAction().equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
//
//                SDFLAG = false;
//
//            }
//
//
//        }
//    };

    //    usb广播监听
    private final BroadcastReceiver usbStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String ACTION = "android.hardware.usb.action.USB_STATE";
            String action = intent.getAction();
            //USB连接上手机时会发送广播android.hardware.usb.action.USB_STATE"及UsbManager.ACTION_USB_DEVICE_ATTACHED
            if (action.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED) || action.equals(ACTION)) {//判断其中一个就可以了
                Log.e("", "USB已经连接！");
                USBFLAG = true;

            } else if (action.equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {//USB被拔出
                Log.e("", "USB连接断开，程序退出！");
                USBFLAG = false;

            }
        }
    };


    public String getUSBName() {
        String strusbName = null;
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        if (deviceList.size() == 0) {
            return strusbName;
        }
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        if (deviceIterator.hasNext()) {
            UsbDevice device = (UsbDevice) deviceIterator.next();
            strusbName = device.getDeviceName();

            Log.d("", "Name: " + device.getDeviceName() + "\n"
                    + "VID: " + device.getVendorId()
                    + "       PID: " + device.getProductId());

            UsbInterface intf = device.getInterface(0);
            int epc = 0;
            epc = intf.getEndpointCount();
            Log.d("", "Endpoints:" + epc + "\n");

            Log.d("", "Permission:" + Boolean.toString(usbManager.hasPermission(device)) + "\n");

            UsbDeviceConnection connection = usbManager.openDevice(device);
            if (null == connection) {
                Log.d("", "(unable to establish connection)\n");
            } else {

                // Claims exclusive access to a UsbInterface.
                // This must be done before sending or receiving data on
                // any UsbEndpoints belonging to the interface.
                connection.claimInterface(intf, true);

                // getRawDescriptors can be used to access descriptors
                // not supported directly via the higher level APIs,
                // like getting the manufacturer and product names.
                // because it returns bytes, you can get a variety of
                // different data types.
                byte[] rawDescs = connection.getRawDescriptors();
                String manufacturer = "", product = "";


                byte[] buffer = new byte[255];
                int idxMan = rawDescs[14];
                int idxPrd = rawDescs[15];

                int rdo = connection.controlTransfer(UsbConstants.USB_DIR_IN
                                | UsbConstants.USB_TYPE_STANDARD, STD_USB_REQUEST_GET_DESCRIPTOR,
                        (LIBUSB_DT_STRING << 8) | idxMan, 0, buffer, 0xFF, 0);
                manufacturer = new String(buffer, 2, rdo - 2, StandardCharsets.UTF_16LE);

                rdo = connection.controlTransfer(UsbConstants.USB_DIR_IN
                                | UsbConstants.USB_TYPE_STANDARD, STD_USB_REQUEST_GET_DESCRIPTOR,
                        (LIBUSB_DT_STRING << 8) | idxPrd, 0, buffer, 0xFF, 0);
                product = new String(buffer, 2, rdo - 2, StandardCharsets.UTF_16LE);

/*                int rdo = connection.controlTransfer(UsbConstants.USB_DIR_IN
                        | UsbConstants.USB_TYPE_STANDARD, STD_USB_REQUEST_GET_DESCRIPTOR,
                        (LIBUSB_DT_STRING << 8) | idxMan, 0x0409, buffer, 0xFF, 0);*/


                Log.d("", "Manufacturer:" + manufacturer + "\n");
                Log.d("", "Product:" + product + "\n");
                Log.d("", "Serial#:" + connection.getSerial() + "\n");
            }

        }
        return strusbName;
    }


    //判断是否有测试项目
    private static void isHasTest() {
        hasTest = false;
        int length = bl.length;
        for (int i = 0; i < length; i++) {
            if (bl[i]) {
                hasTest = true;
            }
        }
    }


    //监听屏幕点击事件
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //有按下动作时取消定时
                hideBottomUIMenu();
                break;
            case MotionEvent.ACTION_UP:
                //抬起时启动定时
                hideBottomUIMenu();
                break;
        }

        return super.dispatchTouchEvent(ev);
    }


    //隐藏导航栏
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }


//    int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;

    /*
  01xx：WIFI
  02xx：USB
  03xx：SD
  04xx：本地蓝牙
  05xx：本地音乐
  06xx：在线音乐
  07xx：有声内容
  08xx:BlueTooth
   */

    //    private static Handler handlerGo = new Handler() {
    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        private MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case 001:
                        netNum = whichNet();

                        keyPrint("正在检测WIFI模块:", "");

                        myPrint(order++ + "——", "检测网络状态...：" + TAB + getDateEN());

//
                        break;

                    case 002:
                        keyPrint(TAB, "当前无网络");
//
                        NETWORKSTATE = false;

                        break;

                    case 003:
//
                        keyPrint("WIFI检测正常", "");

                        myPrint(TAB, "当前已连wifi");

//
                        Log.e("当前已连wifi", "handler003");
                        NETWORKSTATE = true;

                        break;

                    case 004:
//
                        myPrint(TAB, "当前已连有线");

//

                        NETWORKSTATE = true;

                        break;


                    //WIFI
                    case 100:

                        myPrint(TAB, "Wifi开启失败：");
                        keyPrint("", "Wifi开启失败");

//

                        break;

                    case 101:
//
                        Log.e("!!!!!!!!", String.valueOf(wifiManager.isWifiEnabled()));
                        myPrint(TAB, "检测Wifi是否开启...：" + wifiManager.isWifiEnabled());
//
                        Log.e("检测Wifi是否开启", "handler101");

                        break;

                    case 102:
//
                        myPrint(TAB, "Wifi未开启，正在开启Wifi");
//
                        wifiManager.setWifiEnabled(true);

                        break;

                    case 103:
//

                        myPrint(TAB, "Wifi已开启，正在检测是否连接Wifi");
                        keyPrint("测试通过", "");


                        break;

                    case 104:
//

                        if (isWifiConnect()) {


                            myPrint(TAB, "已连接Wifi:" + wifiManager.getConnectionInfo().getBSSID());
//
                            NETWORKSTATE = true;

                        } else {
                            myPrint(TAB, "未连接WIFI");
//
                        }

                        break;
                    //usb

                    case 201:

//                    detectInputDeviceWithShell();

                        detectUsbDeviceWithUsbManager();
//getSPath();
//                    detectUsbDeviceWithInputManager();


                        keyPrint("正在检测USB：", "");

                        myPrint(order++ + "——", "正在检测USB" + TAB + getDateEN());
                        myPrint(TAB, "正在检测外部存储设备\t" + "数量：" + sdNum());
//

                        if (sdNum() == 2 || usbReceiver.getUSBFLAG() || USBFLAG) {

                            myPrint(TAB, "USB：检测到USB");
                            keyPrint("检测到USB", "");


//
                            Log.e("检测到USB", "handler201");
//
                        } else {

                            myPrint(TAB, "USB：没有检测到USB");
                            keyPrint("", "没有检测到USB");

//
                        }

                        break;

                    //sd
                    case 301:
//                    detectSd();

                        keyPrint("正在检测SD卡：", "");

                        myPrint(order++ + "——", "正在检测SD卡\t" + getDateEN());

//

                        if (sdNum() == 2 || ((sdNum() == 1) && (!USBFLAG))) {

                            myPrint(TAB, "SD：检测到SD卡");
                            keyPrint("检测到SD卡", "");

//
                        } else {
                            myPrint(TAB, "SD：未检测到SD卡");
                            keyPrint("", "未检测到SD卡");

//
                        }
                        rv_log.scrollToPosition(rAdapter.getItemCount() - 1);
                        break;

                    //系统蓝牙
                    case 401:

                        keyPrint("正在检测蓝牙模块：", "");
                        myPrint(order++ + "——" + "蓝牙：", "正在检测蓝牙模块\t" + getDateEN());

//
                        if (blueadapter == null) {

                            myPrint(TAB + "蓝牙：", "未检测到蓝牙模块");
                            keyPrint("", "未检测到蓝牙模块");
//
                        } else {

                            Log.e("蓝牙状态", String.valueOf(blueadapter.getState()));

                            myPrint(TAB, "检测蓝牙是否开启。。。");
//
                        }

                        rv_log.scrollToPosition(rAdapter.getItemCount() - 1);


                        break;


                    case 402:

                        myPrint(TAB, "蓝牙已开启");
                        keyPrint("蓝牙模块正常", "");
//
                        rv_log.scrollToPosition(rAdapter.getItemCount() - 1);
                        break;


                    case 403:

                        myPrint(TAB, "蓝牙未开启，正在开启蓝牙");

//
                        rv_log.scrollToPosition(rAdapter.getItemCount() - 1);

                        break;


                    case 404:
                        myPrint(TAB, "蓝牙开启成功");
                        keyPrint("蓝牙模块正常", "");

//
                        rv_log.scrollToPosition(rAdapter.getItemCount() - 1);
                        break;

                    case 405:

                        myPrint(TAB, "蓝牙开启失败");
                        keyPrint("", "蓝牙开启失败");

//
                        rv_log.scrollToPosition(rAdapter.getItemCount() - 1);
                        break;


                    case 406:

                        if (blueToothReceiver.getBLUETFLAG()) {

                            myPrint(TAB + "蓝牙：", "已连接");

//
                        } else {

                            myPrint(TAB + "蓝牙：", "未连接");

//
                            Log.e("蓝牙", "未连接");
                        }


                        Log.e("蓝牙是否连接", String.valueOf(blueToothReceiver.getBLUETFLAG()));
                        rv_log.scrollToPosition(rAdapter.getItemCount() - 1);
                        break;

//本地音乐
                    case 501:

                        try {
                            myPrint(order++ + "——", "本地音乐播放器接口测试" + TAB + getDateEN());
                            keyPrint("本地音乐播放器接口测试：", "");

//
                            if (iPlayerProvider.getRoot() == 1) {

                                myPrint(TAB, "当前路径在本地目录");
//
                            } else if (iPlayerProvider.getRoot() == 2) {

                                myPrint(TAB, "当前路径在SD目录");
                                myPrint(TAB, "打开本地列表");
//
                                iPlayerProvider.setRoot(1);
                            } else if (iPlayerProvider.getRoot() == 3) {

                                myPrint(TAB, "当前路径在U盘目录");
                                myPrint(TAB, "打开本地列表");
//
                                iPlayerProvider.setRoot(1);
                            }


                            int root = iPlayerProvider.getRoot();
                            if (root == 1) {

                                myPrint(TAB + "当前路径：", "本地列表");

//
                            } else if (root == 2) {
                                myPrint(TAB + "当前路径：", "SD卡");
//
                                LocalMusicFlag = false;
                            } else if (root == 3) {
                                myPrint(TAB + "当前路径：", "U盘");
//
                                LocalMusicFlag = false;
                            } else {
                                myPrint(TAB + "当前路径：", "i don't know");
//

                                LocalMusicFlag = false;
                            }

                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        rv_log.scrollToPosition(rAdapter.getItemCount() - 1);
                        break;

                    case 502:
                        try {

                            if (iPlayerProvider.getMusics().isEmpty()) {
                                myPrint(TAB + "该目录无可播放歌曲", "");
//

                            } else {
                                myPrint(TAB, "getPlayerState:" + iPlayerProvider.getPlayerState());
//

                                int playState = iPlayerProvider.getPlayerState();
                                if (playState == 2) {
                                    iPlayerProvider.play();

                                    myPrint(TAB, "getPlayerState:" + iPlayerProvider.getPlayerState());

                                    myPrint(TAB + "当前歌曲:", "" + iPlayerProvider.getPlayingName());

                                    myPrint(TAB + "当前歌曲时常:", "getDuration:" + iPlayerProvider.getDuration());

                                    myPrint(TAB + "当前歌曲播放位置:", "getCurrentPlayPosition:" + iPlayerProvider.getCurrentPlayPosition());

                                }

                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        rv_log.scrollToPosition(rAdapter.getItemCount() - 1);
                        break;

                    case 503:
                        myPrint(TAB, "当前为开机自动播放");

//
                        try {
                            myPrint(TAB, "关闭开机自动播放");
//
                            iPlayerProvider.setBootPlay(false);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        rv_log.scrollToPosition(rAdapter.getItemCount() - 1);

                        break;

                    case 504:

                        try {
                            if (iPlayerProvider.getBootPlay()) {
                                myPrint(TAB, "关闭开机自动播放失败");
//
                                LocalMusicFlag = false;
                            } else {
                                iPlayerProvider.setBootPlay(true);
                                myPrint(TAB, "设置开机自动播放");
//
                                if (iPlayerProvider.getBootPlay()) {
                                    myPrint(TAB, "设置开机自动播放成功");

//

                                } else {
                                    myPrint(TAB, "设置开机自动播放失败");
//
                                    LocalMusicFlag = false;
                                }
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                        if (LocalMusicFlag) {
                            keyPrint("测试通过", "");
//
                        } else {
                            keyPrint("", "测试未通过");
//
                        }

                        rv_log.scrollToPosition(rAdapter.getItemCount() - 1);
                        break;

//在线音乐

                    case 600:

                        myPrint(order++ + "——", "在线音乐接口测试\t" + getDateEN());
                        keyPrint("在线音乐接口测试：", "");
                        myPrint(TAB, "当前未联网，无法使用在线音乐应用");
                        keyPrint("", "无网络无法测试");

//
                        rv_log.scrollToPosition(rAdapter.getItemCount() - 1);
                        break;

                    case 601:
                        myPrint(order++ + "——", "在线音乐接口测试\t" + getDateEN());
                        keyPrint("在线音乐接口测试：", "");
//
                        try {

                            myPrint(TAB, "暂停");

//
                            iOnlineMusicAidl.pause();
                            myPrint(TAB + "-after pause:isPlaying:", "" + iOnlineMusicAidl.isPlaying());

//
                            int random = new Random().nextInt(60);
                            if (random < 10) {


                                myPrint(TAB, "搜索歌曲：温柔");
//
                                iOnlineMusicAidl.searchOnLineMusic("温柔");
                            } else if (random < 20) {
                                myPrint(TAB, "搜索歌曲：Paradise coldplay");

//
                                iOnlineMusicAidl.searchOnLineMusic("paradise coldplay");
                            } else if (random < 30) {
                                myPrint(TAB, "搜索歌曲：世界末日 周杰伦");

//
                                iOnlineMusicAidl.searchOnLineMusic("世界末日 周杰伦");
                            } else if (random < 40) {
                                myPrint(TAB, "搜索歌曲：岁月如歌");
//
                                iOnlineMusicAidl.searchOnLineMusic("岁月如歌");
                            } else if (random < 50) {
                                myPrint(TAB, "搜索歌曲：Yellow coldplay");
//
                                iOnlineMusicAidl.searchOnLineMusic("yellow coldplay");
                            } else {
                                myPrint(TAB, "搜索歌曲：夜空中最亮的星");
//
                                iOnlineMusicAidl.searchOnLineMusic("夜空中最亮的星");
                            }

                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                        myPrint(TAB, "OnlineMusicFlag(601):" + OnlineMusicFlag);
//
                        break;

                    case 602:


//

                        if (musicList.isEmpty()) {
                            myPrint(TAB, "musiclist为空,未从搜索回调中获取到音乐");
//
                            Log.e("", "musiclist为空");
                            OnlineMusicFlag = false;

                        } else {
                            Log.e("", "musiclist不为空");
                            myPrint("——", "musiclist不为空");
                            myPrint(TAB + "musiclist大小：" + String.valueOf(musicList.size()), "");
                            myPrint(TAB, "musiclist:");
//
                            for (int i = 0; i < musicList.size(); i++) {
//
                                myPrint("\t", String.valueOf(musicList.get(i)));
//
                            }
                            try {
                                iOnlineMusicAidl.playSearchMusic(musicList.get(0), true);
                                if (!iOnlineMusicAidl.isPlaying()) {
                                    iOnlineMusicAidl.play();
                                }
                                myPrint("\t", String.valueOf(iOnlineMusicAidl.isPlaying()));
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
//
                        }

                        myPrint(TAB, "OnlineMusicFlag(602):" + OnlineMusicFlag);
//                    tl_List.add(new TestLog(TAB, "OnlineMusicFlag(602):"+OnlineMusicFlag));
//                    rAdapter.notifyItemChanged(tl_List.size() - 1);

                        break;

                    case 603:
                        try {


//                        tl_List.add(new TestLog(TAB, "是否在播放" + String.valueOf(iOnlineMusicAidl.isPlaying())));
//                        rAdapter.notifyItemChanged(tl_List.size() - 1);


                            myPrint(TAB, "当前播放总时长" + iOnlineMusicAidl.getDurationTime());
                            myPrint(TAB, "搜索播放的音乐:" + musicList.get(0).getTitle());
                            myPrint(TAB, "当前播放音乐:" + iOnlineMusicAidl.getCurrentMusic().getTitle());
                            if (!musicList.get(0).getTitle().equals(iOnlineMusicAidl.getCurrentMusic().getTitle())) {

                                myPrint(TAB, "当前播放音乐和搜索内容不符");
                                OnlineMusicFlag = false;
                            }
//                            tl_List.add(new TestLog(TAB, "当前播放总时长" + iOnlineMusicAidl.getDurationTime()));


                            iOnlineMusicAidl.seekTo((int) (iOnlineMusicAidl.getDurationTime() / 2));
                            myPrint(TAB, "播放进度设置为总时长一半");

//                        rAdapter.notifyItemChanged(tl_List.size() - 1);

                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        myPrint(TAB, "OnlineMusicFlag(602):" + OnlineMusicFlag);
//
                        myPrint(TAB, "OnlineMusicFlag(602):" + OnlineMusicFlag);
//
                        if (OnlineMusicFlag) {

                            keyPrint("测试通过", "");
//
                        } else {
                            keyPrint("", "测试未通过");
//
                        }
                        rv_log.scrollToPosition(rAdapter.getItemCount() - 1);

                        break;
//有声内容

                    case 700:

                        myPrint(order++ + "——", "有声内容接口测试\t" + getDateEN());
                        keyPrint("有声内容接口测试：", "");
                        myPrint(TAB, "当前未联网，无法使用有声内容应用");
                        keyPrint("", "无网络无法测试");
//
                        rv_log.scrollToPosition(rAdapter.getItemCount() - 1);
                        break;

                    case 701:
                        myPrint(order++ + "——", "有声内容接口测试\t" + getDateEN());
                        keyPrint("有声内容接口测试：", "");
//

                        Log.e("1111111111111111111111", "1111111111111");

                        try {

                            int random = new Random().nextInt(60);
                            if (random < 10) {

                                myPrint("——", "搜索：看天下");
//
                                albumList.addAll(radioAidl.searchAudioContentAlbum("看天下"));
                            } else if (random < 20) {
                                myPrint("——", "搜索：读者");
//
                                albumList.addAll(radioAidl.searchAudioContentAlbum("读者"));
                            } else if (random < 30) {
                                myPrint("——", "搜索：逻辑思维");
//
                                albumList.addAll(radioAidl.searchAudioContentAlbum("逻辑思维"));
                            } else if (random < 40) {
                                myPrint("——", "搜索：古典音乐");
//
                                albumList.addAll(radioAidl.searchAudioContentAlbum("古典音乐"));
                            } else if (random < 50) {
                                myPrint("——", "搜索：钢琴曲");
//
                                albumList.addAll(radioAidl.searchAudioContentAlbum("钢琴曲"));
                            } else {
                                myPrint("——", "搜索：岳云鹏相声");
//
                                albumList.addAll(radioAidl.searchAudioContentAlbum("岳云鹏相声"));
                            }

//
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
//

                        if (albumList.isEmpty()) {
                            myPrint(TAB, "搜索列表为空");
//

                        } else {
                            myPrint(TAB, "搜索列表：");
//
                            for (Album a : albumList) {

                                myPrint(TAB, a.getAlbumTitle());
//
                            }

//
                        }

                        if (albumList2.isEmpty()) {
                            myPrint(TAB, "回调搜索列表为空");
                        } else {
                            myPrint(TAB, "回调的搜索列表不为空");
                        }

                        rv_log.scrollToPosition(rAdapter.getItemCount() - 1);
                        break;

                    case 702:
                        try {
                            if (albumList.size() > 1) {

                                album = albumList.get(0);

                                Log.e("album.getAlbumIntro", album.getAlbumIntro() + "/" + album.getAlbumTitle());

                                radioAidl.playAudioContentVoice(album);

                            } else {
                                RadioContentFlag = false;
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                        break;

                    case 703:


                        try {
                            myPrint(TAB + "-after playAudioContentVoice:getCurrentAlbumId:", radioAidl.getCurrentAlbumId());
                            if (radioAidl.getCurrentAlbumId() == null) {
                                RadioContentFlag = false;
                            } else {
//
                                myPrint(TAB + "-after playAudioContentVoice:getCurrentTrackIndex:", String.valueOf(radioAidl.getCurrentTrackIndex()));
                            }
//
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        if (RadioContentFlag) {

                            keyPrint("测试通过", "");

//
                        } else {
                            keyPrint("", "测试未通过");
                        }


                        rv_log.scrollToPosition(rAdapter.getItemCount() - 1);
                        break;

                    //本地蓝牙

                    case 800:

                        myPrint("——", "本地蓝牙：通过AIDL接口打开蓝牙失败");
                        keyPrint("", "测试未通过");
//
                        rv_log.scrollToPosition(rAdapter.getItemCount() - 1);
                        break;

                    case 801:
                        myPrint(order++ + "——", "本地蓝牙接口测试\t" + getDateEN());
                        keyPrint("本地蓝牙接口测试：", "");
                        myPrint(TAB, "检测本地蓝牙是否开启");
//

                        break;

                    case 802:
                        myPrint(TAB, "本地蓝牙：蓝牙已打开");
//
                        BTLOCALFLAG = true;
                        rv_log.scrollToPosition(rAdapter.getItemCount() - 1);
                        break;

                    case 803:
                        try {
                            myPrint(TAB, "本地蓝牙：蓝牙未打开");
                            myPrint(TAB, "本地蓝牙：通过AIDL接口打开蓝牙");
//
                            iBluetoothService.openBluetooth();

                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        rv_log.scrollToPosition(rAdapter.getItemCount() - 1);
                        break;

                    case 804:

                        myPrint(TAB, "本地蓝牙：蓝牙已打开");
                        keyPrint("测试通过：", "");
//
                        rv_log.scrollToPosition(rAdapter.getItemCount() - 1);

                        break;

                    case 805:
                        keyPrint("测试通过", "");

//
                        break;

                    case 900:

                        myPrint(TAB, "本次检测结束！");
//
                        rv_log.scrollToPosition(rAdapter.getItemCount() - 1);

                        keyPrint("", "本次检测结束！");
//

                        Toast.makeText(MainActivity.context, "本次检测结束！", Toast.LENGTH_SHORT).show();

//                    getWindow().getDecorView().invalidate();
//
//                    FileUtils.saveFile(screenShotWholeScreen(), "testPic.jpeg", MainActivity.this);

                        if (!tl_List.isEmpty()) {


//                        Log.e("保存日志文件到", "//sdcard//UTest//testlog.txt");
//    saveFile(tl_List);

//                        FileUtils.saveFile(tl_List);
                        }
                        initFlag();
                        pBar.setVisibility(View.GONE);
                        bt_go.setText("开始测试");
                        isThread = false;

                        break;

                    default:
                        break;
                }

            }
        }
    }

    private final MyHandler handlerGo = new MyHandler(this);

    private static void myPrint(String tag, String content) {
        tl_List.add(new TestLog(tag, content));
        rAdapter.notifyItemChanged(tl_List.size() - 1);
    }


    private static void keyPrint(String tag, String content) {
        sc_list.add(new ShowContent(tag, content));
        slAdapter.notifyItemChanged(sc_list.size() - 1);
    }
/*
获取英文格式时间戳
 */

    public static String getDateEN() {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date1 = format1.format(new Date(System.currentTimeMillis()));
        return date1;//example： 2018-10-03 23:41:31
    }

    /*
    保存文件到手机根目录
     */
    public void saveFile(List<TestLog> logList) {

        File file0 = Environment.getExternalStoragePublicDirectory("testlog.txt");

        String filePath = "/sdcard/UTest/";
        File file1 = new File(filePath);
        file1.mkdir();

        File file = new File(filePath + "testLog.txt");


        if (file.exists()) {

            file.delete();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                for (TestLog testLog : logList) {
                    fos.write(testLog.getID().getBytes("UTF-8"));
                    fos.write(testLog.getLogmsg().getBytes("UTF-8"));
                    fos.write("\r\n".getBytes());
                }
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

        }

    }
    /*
    执行选中的测试项
     */

    private void testAll() {
        isThread = true;
        if (bl[1]) {
            testWIFI();
        }
        if (bl[2]) {
            testUsb();
        }
        if (bl[3]) {
            testSd();
        }
        if (bl[4]) {
            testBlueTooth();
        }
        if (bl[5]) {
            testLocalM();
        }
        if (bl[6]) {
            testOnlineM();
        }
        if (bl[7]) {
            testSoundC();
        }
        if (bl[8]) {

            testLocalBluetooth();
        }

        executor.submit(fileSave);

    }

    private void testWIFI() {
        executor.submit(tdNet1);
        executor.submit(tdNet2);
        executor.submit(tdWifi1);
        executor.submit(tdWifi2);
        executor.submit(tdWifi3);

    }

    private void testUsb() {
        executor.submit(tdUsb1);
    }

    private void testSd() {
        executor.submit(tdSd1);
    }

    private void testBlueTooth() {
        executor.submit(tdBTmethod1);
        executor.submit(tdBTmethod2);
        executor.submit(tdBTmethod3);
        executor.submit(tdBTmethod4);
        executor.submit(tdBTmethod5);
    }


    private void testLocalM() {
        executor.submit(tdLM1);
        executor.submit(tdLM2);
        executor.submit(tdLM3);
    }

    private void testOnlineM() {

        executor.submit(tdOM0);

        executor.submit(tdOM1);
        executor.submit(sleepThread);
        executor.submit(sleepThread);
        executor.submit(tdOM2);
        executor.submit(tdOM3);
    }


    private void testSoundC() {

        executor.submit(tdSC0);

        executor.submit(tdSC1);
        executor.submit(sleepThread);
        executor.submit(tdSC2);
        executor.submit(tdSC3);
    }


    private void testLocalBluetooth() {
        executor.submit(tdBT0);
        executor.submit(tdBT1);
        executor.submit(tdBT2);
    }


    /*
    延时线程
     */

    Thread sleepThread = new Thread(new Runnable() {
        @Override
        public void run() {
            if (NETWORKSTATE == false) {
            } else {
                try {
                    Thread.sleep(MILLIS * 2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });


    /*
    开辟测试线程
     */


    //网络测试线程
    Thread tdNet1 = new Thread(new Runnable() {
        @Override
        public void run() {

            handlerGo.sendEmptyMessage(001);

            Log.e("tdNet1", "tdNet1");
            try {
                Thread.sleep(MILLIS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });


    Thread tdNet2 = new Thread(new Runnable() {
        @Override
        public void run() {
            if (netNum == 0) {
                handlerGo.sendEmptyMessage(002);

            } else if (netNum == 1) {
                handlerGo.sendEmptyMessage(003);

            } else if (netNum == 10) {
                handlerGo.sendEmptyMessage(004);

            }
            Log.e("tdNet2", "tdNet2");
            try {
                Thread.sleep(MILLIS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });


    //WIFI测试线程
    Thread tdWifi1 = new Thread(new Runnable() {

        @Override
        public void run() {
            if (netNum == 1 || netNum == 10) {

            } else {

                if (wifiManager.isWifiEnabled()) {
                    handlerGo.sendEmptyMessage(101);

                } else {
                    handlerGo.sendEmptyMessage(102);

                }

            }

            try {
                Thread.sleep(MILLIS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });


    Thread tdWifi2 = new Thread(new Runnable() {
        @Override
        public void run() {
            if (netNum == 1 || netNum == 10) {

            } else {
                try {

                    for (int i = 0; i < 3; i++) {
                        Log.e("正在开启wifi：", "第" + (i + 1) + "次");
                        Thread.sleep(MILLIS);

                        if (wifiManager.isWifiEnabled()) {

                            handlerGo.sendEmptyMessage(103);
                            break;
                        }
                    }

                    if (!(wifiManager.isWifiEnabled())) {
                        Log.e("Wifi打开失败", ",and i don't know why");
                        handlerGo.sendEmptyMessage(100);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            Log.e("tdWifi2", "tdWifi2");
        }
    });

    Thread tdWifi3 = new Thread(new Runnable() {
        @Override
        public void run() {
            if (netNum == 1 || netNum == 10) {

            } else {
                try {
                    Thread.sleep(MILLIS);

                    handlerGo.sendEmptyMessage(104);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            Log.e("tdWifi3", "tdWifi3");
        }
    });


    //USB测试线程
    Thread tdUsb1 = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(MILLIS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            handlerGo.sendEmptyMessage(201);

            Log.e("tdUsb1", "tdUsb1");
        }
    });

    //SD卡测试线程

    Thread tdSd1 = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(MILLIS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            handlerGo.sendEmptyMessage(301);
        }
    });


    //检测系统蓝牙

    Thread tdBTmethod1 = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(MILLIS);
                handlerGo.sendEmptyMessage(401);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    });

    Thread tdBTmethod2 = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(MILLIS);
                if (blueadapter.isEnabled()) {
                    handlerGo.sendEmptyMessage(402);
                } else {
                    handlerGo.sendEmptyMessage(403);
                    blueadapter.enable();
                    BTSYSFLAG = true;
                    Thread.sleep(MILLIS);

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });

    Thread tdBTmethod3 = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(MILLIS);
                if (blueadapter.isEnabled() && BTSYSFLAG) {
                    handlerGo.sendEmptyMessage(404);
                } else if (blueadapter.isEnabled() == false) {
                    handlerGo.sendEmptyMessage(405);

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });


    Thread tdBTmethod4 = new Thread(new Runnable() {
        @Override
        public void run() {
            if (blueadapter.isEnabled()) {
                try {
                    Thread.sleep(MILLIS * 2);
                    handlerGo.sendEmptyMessage(406);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    Thread tdBTmethod5 = new Thread(new Runnable() {
        @Override
        public void run() {
            if (blueadapter.isEnabled()) {
                try {

                    Thread.sleep(MILLIS);

                    handlerGo.sendEmptyMessage(407);

                } catch (InterruptedException e) {

                    e.printStackTrace();

                }

            }
        }
    });


    //本地音乐测试线程

    Thread tdLM1 = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                startLocalService();
                Thread.sleep(MILLIS);

                handlerGo.sendEmptyMessage(501);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });

    Thread tdLM2 = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(MILLIS);

                handlerGo.sendEmptyMessage(502);
                Thread.sleep(MILLIS * 2);

                iPlayerProvider.pause();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
    });

    Thread tdLM3 = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(MILLIS);
                if (iPlayerProvider.getBootPlay()) {
                    handlerGo.sendEmptyMessage(503);

                }
                handlerGo.sendEmptyMessage(504);

                Thread.sleep(MILLIS * 2);

                iPlayerProvider.unRegisterMusicStateListener(iPlayMusicStateListener);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
    });


    //在线音乐测试线程

    Thread tdOM0 = new Thread(new Runnable() {
        @Override
        public void run() {
            if (ping() == false) {
                handlerGo.sendEmptyMessage(600);
                NETWORKSTATE = false;
            }
        }
    });


    Thread tdOM1 = new Thread(new Runnable() {
        @Override
        public void run() {
            if (NETWORKSTATE == false) {
            } else {
                try {
                    Thread.sleep(MILLIS);
                    handlerGo.sendEmptyMessage(601);
                    Thread.sleep(MILLIS * 2);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    });

    Thread tdOM2 = new Thread(new Runnable() {
        @Override
        public void run() {
            if (NETWORKSTATE == false) {
            } else {
                try {

                    Thread.currentThread().sleep(MILLIS * 2);
                    handlerGo.sendEmptyMessage(602);


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }
    });
    Thread tdOM3 = new Thread(new Runnable() {
        @Override
        public void run() {
            if (NETWORKSTATE == false) {
            } else {
                try {

                    Thread.currentThread().sleep(MILLIS * 2);
                    handlerGo.sendEmptyMessage(603);


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }
    });


    //有声内容测试线程


    Thread tdSC0 = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(MILLIS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (NETWORKSTATE == false) {
                handlerGo.sendEmptyMessage(700);
            }
        }

    });


    Thread tdSC1 = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(MILLIS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (NETWORKSTATE == false) {
            } else {
                try {

                    handlerGo.sendEmptyMessage(701);
                    Thread.sleep(MILLIS);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }
    });

    Thread tdSC2 = new Thread(new Runnable() {
        @Override
        public void run() {

            if (NETWORKSTATE == false) {
            } else {
                try {
                    Thread.sleep(MILLIS);
                    handlerGo.sendEmptyMessage(702);
                    Thread.sleep(MILLIS);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }
    });
    Thread tdSC3 = new Thread(new Runnable() {
        @Override
        public void run() {

            if (NETWORKSTATE == false) {
            } else {
                try {
                    Thread.sleep(MILLIS);
                    handlerGo.sendEmptyMessage(703);
                    Thread.sleep(MILLIS);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }
    });


    //本地蓝牙测试线程

    Thread tdBT0 = new Thread(new Runnable() {
        @Override
        public void run() {
            handlerGo.sendEmptyMessage(801);

        }
    });

    Thread tdBT1 = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(MILLIS);
                Log.e("tdBT1:", "my turn!!!!!!");
                if (iBluetoothService.getOpenState()) {
                    handlerGo.sendEmptyMessage(802);

                } else {
                    handlerGo.sendEmptyMessage(803);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    });

    Thread tdBT2 = new Thread(new Runnable() {
        @Override
        public void run() {
            try {

                Log.e("tdBT2:", "my turn!!!!!!");
                Thread.currentThread().sleep(MILLIS * 3);
                if (iBluetoothService.getOpenState() && BTLOCALFLAG == false) {
                    handlerGo.sendEmptyMessage(804);
                } else if (iBluetoothService.getOpenState() && BTLOCALFLAG == true) {

                    handlerGo.sendEmptyMessage(805);
                } else {
                    handlerGo.sendEmptyMessage(800);
                    Log.e("蓝牙响应超时", ",and i don't know why");

                }

            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });


    Thread fileSave = new Thread(new Runnable() {
        @Override
        public void run() {
            handlerGo.sendEmptyMessage(900);

        }
    });


//检测USB设备


    private void detectInputDeviceWithShell() {
        try {
            //获得外接USB输入设备的信息
            Process p = Runtime.getRuntime().exec("cat /proc/bus/input/devices");
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                String deviceInfo = line.trim();
                //对获取的每行的设备信息进行过滤，获得自己想要的。
//                if (deviceInfo.contains("Name="))
                Log.d(TAG, "detectInputDeviceWithShell: " + deviceInfo);
                tl_List.add(new TestLog(order++ + "——", "已检测到的USB设备：" + deviceInfo));
                rAdapter.notifyItemChanged(tl_List.size() - 1, "a");
            }
            Log.d(TAG, "-----------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void detectUsbDeviceWithInputManager() {
        InputManager im = (InputManager) getSystemService(INPUT_SERVICE);
        int[] devices = im.getInputDeviceIds();
        tl_List.add(new TestLog(order++ + "——", "检测是否有USB设备...：" + !(devices == null)));
        rAdapter.notifyItemChanged(tl_List.size() - 1, "a");
        if (devices == null) {
            Log.e("USB检测:", "未检测到设备");
        } else {
            Log.e("USB检测:", "检测到设备");
        }
        for (int id : devices) {
            InputDevice device = im.getInputDevice(id);
            Log.e("!!!", "detectUsbDeviceWithInputManager: " + device.getName());
            tl_List.add(new TestLog(order++ + "——", "已检测到的USB设备：" + device.getName()));
            rAdapter.notifyItemChanged(tl_List.size() - 1, "a");
        }
    }


    private static void detectUsbDeviceWithUsbManager() {
        HashMap<String, UsbDevice> deviceHashMap = ((UsbManager) MainActivity.context.getSystemService(USB_SERVICE)).getDeviceList();

        for (Map.Entry entry : deviceHashMap.entrySet()) {
            Log.d(TAG, "detectUsbDeviceWithUsbManager: " + entry.getKey() + ", " + entry.getValue());
            String s = (String) entry.getKey();

            if (s.startsWith("/dev/bus/usb/001")) {
                continue;
            }
            USBFLAG = true;

//            tl_List.add(new TestLog(order++ + "——", "已检测到的USB设备：" +entry.getKey() + ", " + entry.getValue()));
//            rAdapter.notifyItemChanged(tl_List.size() - 1);
        }
    }


    //检测SD卡
    private void detectSd() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.e("!!!", "检测到SD卡");
            tl_List.add(new TestLog(order++ + "——", "检测到SD卡"));
            rAdapter.notifyItemChanged(tl_List.size() - 1, "a");
        } else {
            Log.e("!!!", "未检测到SD卡 ");
            tl_List.add(new TestLog(order++ + "——", "未检测到SD卡"));
            rAdapter.notifyItemChanged(tl_List.size() - 1, "a");
        }
    }

    //初始化RecycleView内容和dialoglist内容
    private void initList() {

        TestLog tlog1 = new TestLog("", getResources().getString(R.string.testcontent));
        tl_List.add(tlog1);

        sc_list.add(new ShowContent("", ""));

        int length = mlistText.length;
        for (int i = 0; i < length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("text", mlistText[i]);
            mData.add(item);
        }

        hideBottomUIMenu();
    }

    private void initShowContents() {

        showContentList.add(new ShowContent("init it", "showcontent"));

    }

    public boolean fileIsExists() {
        try {
            File f = new File("/sdcard/UTest/");
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
// TODO: handle exception
            return false;
        }
        return true;
    }

    /*
    获取权限
     */
    private void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            for (int i = 0; i < permissions.length; i++) {

                if (permissions[i] == "android.permission.MOUNT_UNMOUNT_FILESYSTEMS") {

                    Log.e("fileIsExists:", String.valueOf(fileIsExists()));

                    if (fileIsExists()) {

                        continue;
                    }
                }

                int j = ContextCompat.checkSelfPermission(this, permissions[i]);


                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                if (j != PackageManager.PERMISSION_GRANTED) {
                    // 如果没有授予该权限，就去提示用户请求
                    showDialogTipUserRequestPermission(i);
                }
            }
        }
        hideBottomUIMenu();
    }

    /*
          权限申请
           */
    private void showDialogTipUserRequestPermission(int i) {
        String str_1 = "";
        String str_2 = "";
        switch (i) {
            case 0:
                str_1 = "获粗略取位置权限不可用";
                str_2 = "由于需要获粗略取位置信息；\n否则，您将无法正常使用";
                break;
            case 1:
                str_1 = "获取精准位置权限不可用";
                str_2 = "由于需要精准位置信息；\n否则，您将无法正常使用";
                break;
            case 2:
                str_1 = "在SDCard中创建与删除文件权限不可用";
                str_2 = "在SDCard中创建与删除文件权限；\n否则，您将无法正常使用";
                break;

            case 3:
                str_1 = "往SDCard读出数据权限不可用";
                str_2 = "往SDCard读出数据权限；\n否则，您将无法正常使用";
                break;
            case 4:
                str_1 = "创建删除文件夹权限不可用";
                str_2 = "创建删除文件夹权限；\n否则，您将无法正常使用";
                break;
            default:
                break;
        }


        new AlertDialog.Builder(this)
                .setTitle(str_1)
                .setMessage(str_2)
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startRequestPermission();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();
    }


    // 开始提交请求权限
    private void startRequestPermission() {
        ActivityCompat.requestPermissions(this, permissions, 321);
    }

    // 用户权限 申请 的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean b = shouldShowRequestPermissionRationale(permissions[0]);
                    if (!b) {
                        // 用户还是想用我的 APP 的
                        // 提示用户去应用设置界面手动开启权限
                        showDialogTipUserGoToAppSetting();
                    } else
                        finish();
                } else {
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                    hideBottomUIMenu();
                }
            }
        }
    }

    // 提示用户去应用设置界面手动开启权限

    private void showDialogTipUserGoToAppSetting() {

        dialog = new AlertDialog.Builder(this)
                .setTitle("存储权限不可用")
                .setMessage("请在-应用设置-权限-中，允许使用存储权限来保存用户数据")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转到应用设置界面
                        goToAppSetting();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();


    }

    // 跳转到当前应用的设置界面
    private void goToAppSetting() {
        Intent intent = new Intent();

        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);

        startActivityForResult(intent, 123);
    }

    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 检查该权限是否已经获取
                int i = ContextCompat.checkSelfPermission(this, permissions[0]);
                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                if (i != PackageManager.PERMISSION_GRANTED) {
                    // 提示用户应该去应用设置界面手动开启权限
                    showDialogTipUserGoToAppSetting();
                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                    hideBottomUIMenu();
                }
            }
        }
    }


    private void secondWay() {
        if (wifiManager.isWifiEnabled()) {
            turnOffTheWifi();
        }
        turnOnTheWifi();
    }

    private void turnOnTheWifi() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tl_List.add(new TestLog(order++ + "——", "已关闭，开启Wifi"));
        wifiManager.setWifiEnabled(true);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void turnOffTheWifi() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tl_List.add(new TestLog(order++ + "——", "已开启，关闭Wifi"));
        rAdapter.notifyItemChanged(tl_List.size() - 1);
        wifiManager.setWifiEnabled(false);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    public static boolean isWifiConnect() {
        ConnectivityManager connManager = (ConnectivityManager) MainActivity.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifiInfo.isConnected();
    }


    protected int checkNetworkInfo() {
        ConnectivityManager con = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = con.getActiveNetworkInfo();
        if (networkinfo == null || !networkinfo.isAvailable()) {
            // 当前网络不可用
            return 1;
        }
        boolean wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting();
        if (wifi) {
            // 使用wifi上网
            return 2;
        }
        return 3;
    }


    /*
    是否联网以及连的是有线还是wifi
     */


    private static int whichNet() {


        int i = getAPNType(MainActivity.context.getApplicationContext());

        if (ping() && i == 0) {
            i = 10;
        }
        return i;
    }


    /**
     * 获取当前的网络状态 ：没有网络-0：WIFI网络1：4G网络-4：3G网络-3：2G网络-2
     * 自定义
     *
     * @param context
     * @return
     */
    public static int getAPNType(Context context) {
        //结果返回值
        int netType = 0;
        //获取手机所有连接管理对象
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取NetworkInfo对象
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        //NetworkInfo对象为空 则代表没有网络
        if (networkInfo == null) {
            return netType;
        }
        //否则 NetworkInfo对象不为空 则获取该networkInfo的类型
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_WIFI) {
            //WIFI
            netType = 1;
        } else if (nType == ConnectivityManager.TYPE_MOBILE) {
            int nSubType = networkInfo.getSubtype();
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //3G   联通的3G为UMTS或HSDPA 电信的3G为EVDO
            if (nSubType == TelephonyManager.NETWORK_TYPE_LTE
                    && !telephonyManager.isNetworkRoaming()) {
                netType = 4;
            } else if (nSubType == TelephonyManager.NETWORK_TYPE_UMTS
                    || nSubType == TelephonyManager.NETWORK_TYPE_HSDPA
                    || nSubType == TelephonyManager.NETWORK_TYPE_EVDO_0
                    && !telephonyManager.isNetworkRoaming()) {
                netType = 3;
                //2G 移动和联通的2G为GPRS或EGDE，电信的2G为CDMA
            } else if (nSubType == TelephonyManager.NETWORK_TYPE_GPRS
                    || nSubType == TelephonyManager.NETWORK_TYPE_EDGE
                    || nSubType == TelephonyManager.NETWORK_TYPE_CDMA
                    && !telephonyManager.isNetworkRoaming()) {
                netType = 2;
            } else {
                netType = 2;
            }
        }
        return netType;
    }


    /*
     * @category 判断是否有外网连接（普通方法不能判断外网的网络是否连接，比如连接上局域网）
     * @return
     */
    public static final boolean ping() {

        String result = null;
        try {
            String ip = "www.baidu.com";// ping 的地址，可以换成任何一种可靠的外网
            Process p = Runtime.getRuntime().exec("ping -c 1 -w 100 " + ip);// ping网址1次
            // 读取ping的内容，可以不加

//            InputStream input = p.getInputStream();
//            BufferedReader in = new BufferedReader(new InputStreamReader(input));
//            StringBuffer stringBuffer = new StringBuffer();
//            String content = "";
//            while ((content = in.readLine()) != null) {
//                stringBuffer.append(content);
//            }
//            Log.d("------ping-----", "result content : " + stringBuffer.toString());
            // ping的状态
            int status = p.waitFor();
            if (status == 0) {
                result = "success";
                return true;
            } else {
                result = "failed";
            }
        } catch (IOException e) {
            result = "IOException";
        } catch (InterruptedException e) {
            result = "InterruptedException";
        } finally {
            Log.d("----result---", "result = " + result);
        }
        return false;
    }


    /*
    连接蓝牙AIDL
     */

    private ServiceConnection bluetoothConnect = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            // 从连接中获取Stub对象

            iBluetoothService = IBluetoothService.Stub.asInterface(iBinder);

            Log.e("TAG", "连接蓝牙成功");
            try {
                iBluetoothService.registerBluetoothStateCallback(iBluetoothStateCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            // 断开连接
            iBluetoothService = null;
        }
    };

    private void starBlueToothService() {
        final Intent blueToothIntent = new Intent();
        blueToothIntent.setAction("com.dfzt.blutooth.service");
        blueToothIntent.setPackage("com.dfzt.bluetooth");
        startService(blueToothIntent);
        bindService(blueToothIntent, bluetoothConnect, Context.BIND_AUTO_CREATE);
    }

//本地蓝牙回调方法

    public IBluetoothStateCallback iBluetoothStateCallback = new IBluetoothStateCallback.Stub() {
        @Override
        public void onBluetoothPlayState(boolean isPlaying) throws RemoteException {

        }

        @Override
        public void onBluetoothConnectState(int state, String name) throws RemoteException {

        }

        @Override
        public void onBluetoothOpenState(boolean opened) throws RemoteException {

        }
    };

    /*
    连接本地音乐AIDL
     */


    private ServiceConnection localmusicConnect = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            // 从连接中获取Stub对象
            iPlayerProvider = IPlayerProvider.Stub.asInterface(iBinder);

            try {
                iPlayerProvider.registerMusicStateListener(iPlayMusicStateListener);
                Log.e("TAG", "连接本地音乐成功");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            // 断开连接
            iPlayerProvider = null;


        }
    };

    private void startLocalService() {
        final Intent localmusicIntent = new Intent();
        localmusicIntent.setAction("com.dfzt.music_player.aidl");
        localmusicIntent.setPackage("com.dfzt.newmyplayer");
        startService(localmusicIntent);
        bindService(localmusicIntent, localmusicConnect, Context.BIND_AUTO_CREATE);
    }

    //本地音乐回调方法
    private IPlayMusicStateListener iPlayMusicStateListener = new IPlayMusicStateListener.Stub() {
        @Override
        public void currentTime(long time) throws RemoteException {

        }

        @Override
        public void durationTime(long time) throws RemoteException {

        }

        @Override
        public void onMusicChange(Music music) throws RemoteException {

        }

        @Override
        public void onMusicPlayFinish() throws RemoteException {

        }

        @Override
        public void onPlayerStart() throws RemoteException {
            Log.e("onPlayerStart", "onPlayerStart");
        }

        @Override
        public void onPlayerPause() throws RemoteException {
            Log.e("onPlayerPause", "onPlayerPause");
        }

        @Override
        public void onPlayModeChange(int model) throws RemoteException {

        }

        @Override
        public void onRootChange(int root) throws RemoteException {
            Log.e("onRootChange", "onRootChange");
        }

        @Override
        public void scanFinish() throws RemoteException {
            Log.e("scanFinish", "scanFinish");
        }

        @Override
        public IBinder asBinder() {
            return null;
        }
    };



    /*
    连接在线音乐AIDL
     */


    private ServiceConnection onlinemusicConnect = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            // 从连接中获取Stub对象
            iOnlineMusicAidl = IOnlineMusicAidl.Stub.asInterface(iBinder);

            try {
                iOnlineMusicAidl.registerSearchListener(iSearchMusicCallBack);
                iOnlineMusicAidl.registerMusicStateListener(oiPlayMusicStateListener);
                Log.e("TAG", "连接在线音乐成功");
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }


        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            // 断开连接
            iOnlineMusicAidl = null;

        }
    };


    //在线音乐服务
    private void startOnlineService() {
        final Intent onlinemusicIntent = new Intent();
        onlinemusicIntent.setAction("com.dfzt.onlinemusic.service");
        onlinemusicIntent.setPackage("com.dfzt.olinemusic");
        startService(onlinemusicIntent);
        bindService(onlinemusicIntent, onlinemusicConnect, Context.BIND_AUTO_CREATE);

    }

    //在线音乐回调方法

    private ISearchMusicCallBack iSearchMusicCallBack = new ISearchMusicCallBack.Stub() {
        @Override
        public void searchMusicSuccess(final List<com.dfzt.olinemusic.entity.Music> list) throws RemoteException {
            Log.e("ONLINEMUSICAIDL", "searchMusicSuccess");


            musicList.clear();
            musicList.addAll(list);

        }

        @Override
        public void noSearchMusic() throws RemoteException {

            Log.e("noSearchMusic", "noSearchMusic");
        }

        @Override
        public void netWorkError() throws RemoteException {

        }


    };

    private com.dfzt.olinemusic.callback.IPlayMusicStateListener oiPlayMusicStateListener = new com.dfzt.olinemusic.callback.IPlayMusicStateListener.Stub() {


        @Override
        public void currentTime(long time) throws RemoteException {

        }

        @Override
        public void durationTime(long time) throws RemoteException {

        }

        @Override
        public void onMusicChange(com.dfzt.olinemusic.entity.Music music) throws RemoteException {
            currentMusic = music;
        }

        @Override
        public void onMusicPlayFinish() throws RemoteException {

        }

        @Override
        public void onPlayerStart() throws RemoteException {

        }

        @Override
        public void onPlayerPause() throws RemoteException {

        }
    };


/*
连接有声内容AIDL
 */


    private ServiceConnection soundcontConnect = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            // 从连接中获取Stub对象
            radioAidl = RadioAidl.Stub.asInterface(iBinder);

            try {
                radioAidl.registerSearchListener(iestRadioServiceCallBack);
                Log.e("TAG", "连接有声内容成功");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            // 断开连接
            radioAidl = null;

        }
    };


    //有声内容服务
    private void startSoundService() {
        final Intent soundcontIntent = new Intent();
        soundcontIntent.setAction("com.dfzt.audio_content.service");
        soundcontIntent.setPackage("com.dfzt.dfzt_radio");
        startService(soundcontIntent);
        bindService(soundcontIntent, soundcontConnect, Context.BIND_AUTO_CREATE);
    }


//有声内容回调方法

    private IESTRadioServiceCallBack iestRadioServiceCallBack = new IESTRadioServiceCallBack.Stub() {
        @Override
        public void onXmlySearchByAlbumName(boolean success, List<Album> albums, int totalPage, int currentPage) throws RemoteException {

            albumList2 = albums;
        }

        @Override
        public void onXmlySearchExactlyCompleted(boolean success, List<Track> tracks) throws RemoteException {

        }

        @Override
        public void onXmlySearchTag(boolean success, String category, String tag) throws RemoteException {

        }

        @Override
        public void onXmlyError(int errorCode, String errorString) throws RemoteException {

        }

        @Override
        public void getNextIndexTrackList(List<Track> list) throws RemoteException {

        }

        @Override
        public IBinder asBinder() {
            return null;
        }
    };


    /*
    多选Dialog框listview
     */
    class ItemOnClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
            CheckBox cBox = (CheckBox) view.findViewById(R.id.X_checkbox);
            if (cBox.isChecked()) {
                Log.i("TAG", "取消选项");
                cBox.setChecked(false);
            } else {
                Log.i("TAG", "确认选项");
                cBox.setChecked(true);
            }

            if (position == 0 && (cBox.isChecked())) {
                //如果是选中 全选  就把所有的都选上 然后更新
                for (int i = 0; i < bl.length; i++) {
                    bl[i] = true;
                }
                adapter.notifyDataSetChanged();
            } else if (position == 0 && (!cBox.isChecked())) {
                //如果是取消全选 就把所有的都取消 然后更新
                for (int i = 0; i < bl.length; i++) {
                    bl[i] = false;
                }
                adapter.notifyDataSetChanged();
            }
            if (position != 0 && (!cBox.isChecked())) {
                // 如果把其它的选项取消   把全选取消
                bl[0] = false;
                bl[position] = false;
                adapter.notifyDataSetChanged();
            } else if (position != 0 && (cBox.isChecked())) {
                //如果选择其它的选项，看是否全部选择
                //先把该选项选中 设置为true
                bl[position] = true;
                int a = 0;
                for (int i = 1; i < bl.length; i++) {
                    if (bl[i] == false) {
                        //如果有一个没选中  就不是全选 直接跳出循环
                        break;
                    } else {
                        //计算有多少个选中的
                        a++;
                        if (a == bl.length - 1) {
                            //如果选项都选中，就把全选 选中，然后更新
                            bl[0] = true;
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }

    }


    public void CreateMyDialog() {
        myDialog = new MyDialog(this);
        adapter = new SetSimpleAdapter(this, mData, R.layout.check, new String[]{"text"},
                new int[]{R.id.X_item_text});
        // 给listview加入适配器
        myDialog.setAdapter(adapter);
        myDialog.setItemsCanFocus(false);
        myDialog.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        myDialog.setOnItemClickListener(new ItemOnClick());

        myDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                hideBottomUIMenu();
            }
        });

        myDialog.setYesOnclickListener("确定", new MyDialog.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                myDialog.dismiss();
                hideBottomUIMenu();
            }
        });

        myDialog.setNoOnclickListener("", new MyDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                myDialog.dismiss();
                hideBottomUIMenu();
            }
        });

        myDialog.show();
    }


    public void CreateDialog() {

        // 动态加载一个listview的布局文件进来
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        getlistview = inflater.inflate(R.layout.listview, null);

        // 给ListView绑定内容
        ListView listview = (ListView) getlistview.findViewById(R.id.X_listview);
        adapter = new SetSimpleAdapter(MainActivity.this, mData, R.layout.check, new String[]{"text"},
                new int[]{R.id.X_item_text});
        // 给listview加入适配器
        listview.setAdapter(adapter);
        listview.setItemsCanFocus(false);
        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listview.setOnItemClickListener(new ItemOnClick());


        testdialog = new AlertDialog.Builder(this)
                .setTitle("请选择测试内容")
                .setIcon(R.mipmap.testicon)
                .setView(getlistview)
                .setPositiveButton("确定", new DialogOnClick())
                .setNegativeButton("取消", new DialogOnClick())
                .create();

        testdialog.show();


        testdialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(26);
        testdialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextSize(26);

        //监听dialog关闭
        testdialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.e("", "dialog dismiss");
                hideBottomUIMenu();
            }
        });


    }

    class DialogOnClick implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case Dialog.BUTTON_POSITIVE:
                    //确定按钮的事件
                    hideBottomUIMenu();
                    break;
                case Dialog.BUTTON_NEGATIVE:
                    //取消按钮的事件
                    hideBottomUIMenu();
                    break;
                default:
                    hideBottomUIMenu();
                    break;
            }
        }
    }

    //重写simpleadapterd的getview方法
    class SetSimpleAdapter extends SimpleAdapter {

        public SetSimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from,
                                int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LinearLayout.inflate(getBaseContext(), R.layout.check, null);
            }
            CheckBox ckBox = (CheckBox) convertView.findViewById(R.id.X_checkbox);
            //每次都根据 bl[]来更新checkbox
            if (bl[position]) {
                ckBox.setChecked(true);
            } else if (bl[position] == false) {
                ckBox.setChecked(false);
            }
            return super.getView(position, convertView, parent);
        }
    }


    private int getSPath() {

        String s = Environment.getExternalStorageDirectory().getAbsolutePath();

        File f = Environment.getExternalStorageDirectory();


        tl_List.add(new TestLog("getAbsolutePath:", f.toString()));
        rAdapter.notifyItemChanged(tl_List.size() - 1);
        return 1;
    }

    //判断外部存储数量

    private static int sdNum() {

        StorageManager sm = (StorageManager) MainActivity.context.getSystemService(Context.STORAGE_SERVICE);

        String[] paths;

        List<String> pathlist = new ArrayList<>();
        pathlist.clear();


        try {
            paths = (String[]) sm.getClass().getMethod("getVolumePaths", null).invoke(sm, null);


            for (String s : paths) {
                if (s.startsWith("/storage/emulated/0")) {

                    continue;
                }
                pathlist.add(s);

                tl_List.add(new TestLog("paths name:", s));
                rAdapter.notifyItemChanged(tl_List.size() - 1);

            }


        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        if (pathlist.size() == 0) {
            USBFLAG = false;
        }
        return pathlist.size();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<String> getAllExternalStorage() {
        List<String> storagePath = new ArrayList<>();
        StorageManager storageManager = (StorageManager) this.getSystemService(Context.STORAGE_SERVICE);
        StorageVolume[] storageVolumes;
        try {
            Method getVolumeList = StorageManager.class.getDeclaredMethod("getVolumeList");
            storageVolumes = (StorageVolume[]) getVolumeList.invoke(storageManager);
            Method getVolumeState = StorageManager.class.getDeclaredMethod("getVolumeState", String.class);
            for (StorageVolume storageVolume : storageVolumes) {
                Method getPath = null;
                getPath = StorageVolume.class.getMethod("getPath");
                String path = (String) getPath.invoke(storageVolume);
                Log.e("sdcard", "====path==" + path);
                String state = (String) getVolumeState.invoke(storageManager, path);
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    storagePath.add(path);
                }
            }
        } catch (Exception e) {
            Log.e("cdl", e.getMessage());
        }
        return storagePath;
    }

    public static String getSDcardPath(Context context) {
        String pathBack = null;
        if (Build.VERSION.SDK_INT < 23) {
            pathBack = "/mnt/external_sd/";
            File file = new File(pathBack);
            if (!file.exists()) {
                pathBack = null;
            }
        } else {
            pathBack = getSDcardDir(context);
            Log.e("path==", pathBack);
        }
        if (pathBack == null || pathBack.contains("null") || pathBack.length() < 6) {
            return null;
        }
        if (pathBack.endsWith("/")) {
            pathBack = pathBack.substring(0, pathBack.length() - 1);
        }
        return pathBack;
    }

    private static String getSDcardDir(Context context) {
        String sdcardDir = null;
//            StorageManager storageManager = getStorageManager(context);
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        Class<?> volumeInfoClazz = null;
        Class<?> diskInfoClazz = null;
        try {
            diskInfoClazz = Class.forName("android.os.storage.DiskInfo");
            Method isSd = diskInfoClazz.getMethod("isSd");
            volumeInfoClazz = Class.forName("android.os.storage.VolumeInfo");
            Method getType = volumeInfoClazz.getMethod("getType");
            Method getDisk = volumeInfoClazz.getMethod("getDisk");
            Field path = volumeInfoClazz.getDeclaredField("path");
            Method getVolumes = storageManager.getClass().getMethod("getVolumes");
            List<Class<?>> result = (List<Class<?>>) getVolumes.invoke(storageManager);
            for (int i = 0; i < result.size(); i++) {
                Object volumeInfo = result.get(i);
                if ((int) getType.invoke(volumeInfo) == 0) {
                    Object disk = getDisk.invoke(volumeInfo);
                    if (disk != null) {
                        if ((boolean) isSd.invoke(disk)) {
                            sdcardDir = (String) path.get(volumeInfo);
                            break;
                        }
                    }
                }
            }
            return sdcardDir + File.separator;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



        /*
截屏
         */


    private Bitmap screenShotWholeScreen() {
        View dView = getWindow().getDecorView();
        dView.setDrawingCacheEnabled(true);
        dView.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(dView.getDrawingCache());
        return bitmap;
    }

    /*
    重写销毁方法在销毁时注销监听和服务
     */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FileUtils.saveFile(tl_List);
        FileUtils.saveFile(screenShotWholeScreen(), "testPic.jpeg", MainActivity.this);
        Log.e("保存截图到", "//sdcard//UTest//testPic.jpeg");
        Log.e("关闭应用", "");
        try {

            Log.e("UTest", "退出优测");

            iOnlineMusicAidl.unRegisterMusicStateListener(oiPlayMusicStateListener);

            iOnlineMusicAidl.unRegisterSearchListener(iSearchMusicCallBack);

            radioAidl.unRegisterSearchListener(iestRadioServiceCallBack);

            iBluetoothService.unRegisterBluetoothStateCallback(iBluetoothStateCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        unbindService(onlinemusicConnect);

        unbindService(bluetoothConnect);
        unbindService(soundcontConnect);


        unregisterReceiver(usbStateReceiver);
//        unregisterReceiver(sdstateReceiver);
//        unregisterReceiver(btstateReceiver);
    }
}