package com.appota.model;

import android.graphics.Bitmap;

public class ContentInfo {

	private Bitmap thumbs;
	private int videoThumbnailsResId;
	private String text;
	private String path;
	private String duration;

	public Bitmap getThumbs() {
		return thumbs;
	}

	public void setThumbs(Bitmap thumbs) {
		this.thumbs = thumbs;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public int getVideoThumbnailsResId() {
		return videoThumbnailsResId;
	}

	public void setVideoThumbnailsResId(int videoThumbnailsResId) {
		this.videoThumbnailsResId = videoThumbnailsResId;
	}

}
