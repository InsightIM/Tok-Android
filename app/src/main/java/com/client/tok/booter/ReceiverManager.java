package com.client.tok.booter;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import com.client.tok.TokApplication;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.StringUtils;

public class ReceiverManager {
    private static String TAG = "receiverManager";

    public static void registerNetworkReceiver() {
        LogUtil.i(TAG, "register network receiver,sdk:" + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            MMReceiver.NetWorkReceiver receiver = new MMReceiver.NetWorkReceiver();
            registerReceiver(ConnectivityManager.CONNECTIVITY_ACTION, receiver);
        }
    }

    public static void registerReceiver(String action, BroadcastReceiver receiver) {
        if (!StringUtils.isEmpty(action)) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(action);
            TokApplication.getInstance().registerReceiver(receiver, intentFilter);
        }
    }

    public static void unRegisterReceiver(BroadcastReceiver receiver) {
        if (receiver != null) {
            TokApplication.getInstance().unregisterReceiver(receiver);
        }
    }
}
