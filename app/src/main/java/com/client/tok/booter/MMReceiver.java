package com.client.tok.booter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.client.tok.db.repository.UserRepository;
import com.client.tok.service.ServiceManager;
import com.client.tok.tox.State;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.NetUtils;

public class MMReceiver {
    private static String TAG = "chatService";

    private static void startService(Intent intent) {
        UserRepository userRepo = State.userRepo();
        LogUtil.i(TAG, "start chatService from action:"
            + intent.getAction()
            + ",useLogin:"
            + userRepo.loggedIn());
        if (userRepo.loggedIn()) {
            ServiceManager.startToxService();
        }
    }

    /**
     * phone boot start receiver
     * it is useful on some phone ,such Google pixel, but SamSung,Oppo,HuaWei
     */
    public static class BootReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.i(TAG, "BootReceiver  action:" + intent.getAction());
            startService(intent);
        }
    }

    /**
     * after android 7.0，not support regist on Manifest.xml,
     */
    public static class NetWorkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.i(TAG, "NetWorkReceiver  action:"
                + intent.getAction()
                + ",network:"
                + NetUtils.isNetworkAvailable());
            if (NetUtils.isNetworkAvailable()) {
                startService(intent);
            }
        }
    }
}
