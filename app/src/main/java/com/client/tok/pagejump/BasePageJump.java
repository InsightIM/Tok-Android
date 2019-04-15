package com.client.tok.pagejump;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.View;
import com.client.tok.TokApplication;

class BasePageJump {
    /**
     * base jump
     */
    protected static void jump(Context context, Intent intent) {
        try {
            if (context == null) {
                context = TokApplication.getInstance();
            }
            if (!(context instanceof Activity)) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * base jump for result
     */
    protected static void jumpForResult(Activity activity, Intent intent, int reqCode) {
        try {
            if (activity == null) {
                return;
            }
            activity.startActivityForResult(intent, reqCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static void jumpWithTransition(Context context, Intent intent,
        Pair<View, String>... sharedElements) {
        if (context instanceof Activity
            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1
            && sharedElements != null
            && sharedElements.length > 0) {
            ActivityOptionsCompat compat =
                ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,
                    sharedElements);
            ActivityCompat.startActivity(context, intent, compat.toBundle());
        } else {
            jump(context, intent);
        }
    }
}
