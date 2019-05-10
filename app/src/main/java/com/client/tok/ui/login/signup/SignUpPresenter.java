package com.client.tok.ui.login.signup;

import android.os.Handler;
import com.client.tok.R;
import com.client.tok.db.repository.UserRepository;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.pagejump.PageJumpIn;
import com.client.tok.service.ServiceManager;
import com.client.tok.tox.State;
import com.client.tok.ui.login.login.UserModel;
import com.client.tok.utils.LocalBroaderUtils;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.StringUtils;

public class SignUpPresenter implements SignUpContract.ISignUpPresenter {
    private String TAG = "LoginPresenter";
    private SignUpContract.ISignUpView mSignUpView;
    private UserRepository mUserRepo = State.userRepo();
    private UserModel mUserModel = new UserModel();

    public SignUpPresenter(SignUpContract.ISignUpView iLoginView) {
        mSignUpView = iLoginView;
        mSignUpView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void signUp(String userName, String pwd) {
        if (StringUtils.isEmpty(userName)) {
            mSignUpView.showSignUpFail(
                StringUtils.getTextFromResId(R.string.input_user_name_prompt));
            return;
        }
        if (StringUtils.isEmpty(pwd)) {
            mSignUpView.showSignUpFail(
                StringUtils.getTextFromResId(R.string.input_user_pwd_prompt));
            return;
        }
        if (!mUserRepo.doesUserExist(userName)) {
            boolean result = mUserModel.createUser(userName, pwd, true, false);
            if (result) {
                signUpSuccess();
            } else {
                signUpFail();
            }
        } else {
            mSignUpView.showSignUpFail(
                StringUtils.formatTxFromResId(R.string.user_has_exist, userName));
        }
    }

    @Override
    public void goToLogin() {
        PageJumpIn.jumpLoginPage(mSignUpView.getActivity());
        mSignUpView.viewDestroy();
    }

    private void signUpSuccess() {
        ServiceManager.startToxService();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSignUpView.viewDestroy();
                LocalBroaderUtils.sendLocalBroadcast(GlobalParams.ACTION_LOGIN_SUCCESS, null);
                PageJumpIn.jumpHomePage(mSignUpView.getActivity());
            }
        }, GlobalParams.DELAY_ENTER_HOME);
    }

    public void signUpFail() {
        LogUtil.e(TAG, "Create Account Failed");
        mSignUpView.showSignUpFail(StringUtils.getTextFromResId(R.string.sign_up_failed));
    }
}
