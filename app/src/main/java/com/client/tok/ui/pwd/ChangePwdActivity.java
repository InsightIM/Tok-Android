package com.client.tok.ui.pwd;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.base.BaseCommonTitleActivity;
import com.client.tok.pagejump.IntentConstants;
import com.client.tok.tox.State;
import com.client.tok.ui.setting.SettingModel;
import com.client.tok.utils.StringUtils;
import com.client.tok.utils.ToastUtils;

public class ChangePwdActivity extends BaseCommonTitleActivity {
    private TextView mUserTv;
    private LinearLayout mOriginPwdLayout;
    private EditText mOriginPwdEt;
    private EditText mNewPwdEt;
    private EditText mRepeatPwdEt;

    private String mUserName;
    private boolean mHasPwd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
        mUserTv = $(R.id.id_user_name_tv);
        mOriginPwdLayout = $(R.id.id_original_pwd_layout);
        mOriginPwdEt = $(R.id.id_original_pwd_et);
        mNewPwdEt = $(R.id.id_new_pwd_et);
        mRepeatPwdEt = $(R.id.id_new_pwd_again_et);

        readIntent();

        mUserTv.setText(mUserName);
        mHasPwd = State.userRepo().hasPwd(mUserName);
        if (mHasPwd) {
            setPageTitle(R.string.change_pwd);
            mOriginPwdLayout.setVisibility(View.VISIBLE);
        } else {
            setPageTitle(R.string.setting_pwd);
            mOriginPwdLayout.setVisibility(View.GONE);
        }
        addKeyboardAction(mRepeatPwdEt);
    }

    private void readIntent() {
        mUserName = getIntent().getStringExtra(IntentConstants.USER_NAME);
        if (StringUtils.isEmpty(mUserName)) {
            mUserName = State.userRepo().getActiveUser();
        }
    }

    @Override
    public int getMenuTxtId() {
        return R.string.done;
    }

    @Override
    public void onMenuClick() {
        submit();
    }

    @Override
    public void onKeyboardAction(TextView textView, int actionId) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            submit();
        }
    }

    private void submit() {
        if (mHasPwd) {
            String pwd = mOriginPwdEt.getText().toString().trim();
            boolean exist = State.userRepo().doesUserExist(mUserName, pwd);
            if (!exist) {
                ToastUtils.show(R.string.pwd_error);
                return;
            }
        }

        String newPwd = mNewPwdEt.getText().toString().trim();
        String repeatPwd = mRepeatPwdEt.getText().toString().trim();
        if (StringUtils.isEmpty(newPwd)) {
            ToastUtils.show(R.string.new_pwd_empty);
            return;
        }
        if (!newPwd.equals(repeatPwd)) {
            ToastUtils.show(R.string.pwd_not_match);
            return;
        }

        int result = State.userRepo().changePwd(mUserName, newPwd);
        if (result > 0) {
            ToastUtils.show(R.string.successful);
            //export profile again
            new SettingModel().exportAccountInfo();
            finish();
        } else {
            ToastUtils.show(R.string.failed);
        }
    }
}
