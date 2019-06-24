// IPlayMusicStateListener.aidl
package com.dfzt.olinemusic.callback;

// Declare any non-default types here with import statements
import com.dfzt.olinemusic.entity.Music;
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
}
