package com.client.tok.ui.login.home;

import com.client.tok.ui.basecontract.BaseContract;

public class LoginSignUpContract {
    public interface ILoginSignUpView extends BaseContract.IBaseView<ILoginSignUpPresenter> {
        void showLoginSignUpView();

        void hideLoginSignUpView();

        void showImportInput(String userName, boolean encrypt);

        void showImportFail(String reason);
    }

    public interface ILoginSignUpPresenter extends BaseContract.IBasePresenter {

        void importInfo(String userName, String pwd);

        void importAccountProfile(String filePath);

        void destroy();
    }
}
