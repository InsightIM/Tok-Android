package com.client.tok.ui.login.signup;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.base.BaseCommonTitleActivity;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.utils.StringUtils;
import com.client.tok.utils.SystemUtils;
import com.client.tok.utils.ToastUtils;

public class SignUpActivity extends BaseCommonTitleActivity
    implements SignUpContract.ISignUpView, View.OnClickListener {
    private TextInputLayout mUsernameTil;
    private TextInputEditText mUsernameEt;
    private TextInputLayout mPwdTil;
    private TextInputEditText mPwdEt;
    private TextInputEditText mRepeatPwdEt;
    private Button mSignUpBt;

    private SignUpContract.ISignUpPresenter mLoginPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initView();
        initPresenter();
        mLoginPresenter.start();
    }

    private void initView() {
        mUsernameTil = $(R.id.id_signup_name_til);
        mUsernameEt = $(R.id.id_signup_name_et);

        mPwdTil = $(R.id.id_signup_pwd_til);
        mPwdEt = $(R.id.id_signup_pwd_et);

        mRepeatPwdEt = $(R.id.id_signup_confirm_pwd_et);
        mSignUpBt = $(R.id.id_signup_create_bt);
        addKeyboardAction(mRepeatPwdEt);

        mSignUpBt.setOnClickListener(this);
        addEditListener();
    }

    private void initPresenter() {
        new SignUpPresenter(this);
    }

    @Override
    public int getTitleId() {
        return R.string.create_account;
    }

    private void addEditListener() {
        mUsernameEt.addTextChangedListener(new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                mSignUpBt.setEnabled(isBtnEnable());
                if (s.toString().length() > GlobalParams.USER_NAME_MAX_LENGTH) {
                    mUsernameTil.setError(
                        StringUtils.formatTxFromResId(R.string.input_exceed_length,
                            GlobalParams.USER_NAME_MAX_LENGTH));
                } else {
                    mUsernameTil.setError(null);
                }
            }
        });

        mPwdEt.addTextChangedListener(new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                mSignUpBt.setEnabled(isBtnEnable());
            }
        });

        mRepeatPwdEt.addTextChangedListener(new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                mSignUpBt.setEnabled(isBtnEnable());
            }
        });
    }

    private boolean isBtnEnable() {
        int nameLength = mUsernameEt.getText().toString().trim().length();
        boolean nameOk = nameLength > 0 && nameLength < GlobalParams.USER_NAME_MAX_LENGTH;
        String pwd = mPwdEt.getText().toString().trim();
        String repeatPwd = mRepeatPwdEt.getText().toString().trim();
        boolean pwdOk =
            !StringUtils.isEmpty(pwd) && !StringUtils.isEmpty(repeatPwd) && pwd.equals(repeatPwd);
        return nameOk && pwdOk;
    }

    @Override
    public void onKeyboardAction(TextView textView, int actionId) {
        if (actionId == EditorInfo.IME_ACTION_GO) {
            mLoginPresenter.signUp(mUsernameEt.getText().toString().trim(),
                mPwdEt.getText().toString().trim());
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_signup_create_bt:
                if (mUsernameTil.getError() == null) {
                    mLoginPresenter.signUp(mUsernameEt.getText().toString().trim(),
                        mPwdEt.getText().toString().trim());
                } else {
                    showSignUpFail(mUsernameTil.getError());
                }
                break;
        }
    }

    @Override
    public void setPresenter(SignUpContract.ISignUpPresenter iLoginPresenter) {
        this.mLoginPresenter = iLoginPresenter;
    }

    @Override
    public void showSignUpFail(CharSequence reason) {
        ToastUtils.showLong(reason);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void viewDestroy() {
        finish();
    }

    private class BaseTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

}
