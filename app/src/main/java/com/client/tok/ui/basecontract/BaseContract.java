package com.client.tok.ui.basecontract;

import android.app.Activity;
import android.arch.lifecycle.LifecycleOwner;

public class BaseContract {
    public interface IBaseView<T extends IBasePresenter> extends LifecycleOwner {
        void setPresenter(T t);

        Activity getActivity();

        void viewDestroy();
    }

    public interface IBasePresenter {
        void start();
    }
}
