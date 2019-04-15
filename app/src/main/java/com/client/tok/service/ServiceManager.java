package com.client.tok.service;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.client.tok.TokApplication;

public class ServiceManager {
    public static void startToxService() {
        Context context = TokApplication.getInstance();
        Intent intent = new Intent(context, ChatMainService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public static void stopToxService() {
        Intent intent = new Intent(TokApplication.getInstance(), ChatMainService.class);
        TokApplication.getInstance().stopService(intent);
    }
}
