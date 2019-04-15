package com.client.tok.ui.imgzoom;

import android.graphics.Rect;
import android.os.Parcel;
import android.support.annotation.Nullable;
import com.previewlibrary.enitity.IThumbViewInfo;

public class ImgViewInfo implements IThumbViewInfo {

    private String url;
    private Rect mBounds;
    private String user = "";
    private String videoUrl;

    public ImgViewInfo() {
    }

    public ImgViewInfo(String url) {
        this.url = url;
    }

    public ImgViewInfo(String videoUrl, String url) {
        this.url = url;
        this.videoUrl = videoUrl;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String getUrl() {//将你的图片地址字段返回
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public Rect getBounds() {//将你的图片显示坐标字段返回
        return mBounds;
    }

    @Nullable
    @Override
    public String getVideoUrl() {
        return videoUrl;
    }

    public void setBounds(Rect bounds) {
        mBounds = bounds;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public void setPath(String path){
        //在这里判断是图片还是video,放到url或videoUrl TODO
        setUrl(path);
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeParcelable(this.mBounds, flags);
        dest.writeString(this.user);
        dest.writeString(this.videoUrl);
    }

    protected ImgViewInfo(Parcel in) {
        this.url = in.readString();
        this.mBounds = in.readParcelable(ImgViewInfo.class.getClassLoader());
        this.user = in.readString();
        this.videoUrl = in.readString();
    }

    public static final Creator<ImgViewInfo> CREATOR = new Creator<ImgViewInfo>() {
        @Override
        public ImgViewInfo createFromParcel(Parcel source) {
            return new ImgViewInfo(source);
        }

        @Override
        public ImgViewInfo[] newArray(int size) {
            return new ImgViewInfo[size];
        }
    };
}
