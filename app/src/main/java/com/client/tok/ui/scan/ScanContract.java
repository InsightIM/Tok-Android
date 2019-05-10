package com.client.tok.ui.scan;

import com.client.tok.ui.basecontract.BaseContract;

public class ScanContract {
    public interface IScanView extends BaseContract.IBaseView<IScanPresenter> {

        void setScanable(boolean scanable, int delayMills);

        void showMsgDialog(String tokId, String alias, String defaultMsg);

        void showErr(int msgId);

        void showSuccess(int msgId);
    }

    public interface IScanPresenter extends BaseContract.IBasePresenter {
        void checkId(String tokId);

        void addFriend(String tokId, String alias, String msg);

        void onScanResult(String scanResult);

        void onDestroy();
    }
}
