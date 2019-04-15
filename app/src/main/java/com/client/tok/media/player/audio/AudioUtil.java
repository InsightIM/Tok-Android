package com.client.tok.media.player.audio;

import com.client.tok.utils.FileUtilsJ;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.StringUtils;
import java.util.Arrays;
import java.util.List;

public class AudioUtil {
    private final static String TAG = "audioUtil";
    private static List<String> AUDIO_TAG_LIST = Arrays.asList("ogg");

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
}
