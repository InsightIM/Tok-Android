package com.client.tok.ui.setting.notify;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ToggleButton;
import com.client.tok.R;
import com.client.tok.base.BaseCommonTitleActivity;
import com.client.tok.notification.NotifyManager;
import com.client.tok.utils.PreferenceUtils;
import com.client.tok.widget.ItemInfoView;

public class NotifySetActivity extends BaseCommonTitleActivity {
    private ItemInfoView newMsgIIv;
    private ItemInfoView newFriendReqIIv;
    private ItemInfoView msgCenterIIv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_setting);

        newMsgIIv = $(R.id.id_set_new_msg_iiv);
        newMsgIIv.setToggleListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof ToggleButton) {
                    ToggleButton tb = (ToggleButton) v;
                    PreferenceUtils.saveBoolean(PreferenceUtils.GLOBAL_MSG_NOTIFY, tb.isChecked());
                    NotifyManager.getInstance().updateNotifyToggle();
                    msgCenterIIv.setVisibility(tb.isChecked() ? View.VISIBLE : View.GONE);
                }
            }
        });

        newFriendReqIIv = $(R.id.id_set_friend_req_iiv);
        newFriendReqIIv.setToggleListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof ToggleButton) {
                    ToggleButton tb = (ToggleButton) v;
                    PreferenceUtils.saveBoolean(PreferenceUtils.NEW_FRIEND_REQ_NOTIFY,
                        tb.isChecked());
                    NotifyManager.getInstance().updateNotifyToggle();
                }
            }
        });

        msgCenterIIv = $(R.id.id_set_center_iiv);
        msgCenterIIv.setToggleListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof ToggleButton) {
                    ToggleButton tb = (ToggleButton) v;
                    PreferenceUtils.saveBoolean(PreferenceUtils.NOTIFY_CENTER, tb.isChecked());
                    NotifyManager.getInstance().updateNotifyToggle();
                }
            }
        });
        init();
    }

    private void init() {
        boolean newMsgNotify = PreferenceUtils.getBoolean(PreferenceUtils.GLOBAL_MSG_NOTIFY, true);
        newMsgIIv.setToggleEnable(newMsgNotify);
        newFriendReqIIv.setToggleEnable(
            PreferenceUtils.getBoolean(PreferenceUtils.NEW_FRIEND_REQ_NOTIFY, true));
        msgCenterIIv.setToggleEnable(
            PreferenceUtils.getBoolean(PreferenceUtils.NOTIFY_CENTER, false));

        msgCenterIIv.setVisibility(newMsgNotify ? View.VISIBLE : View.GONE);
    }
}
