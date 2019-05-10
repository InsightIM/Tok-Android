package com.client.tok.ui.setting;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;
import com.client.tok.R;
import com.client.tok.base.BaseCommonTitleActivity;
import com.client.tok.pagejump.PageJumpIn;
import com.client.tok.utils.FilePicker;
import com.client.tok.utils.LoadingUtil;
import com.client.tok.utils.PreferenceUtils;
import com.client.tok.utils.StringUtils;
import com.client.tok.utils.ToastUtils;
import com.client.tok.widget.ItemInfoView;
import com.client.tok.widget.dialog.DialogFactory;

public class SettingActivity extends BaseCommonTitleActivity
    implements SettingContract.ISettingView, View.OnClickListener {
    private String TAG = "SettingActivity";
    private ItemInfoView mChangePwdIiv;
    private ItemInfoView mExportFileIiv;

    private ItemInfoView mResetIdIiv;
    private ItemInfoView mNotifyIiv;

    private ItemInfoView mAutoReceiveFileIiv;

    private ItemInfoView mClearChatLogoutIiv;
    private ItemInfoView mClearChatIiv;
    private ItemInfoView mDelProfileIiv;
    private Button mLogoutBtn;

    private SettingContract.ISettingPresenter mSettingPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        new SettingPresenter(this);
    }

    public void initView() {
        mChangePwdIiv = $(R.id.id_setting_change_pwd_iiv);
        mChangePwdIiv.setOnClickListener(this);
        mExportFileIiv = $(R.id.id_setting_export_file_iiv);
        mExportFileIiv.setOnClickListener(this);

        mResetIdIiv = $(R.id.id_setting_reset_id_iiv);
        mResetIdIiv.setOnClickListener(this);
        mNotifyIiv = $(R.id.id_setting_notify_iiv);
        mNotifyIiv.setOnClickListener(this);

        mAutoReceiveFileIiv = $(R.id.id_setting_auto_file_iiv);
        mAutoReceiveFileIiv.setToggleEnable(
            PreferenceUtils.getBoolean(PreferenceUtils.AUTO_RECEIVE_FILE, true));

        mClearChatLogoutIiv = $(R.id.id_setting_clear_chat_logout_iiv);
        mClearChatLogoutIiv.setToggleEnable(
            PreferenceUtils.getBoolean(PreferenceUtils.CLEAR_MSG_LOGOUT, false));
        mClearChatLogoutIiv.setToggleListener(this);
        mClearChatIiv = $(R.id.id_setting_clear_chat_iiv);
        mClearChatIiv.setOnClickListener(this);
        mDelProfileIiv = $(R.id.id_setting_del_profile_iiv);
        mDelProfileIiv.setOnClickListener(this);
        mLogoutBtn = $(R.id.id_setting_logout_btn);
        mLogoutBtn.setOnClickListener(this);
    }

    @Override
    public int getTitleId() {
        return R.string.setting;
    }

    @Override
    public void setPresenter(SettingContract.ISettingPresenter iSettingPresenter) {
        mSettingPresenter = iSettingPresenter;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void viewDestroy() {
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSettingPresenter != null) {
            mSettingPresenter.onDestroy();
            mSettingPresenter = null;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_setting_change_pwd_iiv:
                PageJumpIn.jumpChangePwdPage(getActivity(), null);
                break;
            case R.id.id_setting_export_file_iiv:
                mSettingPresenter.exportAccountInfo();
                break;

            case R.id.id_setting_reset_id_iiv:
                DialogFactory.show2BtnDialog(this,
                    StringUtils.getTextFromResId(R.string.reset_tox_id),
                    StringUtils.getTextFromResId(R.string.reset_tox_id_message),
                    StringUtils.getTextFromResId(R.string.reset), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mSettingPresenter.setNospam();
                        }
                    });
                break;
            case R.id.id_setting_notify_iiv:
                PageJumpIn.jumpSetNotificationPage(this);
                break;
            case R.id.id_setting_auto_file_iiv:
                if (v instanceof ToggleButton) {
                    ToggleButton tb = (ToggleButton) v;
                    PreferenceUtils.saveBoolean(PreferenceUtils.CLEAR_MSG_LOGOUT, tb.isChecked());
                }
                break;
            case R.id.id_info_item_tb:
                if (v instanceof ToggleButton) {
                    ToggleButton tb = (ToggleButton) v;
                    mSettingPresenter.clearMsgLogout(tb.isChecked());
                }
                break;
            case R.id.id_setting_clear_chat_iiv:
                DialogFactory.showPromptDialog(this,
                    StringUtils.getTextFromResId(R.string.clear_msg_confirm_prompt),
                    StringUtils.getTextFromResId(R.string.delete),
                    StringUtils.getTextFromResId(R.string.cancel), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mSettingPresenter.clearMsgHistory();
                        }
                    });
                break;
            case R.id.id_setting_del_profile_iiv:
                DialogFactory.showPromptDialog(this,
                    StringUtils.getTextFromResId(R.string.del_profile_confirm_prompt),
                    StringUtils.getTextFromResId(R.string.delete),
                    StringUtils.getTextFromResId(R.string.cancel), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mSettingPresenter.delProfile();
                        }
                    });
                break;
            case R.id.id_setting_logout_btn:
                mSettingPresenter.logout();
                break;
        }
    }

    @Override
    public void showExportSuccess(String msg, final String folder) {
        DialogFactory.showPromptDialog(this, msg, StringUtils.getTextFromResId(R.string.check),
            StringUtils.getTextFromResId(R.string.ok), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FilePicker.openFolder(SettingActivity.this, folder);
                }
            });
    }

    @Override
    public void showPwdPrompt(boolean hasPwd, int pwdPrompt) {
        mChangePwdIiv.setPrompt(pwdPrompt);
    }

    @Override
    public void showExportFail(String msg) {
        DialogFactory.showPromptDialog(this, msg, StringUtils.getTextFromResId(R.string.ok), null,
            null);
    }

    @Override
    public void showWarning(String msg) {
        DialogFactory.showPromptDialog(this, msg, StringUtils.getTextFromResId(R.string.ok), null,
            null);
    }

    @Override
    public void showMsg(String msg) {
        ToastUtils.show(msg);
    }

    @Override
    public void showLoading() {
        LoadingUtil.show(this);
    }

    @Override
    public void hideLoading() {
        LoadingUtil.dismiss();
    }
}