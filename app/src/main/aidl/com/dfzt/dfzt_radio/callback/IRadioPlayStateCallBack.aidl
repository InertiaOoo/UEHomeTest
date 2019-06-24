// IRadioPlayState.aidl
package com.dfzt.dfzt_radio.callback;
import com.ximalaya.ting.android.opensdk.model.track.Track;
// Declare any non-default types here with import statements

interface IRadioPlayStateCallBack {
    //当前播放的进度
    void currentTime(long time);

    //当前声音的总进度
    void durationTime(long time);

    //歌曲切换
    void onMusicChange(in Track track);

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
