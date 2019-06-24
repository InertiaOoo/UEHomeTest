package com.dfzt.music.aidl;

import android.os.Parcel;
import android.os.Parcelable;

public class Music implements Parcelable {

	/* 歌曲名字 */
	private String name;

	/* 歌曲路径 */
	private String path;

	/* 歌手 */
	private String artist;

	/* 专辑路径 */
	private String album;

	/* 用于排序字母 */
	private String sortLetters;

	private boolean isPlaying;

	private long duration;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean playing) {
		isPlaying = playing;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public Music(String name, String path, String artist, String album, String sortLetters, boolean isPlaying) {
		super();
		this.name = name;
		this.path = path;
		this.artist = artist;
		this.album = album;
		this.sortLetters = sortLetters;
		this.isPlaying = isPlaying;
	}
	public Music() {
	}
	public Music(Parcel in) {
		name = in.readString();
		path = in.readString();
		artist = in.readString();
		album = in.readString();
		sortLetters = in.readString();
		duration = in.readLong();
	}

	public static final Creator<Music> CREATOR = new Creator<Music>() {
		@Override
		public Music createFromParcel(Parcel in) {
			return new Music(in);
		}

		@Override
		public Music[] newArray(int size) {
			return new Music[size];
		}
	};


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(name);
		parcel.writeString(path);
		parcel.writeString(artist);
		parcel.writeString(album);
		parcel.writeString(sortLetters);
		parcel.writeDouble(duration);
	}
}
