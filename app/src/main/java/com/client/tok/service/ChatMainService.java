package com.client.tok.service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.client.tok.TokApplication;
import com.client.tok.msg.callbacks.ToxCallbackListener;
import com.client.tok.tox.ToxManager;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.NetUtils;
import java.util.Timer;
import java.util.TimerTask;

public class ChatMainService extends Service {
    private String TAG = "chatService";
    private static final int SERVICE_ID = 1;
    public static boolean isRunning;
    private Context context;
    private Thread serviceThread;
    private boolean keepRunning = true;

    private Timer timer;
    private TimerTask task;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, "chatMainService onCreate");
        context = TokApplication.getInstance();
        keepRunning = true;
        Runnable start = new Runnable() {
            @Override
            public void run() {
                ToxManager.getManager().initTox(TokApplication.getInstance());
                final ToxCallbackListener toxCallbackListener = new ToxCallbackListener(context);
                timer = new Timer(true);
                isRunning = true;
                try {
                    task = new TimerTask() {
                        public void run() {
                            if (keepRunning) {
                                if (NetUtils.isNetworkAvailable()) {
                                    if (keepRunning) {
                                        LogUtil.i(TAG,
                                            "coreManager:" + ToxManager.getManager().hashCode());
                                        ToxManager.getManager().iterate(toxCallbackListener);
                                    }
                                }
                            } else {
                                stopTimer();
                            }
                        }
                    };
                } catch (Exception e) {
                    e.printStackTrace();
                }

                timer.scheduleAtFixedRate(task, 200, 200);
            }
        };

        serviceThread = new Thread(start);
        serviceThread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.i(TAG, "ChatMainService onStartCommand");
        startGrayService();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void stopTimer() {
        keepRunning = false;
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
        isRunning = false;
        ToxManager.getManager().saveAndClose();
        LogUtil.i(TAG, "Chat Service onDestroy");
    }

    private void startGrayService() {
        startForeground(SERVICE_ID, new Notification());
        Intent innerIntent = new Intent(this, GrayInnerService.class);
        startService(innerIntent);
    }

    public static class GrayInnerService extends Service {
        private String TAG = "GrayInnerService";

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            LogUtil.i(TAG, "GrayInnerService onStartCommand");
            startForeground(SERVICE_ID, new Notification());
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            LogUtil.i(TAG, "GrayInnerService onDestroy");
        }
    }
}
