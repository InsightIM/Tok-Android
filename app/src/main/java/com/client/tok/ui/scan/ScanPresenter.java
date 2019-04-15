package com.client.tok.ui.scan;

import com.client.tok.R;
import com.client.tok.ui.addfriends.AddFriendsBasePresenter;
import com.client.tok.ui.addfriends.AddFriendsModel;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.PkUtils;
import io.reactivex.disposables.Disposable;

public class ScanPresenter extends AddFriendsBasePresenter implements ScanContract.IScanPresenter {
    private String TAG = "ScanPresenter";
    //https://github.com/mylhyl/Android-Zxing
    private ScanContract.IScanView mScanView;
    private Disposable mDisposable;
    private int mGroupNumber;
    private int RESCAN_DELAY_MILLS = 1000;

    public ScanPresenter(ScanContract.IScanView scanView) {
        this.mScanView = scanView;
        mScanView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void onScanResult(String scanResult) {
        if (scanResult == null) {
            mScanView.showErr(R.string.qr_code_not_found);
            mScanView.reStartScan(RESCAN_DELAY_MILLS);
        } else {
            LogUtil.i(TAG, "scanResult:" + scanResult);
            mScanView.stopScan();
            //好友
            checkId(scanResult);
        }
    }

    @Override
    public void checkId(String scanResult) {
        String tokId = PkUtils.getAddressFromContent(scanResult).toUpperCase();
        int checkResult = checkIdValid(tokId.toUpperCase());
        if (checkResult == AddFriendsModel.TOK_ID_VALID) {
            mScanView.showMsgDialog(tokId, null, null);
        } else {
            mScanView.showErr(checkResult);
            mScanView.reStartScan(RESCAN_DELAY_MILLS);
        }
    }

    @Override
    public void addFriend(String tokId, String alias, String msg) {
        int checkResult = checkIdValid(tokId);
        if (checkResult == AddFriendsModel.TOK_ID_VALID) {
            boolean addResult = super.addFriendById(tokId, alias, msg);
            if (addResult) {
                mScanView.showSuccess(R.string.add_friend_request_has_send);
                mScanView.viewDestroy();
            } else {
                mScanView.showErr(R.string.tok_id_invalid);
            }
        } else {
            mScanView.showErr(checkResult);
        }
    }

    private void stopListen() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        mDisposable = null;
    }

    @Override
    public void onDestroy() {
        if (mScanView != null) {
            mScanView = null;
        }
        stopListen();
    }
}
