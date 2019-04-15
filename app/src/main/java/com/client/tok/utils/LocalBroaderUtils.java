package com.client.tok.utils;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import com.client.tok.TokApplication;
import com.client.tok.pagejump.IntentConstants;
import java.io.Serializable;

public class LocalBroaderUtils {

    public static void sendLocalBroadcast(String action) {
        sendLocalBroadcast(action, null);
    }

    public static void sendLocalBroadcast(String action, Serializable data) {
        if (!StringUtils.isEmpty(action)) {
            Intent intent = new Intent(action);
            if (data != null) {
                intent.putExtra(IntentConstants.NOTIFY_DATA, data);
            }
            LocalBroadcastManager.getInstance(TokApplication.getInstance())
                .sendBroadcastSync(intent);
        }
    }

    public static void registerLocalReceiver(BroadcastReceiver receiver, String... actions) {
        if (receiver != null && actions.length > 0) {
            IntentFilter filter = new IntentFilter();
            for (String action : actions) {
                filter.addAction(action);
            }
            LocalBroadcastManager.getInstance(TokApplication.getInstance())
                .registerReceiver(receiver, filter);
        }
    }

    public static void unRegisterReceiver(BroadcastReceiver receiver) {
        if (receiver != null) {
            LocalBroadcastManager.getInstance(TokApplication.getInstance())
                .unregisterReceiver(receiver);
        }
    }
}
