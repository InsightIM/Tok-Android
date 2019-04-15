package com.client.tok.utils;

import android.content.Context;
import android.widget.Toast;
import com.client.tok.TokApplication;

public class ToastUtils extends Toast {
    private static Toast mToast = null;

    public ToastUtils(Context context) {
        super(context);
    }

    public static void show(CharSequence text) {
        show(text, Toast.LENGTH_SHORT);
    }

    public static void show(int textId) {
        show(textId, Toast.LENGTH_SHORT);
    }

    public static void showLong(CharSequence text) {
        show(text, Toast.LENGTH_LONG);
    }

    public static void showLong(int textId) {
        show(textId, Toast.LENGTH_LONG);
    }

    public static void show(int txtId, int duration) {
        show(TokApplication.getInstance().getString(txtId), duration);
    }

    public static void show(CharSequence text, int duration) {
        if (null == mToast) {
            mToast = Toast.makeText(TokApplication.getInstance(), text, duration);
        } else {
            mToast.setText(text);
            mToast.setDuration(duration);
        }
        mToast.show();
    }
}