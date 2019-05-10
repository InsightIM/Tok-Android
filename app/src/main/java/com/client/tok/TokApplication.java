package com.client.tok;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatDelegate;
import com.client.tok.booter.KeepAliveJobService;
import com.client.tok.notification.NotifyManager;
import com.client.tok.ui.imgzoom.ImgZoomManager;
import com.client.tok.utils.LogUtil;
import java.util.ArrayList;
import java.util.List;

public class TokApplication extends MultiDexApplication
    implements Application.ActivityLifecycleCallbacks {
    private String TAG = "TokApplication";
    private static TokApplication instance;
    private Handler handler;
    private int activityCounter = 0;
    private List<Activity> activities = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        handler = new Handler(this.getMainLooper());
        initImgZoomLoader();
        initNotification();
        registerMultiReceiver();
        registerActivityLifecycleCallbacks(this);
        enableVectorDrawable();
        //initKeepAliveService();
        LogUtil.i(TAG, "Tok application onCreate");
    }

    public static TokApplication getInstance() {
        return instance;
    }

    public Handler getHandler() {
        return handler;
    }

    //TODO ??
    private void enableVectorDrawable() {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private void initNotification() {
        NotifyManager.getInstance().initNotify(this);
    }

    private void initImgZoomLoader() {
        ImgZoomManager.init();
    }

    public void finishOpenedActivities() {
        for (Activity activity : activities) {
            activity.finish();
        }
        activities.clear();
    }

    private void registerMultiReceiver() {
        //ReceiverManager.registerNetworkReceiver();
    }

    private void initKeepAliveService() {
        KeepAliveJobService.startKeepAliveService();
    }

    // 遍历所有Activity并finish
    public void exit() {
        finishOpenedActivities();
    }

    public boolean isAppForeground() {
        return activityCounter > 0;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        activities.add(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        activityCounter++;
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        activityCounter--;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        activities.remove(activity);
        LogUtil.i(TAG, "activity destroy:" + activity.getLocalClassName());
    }
}
