// ISearchMusicCallBack.aidl
package com.dfzt.olinemusic.callback;

// Declare any non-default types here with import statements
import com.dfzt.olinemusic.entity.Music;
//import com.dfzt.olinemusic.mvp.entity.SearchMusic;
interface ISearchMusicCallBack {
   //搜索成功
   void searchMusicSuccess(in List<Music> list);

   //没有搜索到音乐
   void noSearchMusic();

   //网络不好
   void netWorkError();
}
