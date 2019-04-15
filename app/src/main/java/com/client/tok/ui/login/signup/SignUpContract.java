package com.client.tok.ui.login.signup;

import com.client.tok.ui.basecontract.BaseContract;

public class SignUpContract {
    public interface ISignUpView extends BaseContract.IBaseView<ISignUpPresenter> {

        void showSignUpFail(CharSequence reason);
    }

    public interface ISignUpPresenter extends BaseContract.IBasePresenter {
        void signUp(String userName,String pwd);

        void goToLogin();
    }
}
