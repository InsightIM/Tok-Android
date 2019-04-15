package com.client.tok.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.util.concurrent.CountDownLatch;

public class DispatchQueue extends Thread {
    private String TAG = "DispatchQueue";
    private Handler handler;
    private CountDownLatch syncLatch = new CountDownLatch(1);

    public DispatchQueue(String threadName) {
        setName(threadName);
        start();
    }

    public void sendMessage(Message msg, int delay) {
        try {
            syncLatch.await();
            if (handler != null) {
                if (delay <= 0) {
                    handler.sendMessage(msg);
                } else {
                    handler.sendMessageDelayed(msg, delay);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelRunnable(Runnable runnable) {
        try {
            syncLatch.await();
            if (handler != null) {
                handler.removeCallbacks(runnable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void postRunnable(Runnable runnable, long delay) {
        try {
            syncLatch.await();
            if (handler != null) {
                if (delay <= 0) {
                    handler.post(runnable);
                } else {
                    handler.postDelayed(runnable, delay);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleMessage(Message inputMessage) {

    }

    @Override
    public void run() {
        super.run();
        Looper.prepare();
        if (handler == null) {
            handler = new QueueHandler(this);
        }
        syncLatch.countDown();
        Looper.loop();
    }

    private static class QueueHandler extends Handler {
        private DispatchQueue dispatchQueue;

        public QueueHandler(DispatchQueue dispatchQueue) {
            this.dispatchQueue = dispatchQueue;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (dispatchQueue != null) {
                dispatchQueue.handleMessage(msg);
            }
        }
    }

    public void cleanupQueue() {
        try {
            syncLatch.await();
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
            }
            handler = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
