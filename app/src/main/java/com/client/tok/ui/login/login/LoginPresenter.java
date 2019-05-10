package com.client.tok.ui.login.login;

import android.os.Handler;
import com.client.tok.R;
import com.client.tok.bean.UserInfo;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.pagejump.PageJumpIn;
import com.client.tok.service.ServiceManager;
import com.client.tok.tox.State;
import com.client.tok.utils.LocalBroaderUtils;
import com.client.tok.utils.StringUtils;
import java.util.List;

public class LoginPresenter implements LoginContract.ILoginPresenter {
    private String TAG = "LoginPresenter";
    private LoginContract.ILoginView mLoginView;

    public LoginPresenter(LoginContract.ILoginView iLoginView) {
        mLoginView = iLoginView;
        mLoginView.setPresenter(this);
    }

    @Override
    public void start() {
        mLoginView.showExistUser(getUserInfo());
    }

    private String getUserInfo() {
        List<UserInfo> userList = getUserList();
        if (userList != null && userList.size() > 0) {
            return userList.get(0).getProfileName();
        }
        return "";
    }

    @Override
    public void login(String userName, String pwd) {
        if (StringUtils.isEmpty(userName)) {
            mLoginView.showLoginFail(StringUtils.getTextFromResId(R.string.input_user_name_prompt));
            return;
        }
        if (State.userRepo().doesUserExist(userName, pwd)) {
            State.login(userName);
            loginSuccess();
        } else {
            mLoginView.showLoginFail(
                StringUtils.formatTxFromResId(R.string.user_or_pwd_error, userName));
        }
    }

    @Override
    public List<UserInfo> getUserList() {
        return State.userRepo().getAllUser();
    }

    private void loginSuccess() {
        ServiceManager.startToxService();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PageJumpIn.jumpHomePage(mLoginView.getActivity());
                mLoginView.viewDestroy();
                LocalBroaderUtils.sendLocalBroadcast(GlobalParams.ACTION_LOGIN_SUCCESS, null);
            }
        }, GlobalParams.DELAY_ENTER_HOME);
    }
}
