package com.client.tok.ui.imgzoom;

import java.util.List;

public class ImgViewInfoList {
    private int curIndex;
    private String curPath;
    private List<ImgViewInfo> imgViewInfoList;

    public int getCurIndex() {
        return curIndex;
    }

    public void setCurIndex(int curIndex) {
        this.curIndex = curIndex;
    }

    public String getCurPath() {
        return curPath;
    }

    public void setCurPath(String curPath) {
        this.curPath = curPath;
    }

    public List<ImgViewInfo> getImgViewInfoList() {
        return imgViewInfoList;
    }

    public void setImgViewInfoList(List<ImgViewInfo> imgViewInfoList) {
        this.imgViewInfoList = imgViewInfoList;
    }
}
