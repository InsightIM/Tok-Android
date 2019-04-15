package com.client.tok.ui.share;

import com.client.tok.ui.basecontract.BaseContract;


public class ShareContract {
    public interface IShareView extends BaseContract.IBaseView<ISharePresenter> {
        void showPortraitView(String key, String name);

        void showMyInfo(CharSequence info);

        void showQr(String path);

        void showShareTxt(String content);

        void setShareViewInfo(String key, String name, String path);
    }

    public interface ISharePresenter extends BaseContract.IBasePresenter {

    }
}
