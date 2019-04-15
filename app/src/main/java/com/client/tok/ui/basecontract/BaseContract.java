package com.client.tok.ui.basecontract;

import android.app.Activity;

public class BaseContract {
    public interface IBaseView<T extends IBasePresenter> {
        void setPresenter(T t);

        Activity getActivity();

        void viewDestroy();
    }

    public interface IBasePresenter {
        void start();
    }
}
