package com.client.tok.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class DrawableUtils {
    public static Drawable getDrawableById(Context context, int drawableId) {
        if (drawableId > 0) {
            return context.getResources().getDrawable(drawableId);
        }
        return null;
    }
}
