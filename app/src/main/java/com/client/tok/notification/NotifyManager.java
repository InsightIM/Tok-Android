package com.client.tok.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import com.client.tok.R;
import com.client.tok.TokApplication;
import com.client.tok.pagejump.IntentConstants;
import com.client.tok.ui.chat2.Chat2Activity;
import com.client.tok.ui.contactreqdetail.ContactReqDetailActivity;
import com.client.tok.ui.home.HomeActivity;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.NotificationBadge;
import com.client.tok.utils.PreferenceUtils;
import com.client.tok.utils.StringUtils;

public class NotifyManager {
    private final String TAG = "NotifyManager";
    private Context mContext;
    private static NotifyManager sInstance;
    private NotificationManager mNotificationManager;
    private String mGroupId = "tok";
    private String mGroupName = "tok_group";
    //receive message channel
    public final static String mChannelMsgId = "channel_msg_id";
    private String mChannelMsgName = "channel_msg";
    private String mChannelMsgDes = "Receive Tok Message";

    //service channel
    public final static String mChannelServiceId = "channel_service_id";
    private String mChannelServiceName = "channel_service";
    private String mChannelServiceDes = "Tok Service";

    private int lastBadgeCount;

    private boolean newMsgNotify;
    private boolean newFriendReqNotify;
    private boolean notifyCenter;

    private NotifyManager(Context context) {
        this.mContext = context;
        initNotifyToggle();
    }

    public static NotifyManager getInstance() {
        if (sInstance == null) {
            sInstance = new NotifyManager(TokApplication.getInstance());
        }
        return sInstance;
    }

    public void initNotify(Context context) {
        mNotificationManager =
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotifyChannel();
    }

    private void initNotifyToggle() {
        newMsgNotify = PreferenceUtils.getBoolean(PreferenceUtils.GLOBAL_MSG_NOTIFY, true);
        newFriendReqNotify =
            PreferenceUtils.getBoolean(PreferenceUtils.NEW_FRIEND_REQ_NOTIFY, true);
        notifyCenter = PreferenceUtils.getBoolean(PreferenceUtils.NOTIFY_CENTER, false);
    }

    public void updateNotifyToggle() {
        initNotifyToggle();
    }

    private void createNotifyChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannelGroup(
                    new NotificationChannelGroup(mGroupId, mGroupName));
                NotificationChannel channelMsg =
                    new NotificationChannel(mChannelMsgId, mChannelMsgName,
                        NotificationManager.IMPORTANCE_DEFAULT);
                channelMsg.setDescription(mChannelMsgDes);
                channelMsg.enableLights(true);
                channelMsg.setLightColor(Color.BLUE);
                channelMsg.enableVibration(false);
                channelMsg.setVibrationPattern(new long[] { 100, 200, 300, 400 });
                channelMsg.setShowBadge(true);
                channelMsg.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                channelMsg.setGroup(mGroupId);
                mNotificationManager.createNotificationChannel(channelMsg);

                NotificationChannel channelService =
                    new NotificationChannel(mChannelServiceId, mChannelServiceName,
                        NotificationManager.IMPORTANCE_LOW);
                channelService.setDescription(mChannelServiceDes);
                channelService.enableLights(false);
                channelService.enableVibration(false);
                channelService.setShowBadge(false);
                channelService.setSound(null, null);
                channelService.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                channelService.setGroup(mGroupId);
                mNotificationManager.createNotificationChannel(channelService);
            }
        }
    }

    public Notification getServiceNotification(Context context) {
        Notification.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(context, mChannelServiceId);
        } else {
            builder = new Notification.Builder(context);
        }
        PendingIntent pendingIntent =
            PendingIntent.getActivity(context, 0, new Intent(context, HomeActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = builder.setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(StringUtils.getTextFromResId(R.string.service_notify_title))
            .setContentText(StringUtils.getTextFromResId(R.string.service_notify_content))
            .setContentIntent(pendingIntent)
            .build();
        notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONGOING_EVENT;
        return notification;
    }

    private Notification.Builder getMsgNotificationBuilder() {
        Notification.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(mContext, mChannelMsgId);
        } else {
            builder = new Notification.Builder(mContext);
        }
        return builder;
    }

    public void createFriendReqNotify(String reqKey, String content, int count) {
        if (newFriendReqNotify) {
            Intent intent = new Intent(mContext, ContactReqDetailActivity.class);
            intent.putExtra(IntentConstants.REQ_FRIEND_KEY, reqKey);
            if (!notifyCenter) {
                content = "";
            }
            createBaseNotify(intent, StringUtils.getTextFromResId(R.string.new_friends_request),
                content, generateNotifyId(reqKey));
        }
        setBadge(count);
    }

    public void createMsgNotify(String chatType, String contactKey, String sendName, String content,
        int count) {
        if (newMsgNotify) {
            Intent intent = new Intent(mContext, Chat2Activity.class);
            intent.putExtra(IntentConstants.PK, contactKey);
            intent.putExtra(IntentConstants.FROM_NOTIFICATION, true);
            intent.putExtra(IntentConstants.CHAT_TYPE, chatType);
            if (!notifyCenter) {
                sendName = StringUtils.getTextFromResId(R.string.new_message);
                content = "";
            }
            createBaseNotify(intent, sendName, content, generateNotifyId(contactKey));
        }
        setBadge(count);
    }

    private void createBaseNotify(Intent intent, String title, String content, int notifyId) {
        Notification.Builder builder =
            getMsgNotificationBuilder().setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setOngoing(false);
        if (!StringUtils.isEmpty(content)) {
            builder.setContentText(content);
        }
        PendingIntent pendingIntent =
            PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        mNotificationManager.notify(notifyId, notification);
    }

    public void setBadge(final int count) {
        if (lastBadgeCount == count) {
            return;
        }
        lastBadgeCount = count;
        NotificationBadge.applyCount(count);
    }

    public int generateNotifyId(String key) {
        return key.hashCode();
    }

    /**
     * clear notification
     *
     * @param id notification id,if id==1,it is not useful
     */
    public void cleanNotify(int id) {
        if (mNotificationManager != null) {
            mNotificationManager.cancel(id);
            LogUtil.i(TAG, "clean notify id:" + id);
        }
    }

    public void cleanAllNotify() {
        if (mNotificationManager != null) {
            mNotificationManager.cancelAll();
            LogUtil.i(TAG, "clean notify all");
        }
    }
}
