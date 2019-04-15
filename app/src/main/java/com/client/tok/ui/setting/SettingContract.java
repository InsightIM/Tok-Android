package com.client.tok.ui.setting;

import com.client.tok.ui.basecontract.BaseContract;

public class SettingContract {
    public interface ISettingView extends BaseContract.IBaseView<ISettingPresenter> {
        void showExportSuccess(String msg, String folder);

        void showPwdPrompt(boolean hasPwd, int resId);

        void showExportFail(String msg);

        void showWarning(String msg);

        void showMsg(String msg);

        void showLoading();

        void hideLoading();
    }

    public interface ISettingPresenter extends BaseContract.IBasePresenter {
        void exportAccountInfo();

        void clearMsgLogout(boolean enable);

        void setNospam();

        void clearMsgHistory();

        void delProfile();

        void logout();

        void onDestroy();
    }
}
