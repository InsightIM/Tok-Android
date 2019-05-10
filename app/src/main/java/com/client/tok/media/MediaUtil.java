package com.client.tok.media;

import com.client.tok.utils.FileUtilsJ;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.StringUtils;
import java.util.Arrays;
import java.util.List;

public class MediaUtil {
    private final static String TAG = "audioUtil";
    private static List<String> AUDIO_TAG_LIST = Arrays.asList("ogg");
    private static List<String> VIDEO_TAG_LIST = Arrays.asList("mp4","avi");

    /**
     * is file is audio
     *
     * @param path path
     * @return true:false
     */
    public static boolean isAudio(String path) {
        LogUtil.i(TAG, "isAudio:" + path);
        if (!StringUtils.isEmpty(path)) {
            return AUDIO_TAG_LIST.contains(FileUtilsJ.getFileSuffix(path));
        } else {
            return false;
        }
    }

    /**
     * is file video
     *
     * @param path path
     * @return true:false
     */
    public static boolean isVideo(String path) {
        LogUtil.i(TAG, "isVideo:" + path);
        if (!StringUtils.isEmpty(path)) {
            return VIDEO_TAG_LIST.contains(FileUtilsJ.getFileSuffix(path));
        } else {
            return false;
        }
    }
}
