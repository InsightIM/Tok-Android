package com.client.tok.booter;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.client.tok.TokApplication;
import com.client.tok.db.repository.UserRepository;
import com.client.tok.service.ServiceManager;
import com.client.tok.tox.State;
import com.client.tok.utils.LogUtil;

/**
 * TODO to keep our app alive,but has problem
 */
public class KeepAliveJobService extends JobService {
    private String TAG = "KeepAliveJobService";

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, "onCreate");
        scheduleJob();
    }

    private void scheduleJob() {
        int timeDelay = 2000;
        int id = 100;
        JobInfo.Builder builder =
            new JobInfo.Builder(id, new ComponentName(getApplication(), KeepAliveJobService.class));
        if (Build.VERSION.SDK_INT >= 24) {
            builder.setMinimumLatency(timeDelay);
            builder.setOverrideDeadline(timeDelay);
            builder.setMinimumLatency(timeDelay);
            builder.setBackoffCriteria(timeDelay, JobInfo.BACKOFF_POLICY_LINEAR);
        } else {
            builder.setPeriodic(timeDelay);
        }
        builder.setPersisted(true);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setRequiresCharging(false);
        JobInfo info = builder.build();
        JobScheduler jobScheduler =
            (JobScheduler) this.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(id);
        jobScheduler.schedule(info);
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        LogUtil.i(TAG, "onStartJob");
        scheduleJob();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        LogUtil.i(TAG, "onStopJob");
        UserRepository userRepo = State.userRepo();
        if (userRepo.loggedIn()) {
            ServiceManager.startToxService();
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "onDestroyKeepAliveJob");
    }

    public static void startKeepAliveService() {
        Intent intent = new Intent(TokApplication.getInstance(), KeepAliveJobService.class);
        TokApplication.getInstance().startService(intent);
    }
}
