package com.client.tok.utils;

import com.client.tok.TokApplication;
import com.client.tok.R;

public class ColorUtils {
    private static String TAG = "ColorUtils";
    private static int MY_COLOR_INDEX = 7;
    private static int DEFAULT_COLOR_INDEX = 4;
    private static int[] colorList;

    static {
        colorList =
            TokApplication.getInstance().getResources().getIntArray(R.array.portrait_colors);
    }

    public static int getRandomColor() {
        return colorList[0];
    }

    public static int getOwnerColor() {
        return colorList[MY_COLOR_INDEX];
    }

    public static int getFriendColor(String friendKey) {
        if (StringUtils.isEmpty(friendKey)) {
            return colorList[DEFAULT_COLOR_INDEX];
        } else {
            int c = friendKey.charAt(friendKey.length() - 1);
            int index = c % colorList.length;
            if (index == 0) {
                index = DEFAULT_COLOR_INDEX;
            }
            return colorList[index];
        }
    }
}
