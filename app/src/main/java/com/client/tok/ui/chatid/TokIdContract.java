package com.client.tok.ui.chatid;

import com.client.tok.ui.basecontract.BaseContract;

public class TokIdContract {
    public interface IChatIdView extends BaseContract.IBaseView<IChatIdPresenter> {
        void showTokId(CharSequence tokId);

        void showTokIdImg(String file);

        void showTitle(String title);

        void showPrompt(String prompt);
    }

    public interface IChatIdPresenter extends BaseContract.IBasePresenter {

        void share();

        void onDestroy();
    }
}
