package com.client.tok.ui.imgzoom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import com.previewlibrary.GPreviewBuilder;
import com.previewlibrary.ZoomMediaLoader;
import java.util.List;

public class ImgZoomManager {
    public static void init() {
        ZoomMediaLoader.getInstance().init(new ImgZoomLoader());
    }

    public static void showSingleImg(Context context, View fromView, String path) {
        ImgViewInfo info = computeBound(fromView, path);
        GPreviewBuilder.from((Activity) context).setSingleData(info).setCurrentIndex(0).start();
    }

    public static void showImgList(Context context, List<ImgViewInfo> list, int currentIndex) {
        GPreviewBuilder.from((Activity) context)
            .setData(list)
            .setCurrentIndex(currentIndex)
            .setType(GPreviewBuilder.IndicatorType.Number)
            .start();
    }

    private static ImgViewInfo computeBound(View view, String path) {
        ImgViewInfo info = new ImgViewInfo(path);
        Rect bounds = computerBound(view);
        info.setBounds(bounds);
        return info;
    }

    public static Rect computerBound(View view) {
        Rect bounds = new Rect();
        if (view != null) {
            view.getGlobalVisibleRect(bounds);
        }
        return bounds;
    }
}
