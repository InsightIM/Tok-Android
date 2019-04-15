package com.client.tok.ui.login.login;

import com.client.tok.bean.UserInfo;
import com.client.tok.ui.basecontract.BaseContract;
import java.util.List;

public class LoginContract {
    public interface ILoginView extends BaseContract.IBaseView<ILoginPresenter> {
        void showExistUser(String userName);

        void showLoginFail(CharSequence reason);

        void showImporting();

        void hideImport();
    }

    public interface ILoginPresenter extends BaseContract.IBasePresenter {
        void login(String userName,String pwd);

        List<UserInfo> getUserList();
    }
}
