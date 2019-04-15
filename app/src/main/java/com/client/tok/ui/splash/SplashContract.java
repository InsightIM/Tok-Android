package com.client.tok.ui.splash;

import com.client.tok.ui.basecontract.BaseContract;


public class SplashContract {
    public interface ISplashView extends BaseContract.IBaseView<ISplashPresenter> {
        void showGuideView();
    }

    public interface ISplashPresenter extends BaseContract.IBasePresenter {
        void enter();
    }
}
