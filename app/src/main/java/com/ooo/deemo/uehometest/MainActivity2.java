package com.ooo.deemo.uehometest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dfzt.olinemusic.IOnlineMusicAidl;
import com.dfzt.olinemusic.callback.IPlayMusicStateListener;
import com.dfzt.olinemusic.callback.ISearchMusicCallBack;
import com.dfzt.olinemusic.entity.Music;


import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {
    private Intent musicIntent = new Intent();
    private IOnlineMusicAidl musicAidl;
    /**
     * 名称:
     */
    private TextView mMainMusicName;
    private SeekBar mMainSeekbar;
    /**
     * 上一曲
     */
    private Button mMainPrveTv;
    /**
     * 播放
     */
    private Button mMainPlayTv;
    /**
     * 下一曲
     */
    private Button mMainNextTv;
    /**
     * 搜索
     */
    private Button mMainSearchTv;
    private ListView mMainListview;
    /**
     * 搜索
     */
    private EditText mMainSearchEdit;
    private SearchMusicAdapter adapter;
    private List<Music> musicList = new ArrayList<>();
    private Music currentMusic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initView();
        connectAidl();
        initAdapter();
    }

    private void connectAidl() {
        musicIntent.setAction("com.dfzt.onlinemusic.service");
        musicIntent.setPackage("com.dfzt.olinemusic");
        startService(musicIntent);
        bindService(musicIntent, connection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicAidl = IOnlineMusicAidl.Stub.asInterface(service);
            Log.e("TAG", "连接成功");
            //，连接成功 注册callBack
            try {
                musicAidl.registerMusicStateListener(playMusicStateListener);
                musicAidl.registerSearchListener(searchMusicCallBack);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicAidl = null;
        }
    };

    private void initAdapter() {
        adapter = new SearchMusicAdapter(this);
        adapter.setList(musicList);
        mMainListview.setAdapter(adapter);

        mMainListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                   /* Music music = new Music();
                    music.setSongId(Long.valueOf(musicList.get(position).getSongid()));
                    music.setType(Music.Type.ONLINE);
                    music.setTitle(musicList.get(position).getSongname());
                    music.setArtist(musicList.get(position).getArtistname());
                    Log.e("TAG",music.getSongId() +" = 开始播放 = " + music.getTitle());*/
                    musicAidl.playSearchMusic(musicList.get(position),true);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(connection != null){
            try {
                musicAidl.unRegisterMusicStateListener(playMusicStateListener);
                musicAidl.unRegisterSearchListener(searchMusicCallBack);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            unbindService(connection);
        }
    }

    private void initView() {
        mMainMusicName = (TextView) findViewById(R.id.main_music_name);
        mMainSeekbar = (SeekBar) findViewById(R.id.main_seekbar);
        mMainPrveTv = (Button) findViewById(R.id.main_prve_tv);
        mMainPrveTv.setOnClickListener(this);
        mMainPlayTv = (Button) findViewById(R.id.main_play_tv);
        mMainPlayTv.setOnClickListener(this);
        mMainNextTv = (Button) findViewById(R.id.main_next_tv);
        mMainNextTv.setOnClickListener(this);
        mMainSearchTv = (Button) findViewById(R.id.main_search_tv);
        mMainSearchTv.setOnClickListener(this);
        mMainListview = (ListView) findViewById(R.id.main_listview);
        mMainSearchEdit = (EditText) findViewById(R.id.main_search_edit);
        mMainSearchEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ShowKeyboard(mMainSearchEdit);
            }
        });
    }

    //显示虚拟键盘
    public void ShowKeyboard(View v) {
        InputMethodManager imm = ( InputMethodManager ) getSystemService( Context.INPUT_METHOD_SERVICE );

        imm.showSoftInput(v,InputMethodManager.SHOW_FORCED);
    }

    @Override
    public void onClick(View v) {
        if (musicAidl == null) {
            return;
        }
        try {
            switch (v.getId()) {
                case R.id.main_prve_tv:
                    musicAidl.previous();
                    break;
                case R.id.main_play_tv:
                    musicAidl.doPlayOrPause();
                    break;
                case R.id.main_next_tv:
                    musicAidl.next();
                    break;
                case R.id.main_search_tv:
                    musicAidl.searchOnLineMusic(mMainSearchEdit.getText().toString());
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private IPlayMusicStateListener playMusicStateListener = new IPlayMusicStateListener.Stub() {
        @Override
        public void currentTime(long time) throws RemoteException {
            mMainSeekbar.setProgress((int) time);
        }

        @Override
        public void durationTime(long time) throws RemoteException {
            mMainSeekbar.setMax((int) time);
        }

        @Override
        public void onMusicChange(Music music) throws RemoteException {
            currentMusic = music;
            handler.sendEmptyMessage(0x124);

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
    private ISearchMusicCallBack searchMusicCallBack = new ISearchMusicCallBack.Stub() {
        @Override
        public void searchMusicSuccess(List<Music> list) throws RemoteException {
            musicList.clear();
            musicList.addAll(list);

            handler.sendEmptyMessage(0x123);
        }

        @Override
        public void noSearchMusic() throws RemoteException {

        }

        @Override
        public void netWorkError() throws RemoteException {

        }
    };


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x123){
                adapter.setList(musicList);
            }else if (msg.what == 0x124){
                mMainMusicName.setText("音乐名称："+ currentMusic.getTitle());
            }
        }
    };
}
