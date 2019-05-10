package com.client.tok.ui.login.login;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.base.BaseCommonTitleActivity;
import com.client.tok.bean.UserInfo;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.utils.StringUtils;
import com.client.tok.utils.SystemUtils;
import com.client.tok.utils.ToastUtils;
import com.client.tok.widget.TextInputDrawableEt;
import java.util.List;

public class LoginActivity extends BaseCommonTitleActivity
    implements LoginContract.ILoginView, View.OnClickListener {
    private String TAG = "LoginActivity";
    private TextInputLayout mUserNameTil;
    private TextInputDrawableEt mUsernameEt;
    private TextInputEditText mPwdEt;
    private Button mLoginBt;

    private LoginContract.ILoginPresenter mLoginPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        new LoginPresenter(this);
        mLoginPresenter.start();
    }

    private void initView() {
        mUserNameTil = $(R.id.id_login_name_til);
        mUsernameEt = $(R.id.id_login_name_et);
        mPwdEt = $(R.id.id_login_pwd_et);
        mLoginBt = $(R.id.id_login_bt);
        addKeyboardAction(mPwdEt);
        mUsernameEt.setOnDrawableRightListener(new TextInputDrawableEt.OnDrawableRightListener() {
            @Override
            public void onDrawableRightClick() {
                final List<UserInfo> userList = mLoginPresenter.getUserList();
                if (userList != null && userList.size() > 0) {
                    final PopupWindow popupWindow =
                        LoginUserPopWindow.showLoginUsers(LoginActivity.this, userList,
                            new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                                    mUsernameEt.setText(userList.get(position).getProfileName());
                                    mUsernameEt.setSelection(mUsernameEt.getText().length());
                                }
                            });
                    popupWindow.showAsDropDown(mUsernameEt);
                }
            }
        });
        mLoginBt.setOnClickListener(this);
        addEditListener();
    }

    @Override
    public int getTitleId() {
        return R.string.login;
    }

    @Override
    public void onKeyboardAction(TextView textView, int actionId) {
        if (actionId == EditorInfo.IME_ACTION_GO) {
            mLoginPresenter.login(mUsernameEt.getText().toString().trim(),
                mPwdEt.getText().toString().trim());
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_login_bt:
                if (mUserNameTil.getError() == null) {
                    mLoginPresenter.login(mUsernameEt.getText().toString().trim(),
                        mPwdEt.getText().toString().trim());
                } else {
                    showLoginFail(mUserNameTil.getError());
                }
                break;
        }
    }

    private void addEditListener() {
        mUsernameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mLoginBt.setEnabled(s.toString().length() > 0);
                if (s.toString().length() > GlobalParams.USER_NAME_MAX_LENGTH) {
                    mUserNameTil.setError(
                        StringUtils.formatTxFromResId(R.string.input_exceed_length,
                            GlobalParams.USER_NAME_MAX_LENGTH));
                } else {
                    mUserNameTil.setError(null);
                }
            }
        });
    }

    @Override
    public void setPresenter(LoginContract.ILoginPresenter iLoginPresenter) {
        this.mLoginPresenter = iLoginPresenter;
    }

    @Override
    public void showExistUser(String userName) {
        mUsernameEt.setText(userName);
    }

    @Override
    public void showLoginFail(CharSequence reason) {
        ToastUtils.showLong(reason);
    }

    @Override
    public void showImporting() {

    }

    @Override
    public void hideImport() {

    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void viewDestroy() {
        finish();
    }

}
