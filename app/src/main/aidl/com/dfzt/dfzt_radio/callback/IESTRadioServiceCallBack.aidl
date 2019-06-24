// IESTRadioServiceCallBack.aidl
package com.dfzt.dfzt_radio.callback;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
// Declare any non-default types here with import statements

interface IESTRadioServiceCallBack {
    /**
         * 根据名称搜索专辑
         * @param success 状态信息
         * @param albums 对应状态附带信息,专辑列表
         * @param totalPage : 专辑的总页数
         * @param currentPage : 专辑的当前的页数
         */
        void onXmlySearchByAlbumName(boolean success, in List<Album> albums,
                int totalPage, int currentPage);

        /**
         * 根据专辑ID精确搜索声音完成后回调
         * @param success 状态信息
         * @param tracks 声音列表
         */
        void onXmlySearchExactlyCompleted(boolean success, in List<Track> tracks);

        /**
         * 根据类型，查找这个类型下的标签
         * @param success
         * @param category
         * @param tag
         */
        void onXmlySearchTag(boolean success, String category, String tag);


        void onXmlyError(int errorCode, String errorString);

        void getNextIndexTrackList(in List<Track> list);
}
