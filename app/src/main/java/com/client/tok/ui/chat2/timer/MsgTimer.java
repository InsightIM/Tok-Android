package com.client.tok.ui.chat2.timer;

import com.client.tok.db.repository.InfoRepository;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.tox.State;
import com.client.tok.utils.LogUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * message time out
 */
public class MsgTimer {
    private static String TAG = "msgTimer";
    private static Map<Long, Timer> timerMap;

    /**
     * add timer
     *
     * @param receiptId message id
     */
    public static void startTimer(final long receiptId) {
        if (timerMap == null) {
            timerMap = new HashMap<>();
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InfoRepository infoRepo = State.infoRepo();
                infoRepo.setMessageFailByReceiptId(receiptId);
                timerMap.remove(receiptId);
                LogUtil.i(TAG, "time out:" + timerMap.size() + ",receiptId:" + receiptId);
            }
        }, GlobalParams.MSG_OUT_TIME);
        timerMap.put(receiptId, timer);
        LogUtil.i(TAG, "add timer:" + timerMap.size() + ",receiptId:" + receiptId);
    }

    /**
     * stop timer
     *
     * @param receiptId message id
     */
    public static void stopTimer(long receiptId) {
        if (timerMap != null) {
            Timer timer = timerMap.get(receiptId);
            if (timer != null) {
                timer.cancel();
                timerMap.remove(receiptId);
                LogUtil.i(TAG, "stop timer:" + timerMap.size() + ",receiptId:" + receiptId);
            }
        }
    }
}
