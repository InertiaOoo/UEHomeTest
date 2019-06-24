package com.dfzt.music.aidl;
import com.dfzt.music.aidl.Music;
import com.dfzt.music.aidl.callback.IPlayMusicStateListener;
interface IPlayerProvider{

	/*
	获取歌曲列表信息
	Get the player's current song list,String is absolute path
	return list or null
	*/
	List<String> getMusics();

	/*
	获取当前播放状态
	Get the player's current play status
		return:
			1---state_playing
			2---state_paused;
			3---state_stoped
			defalut state is 3.
	*/
	int getPlayerState();

	/*
	获取正在播放的歌名
	Get the player's playing song name
	return name or null
	*/
	String getPlayingName();

	/*
	控制播放器播放,默认播放第一首或者继续播放
	*/
	void play();

	/*
	控制播放器暂停
	*/
	void pause();

	/*
	控制播放器下一曲
	*/
	void next();

	/*
	控制播放器上一曲
	*/
	void prevoius();

	/*
	设置播放模式
		0---列表循环
		1---单曲循环
		2---随机播放
	*/
	void setMode(int mode);

	/*
	获取当前播放模式
		0---列表循环
		1---单曲循环
		2---随机播放
		3--顺序播放
		默认是0
	*/
	int getMode();

	/*
	选择播放路径
	1---选择本地
	2---选择sd卡
	3---选择u盘
	*/
	void setRoot(int root);

	/*
	获取当前播放根路径
	1---本地
	2---sd卡
	3---u盘
	*/
	int getRoot();

	/*
	获取当前播放歌曲的总时长
	*/
	long getDuration();

	/*
	获取当前播放歌曲的播放进度
	*/
	long getCurrent();

	/*
	设置当前播放的播放进度，进度值应在0~总时长之间
	*/
	void setProgress(int progress);

	/*
	指定路径播放歌曲
	*/
	void playByPath(String path);

	/*
	指定列表下标播放歌曲
	*/
	void playByPosition(int position);

	/*
	获取当前播放歌曲所在歌曲列表的索引位置
	return index in play list
	     有播放歌曲:返回当前所在序号(0~list.size - 1)
	     无播放歌曲:-1
	*/
	int getCurrentPlayPosition();

    /**
    * 获取歌曲的信息
    */
    List<Music> getAllMusics();

    /**
     *得到是否开机自动播放
    * */
	boolean getBootPlay();

    /**
    * 设置是否开机自动播放
    * */
    void setBootPlay(boolean falg);

    /**
    * 是否开启了分区功能
    * */
    void setPartitionPlay(boolean isOpened);

    //获取当前是否有音频焦点抢占
    boolean isMusicPlayVolFocus();

    //删除歌曲
    boolean deleteFile(String path);

    //设置是否需要音频焦点抢占
    void  setMusicPlayVolFocus(boolean isVolFocus);

    //获取扫描的状态
    int getScanState();

    //设置是否需要 开始播放时音量 逐渐变大
    void setNeedPlayVolBeLarger(boolean isNeed);

    //设置音乐状态监听的接口回调
    void registerMusicStateListener(in IPlayMusicStateListener callBack);

    //销毁音乐状态监听的接口回调
    void unRegisterMusicStateListener(in IPlayMusicStateListener callBack);

    //获取单前正在播放的歌曲
    Music getCurrentMusic();
}