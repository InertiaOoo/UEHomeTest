// IPlayMusicStateListener.aidl
package com.dfzt.music.aidl.callback;

// Declare any non-default types here with import statements
import com.dfzt.music.aidl.Music;
interface IPlayMusicStateListener {
   //当前播放的进度
   void currentTime(long time);

   //当前歌曲的总进度
   void durationTime(long time);

   //歌曲切换
   void onMusicChange(in Music music);

    //歌曲播放完成的回调
    void onMusicPlayFinish();

     /**
     * 开始播放
     */
    void onPlayerStart();

    /**
     * 暂停播放
     */
    void onPlayerPause();

    //播放模式的切换
    void onPlayModeChange(int model);

    //播放路径的切换
    void onRootChange(int root);

    //扫描歌曲完成
    void scanFinish();
}
