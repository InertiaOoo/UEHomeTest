package com.dfzt.dfzt_radio;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.dfzt.dfzt_radio.callback.IESTRadioServiceCallBack;
import com.dfzt.dfzt_radio.callback.IRadioPlayStateCallBack;
interface RadioAidl {
    //搜索喜马拉雅资源返回声音列表
    List<Album> searchAudioContentAlbum(String content);
    //播放喜马拉雅资源
    void playAudioContentVoice(in Album album);
    //获取播放器的状态
    /**
        STATE_INITIALIZED    = 1       播放器已经初始化了
        STATE_PREPARING      = 9       播放器准备中
        STATE_PREPARED       = 1       播放器准备完成
        STATE_STARTED        = 3       播放器开始播放
        STATE_STOPPED        = 4       播放器停止
        STATE_PAUSED         = 5       播放器暂停
        STATE_COMPLETED      = 6       播放器单曲播放完成
    */
    int getPlayerState();

    //获取当前播放的声音
    Track getCurrentPlayTrack();

    //控制播放器暂停
    void doPauseTrack();

    //控制播放继续播放
    void doPlayTrack();

    //播放Track
    void playAudioTrack(in List<Track> list ,int index);

    //获取下一页的播放声音列表
    void getNextIndexTrackList();

    //获取当前的声音列表
    List<Track> getCurrentTrackList();

    //获取当前播放声音的下标
    int getCurrentTrackIndex();

    //获取单前的专辑id
    String getCurrentAlbumId();

    //通过名称搜索专辑
    void searchAlbumByName(String categoryId, String name,int page);

    //根据分类和标签获取某个分类某个标签下的专辑列表
    void searchAlbumList(String categoryId, String tagName,int page);

    //根据专辑ID获取专辑下的声音列表
    void searchExactlyByAlbum(String categoryId , int page);

    //根据上一次所听声音的id，获取此声音所在那一页的声音
    void searchExactlyByLastPlayTracks(String albumId,String resourceId);

    //设置搜索的内容回调监听
    void registerSearchListener(in IESTRadioServiceCallBack callBack);

    //销毁搜索监听的接口回调
    void unRegisterSearchListener(in IESTRadioServiceCallBack callBack);

    //根据分类和标签获取热门声音列表
    void searchHotTracks(String categoryId,String tagName);

    //设置播放状态的监听回调
    void registerPlayStateLiestener(in IRadioPlayStateCallBack callBack);

    //设置播放状态的监听回调
     void unRegisterPlayStateLiestener(in IRadioPlayStateCallBack callBack);

     /**
     * 上一曲
     */
     void previous();

     /**
     * 下一曲
     */
     void next();

     void seekTo(int time);
}
