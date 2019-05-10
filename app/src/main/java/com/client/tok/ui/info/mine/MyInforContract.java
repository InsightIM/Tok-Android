package com.client.tok.ui.info.mine;

import android.arch.lifecycle.LifecycleOwner;
import com.client.tok.bean.UserInfo;
import com.client.tok.ui.basecontract.BaseContract;

public class MyInforContract {
    public interface IMyInfoView extends BaseContract.IBaseView<IMyInfoPresenter>, LifecycleOwner {

        void showUserInfo(UserInfo userInfo);
    }

    public interface IMyInfoPresenter extends BaseContract.IBasePresenter {

        void editNickName();

        void editSignature();

        void updateAvatars(String friendKey, String avatarName);

        void delAvatars();

        void onDestroy();
    }
}
