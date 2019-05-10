package com.client.tok.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.client.tok.R;
import jp.wasabeef.glide.transformations.MaskTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.load.resource.bitmap.VideoDecoder.FRAME_OPTION;

public class ImageLoadUtils {
    private static String TAG = "ImageLoadUtils";
    private final static float THUMB_NAIL = 0.1f;
    //imageLoader load img need
    private static String IMG_SDCARD = "file://";

    private static RequestOptions getOptions() {
        RequestOptions requestOptions = new RequestOptions();
        //requestOptions.placeholder(R.drawable.img_place_holder);
        requestOptions.error(R.drawable.img_error);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(true);
        return requestOptions;
    }

    private static RequestOptions getPortraitOptions() {
        RequestOptions requestOptions = new RequestOptions();
        //if placeholder exist，if load images，it will placeholder first,and has s blink when the real image show
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(true);
        return requestOptions;
    }

    private static RequestOptions getVideoOptions() {
        RequestOptions requestOptions = RequestOptions.frameOf(1);
        requestOptions.set(FRAME_OPTION, MediaMetadataRetriever.OPTION_CLOSEST);
        //requestOptions.placeholder(R.drawable.img_place_holder);
        requestOptions.error(R.drawable.img_error);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        requestOptions.skipMemoryCache(true);
        return requestOptions;
    }

    /**
     * 加载缩略图
     *
     * @param context 上下文
     * @param path url/filepath/uri
     * @param imgView 目标imageView
     */
    public static void loadThumbnail(Context context, String path, ImageView imgView) {
        LogUtil.i(TAG, "loadThumbnail path:" + path);
        Glide.with(context)
            .setDefaultRequestOptions(getOptions())
            .load(path)
            .thumbnail(THUMB_NAIL)
            .into(imgView);
    }

    public static void loadVideoMask(Context context, String path, ImageView imgView, int maskResId) {
        LogUtil.i(TAG, "loadMask video path:" + path);
        Glide.with(context)
            .setDefaultRequestOptions(getVideoOptions())
            .load(path)
            .apply(RequestOptions.bitmapTransform(
                new MultiTransformation<>(new CenterCrop(), new MaskTransformation(maskResId))))
            .thumbnail(THUMB_NAIL)
            .into(imgView);
    }

    public static void loadMask(Context context, String path, ImageView imgView, int maskResId) {
        LogUtil.i(TAG, "loadMask path:" + path);
        Glide.with(context)
            .setDefaultRequestOptions(getOptions())
            .load(path)
            .apply(RequestOptions.bitmapTransform(
                new MultiTransformation<>(new CenterCrop(), new MaskTransformation(maskResId))))
            .thumbnail(THUMB_NAIL)
            .into(imgView);
    }

    public static void loadRoundImg(Context context, String path, ImageView imgView,
        final LoadListener listener) {
        LogUtil.i(TAG, "loadMask path:" + path);
        Glide.with(context)
            .setDefaultRequestOptions(getPortraitOptions())
            .load(path)
            .listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model,
                    Target<Drawable> target, boolean isFirstResource) {
                    if (listener != null) {
                        listener.onLoadFailed();
                    }
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model,
                    Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    if (listener != null) {
                        listener.onResourceReady();
                    }
                    return false;
                }
            })
            .apply(RequestOptions.bitmapTransform(new MultiTransformation<>(new CenterCrop(),
                new RoundedCornersTransformation(ScreenUtils.dp2px(context, 4), 0))))
            .thumbnail(THUMB_NAIL)
            .into(imgView);
    }

    public static void loadRoundImg(Context context, int imgResId, ImageView imgView) {
        LogUtil.i(TAG, "loadMask imgResId:");
        Glide.with(context)
            .setDefaultRequestOptions(getPortraitOptions())
            .load(imgResId)
            .apply(RequestOptions.bitmapTransform(new MultiTransformation<>(new CenterCrop(),
                new RoundedCornersTransformation(ScreenUtils.dp2px(context, 4), 0))))
            .thumbnail(THUMB_NAIL)
            .into(imgView);
    }

    public static void loadImg(Context context, String path, ImageView imgView) {
        Glide.with(context).setDefaultRequestOptions(getOptions()).load(path).into(imgView);
    }

    public static class LoadListener {

        public void onLoadFailed() {
        }

        public void onResourceReady() {
        }
    }
}
