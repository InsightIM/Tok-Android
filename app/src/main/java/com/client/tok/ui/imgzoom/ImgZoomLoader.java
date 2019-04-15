package com.client.tok.ui.imgzoom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.client.tok.utils.ImageLoadUtils;
import com.client.tok.utils.LogUtil;
import com.previewlibrary.loader.IZoomMediaLoader;
import com.previewlibrary.loader.MySimpleTarget;

public class ImgZoomLoader implements IZoomMediaLoader {
    private String TAG = "ImgZoomLoader";
    @Override
    public void displayImage(@NonNull Fragment context, @NonNull String path, ImageView imageView,
        @NonNull final MySimpleTarget simpleTarget) {
        ImageLoadUtils.loadImg(context.getContext(),path,imageView);
        simpleTarget.onResourceReady();
        LogUtil.i(TAG,"displayImage");
    }

    @Override
    public void displayGifImage(@NonNull Fragment context, @NonNull String path,
        ImageView imageView, @NonNull final MySimpleTarget simpleTarget) {
        ImageLoadUtils.loadImg(context.getContext(),path,imageView);
        simpleTarget.onResourceReady();
        LogUtil.i(TAG,"displayGifImage");
    }

    @Override
    public void onStop(@NonNull Fragment context) {
        Glide.with(context).onStop();
        LogUtil.i(TAG,"onStop");
    }

    @Override
    public void clearMemory(@NonNull Context c) {
        Glide.get(c).clearMemory();
        LogUtil.i(TAG,"clearMemory");
    }
}
