// IOnlineMusicAidl.aidl
package com.dfzt.olinemusic;

// Declare any non-default types here with import statements
import com.dfzt.olinemusic.callback.IPlayMusicStateListener;
import com.dfzt.olinemusic.entity.Music;
import com.dfzt.olinemusic.mvp.entity.SearchMusic;
import com.dfzt.olinemusic.callback.ISearchMusicCallBack;
interface IOnlineMusicAidl {
   /**
    * 上一曲
    */
    void previous();

    /**
    * 下一曲
    */
    void next();

    /**
    * 播放暂停
    */
    void doPlayOrPause();

    /**
    * 暂停
    * */
    void pause();

    /**
    * 播放
    * */
    void play();

    /**
    *  获取播放模式
    *  return 参数 0 列表循环  1 随机播放 2单曲循环
    */
    int getPlayModel();

    /**
    *  设置播放模式
    *  state 0 列表循环  1 随机播放 2单曲循环
    */
    void setPlayModel(int state);

    //设置音乐状态监听的接口回调
    void registerMusicStateListener(in IPlayMusicStateListener callBack);

    //销毁音乐状态监听的接口回调
    void unRegisterMusicStateListener(in IPlayMusicStateListener callBack);

    /**
    *   判断单前是否正在播放
    *   return  false表示不再播放  true表示正在播放
    */
    boolean isPlaying();

    /**
    * 获取当前播放总时长
    * return 当前播放总时长
    */
    long getDurationTime();

    /**
    *   设置播放进度
    *   time 要设置的播放进度
    */
    void seekTo(int time);

    /**
    *  获取当前播放的音乐信息
    *  如果为空的话表示 没有播放过音乐
    */
    Music getCurrentMusic();

    /**
    * 获取本地音乐列表
    */
    List<Music> getLocalMusics();

    /**
    * 搜索歌曲
    *  text 要搜索的内容
    */
    void searchOnLineMusic(String text);

    //设置搜索的内容回调监听
    void registerSearchListener(in ISearchMusicCallBack callBack);

    //销毁搜索监听的接口回调
    void unRegisterSearchListener(in ISearchMusicCallBack callBack);

    /**
    *   播放搜索的歌曲
    *   flag true表示要将这个音乐添加到播放列表中  false不将这首歌添加到播放列表中
    */
    void playSearchMusic(in Music searchMusic,boolean flag);

    /**
    * 播放第三方歌曲
    * */
    void playOtherMusic(String author ,String musicName,String url,boolean flag);

}
