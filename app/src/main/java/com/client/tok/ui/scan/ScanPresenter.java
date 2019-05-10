package com.client.tok.ui.scan;

import com.client.tok.R;
import com.client.tok.ui.addfriends.AddFriendsBasePresenter;
import com.client.tok.ui.addfriends.AddFriendsModel;
import com.client.tok.ui.scan.decoder.Decoder;
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
    private Decoder mDecoder;

    public ScanPresenter(ScanContract.IScanView scanView) {
        this.mScanView = scanView;
        mScanView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void onScanResult(String scanResult) {
        LogUtil.i(TAG, "scanResult:" + scanResult);
        if (scanResult == null) {
            mScanView.showErr(R.string.qr_code_not_found);
            mScanView.setScanable(true, RESCAN_DELAY_MILLS);
        } else {
            mScanView.setScanable(false, -1);
            //friend tokId
            checkId(scanResult);
        }
    }

    @Override
    public void checkId(String scanResult) {
        String tokId = PkUtils.getAddressFromContent(scanResult).toUpperCase();
        int checkResult = checkIdValid(tokId.toUpperCase());
        if (checkResult == AddFriendsModel.TOK_ID_VALID) {
            mScanView.setScanable(false, -1);
            mScanView.showMsgDialog(tokId, null, null);
        } else {
            mScanView.setScanable(true, -1);
            mScanView.showErr(checkResult);
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

    private void joinGroup(String scanResult) {
        mScanView.showErr(R.string.not_support_group);
        //try {
        //    mGroupNumber =
        //        Integer.valueOf(scanResult.substring(GlobalParams.GROUP_ID_PRE_SUFFIX.length()));
        //    FriendKey beInvitedKey = FriendKey.apply(ToxSingleton.tox().getSelfKey().toString());
        //    GroupMsgSender.invitePeer(mGroupNumber,
        //        FriendKey.apply(ToxSingleton.tox().getSelfKey().toString()));
        //    LogUtil.i(TAG, "join group:" + beInvitedKey.toString());
        //    //GroupMsgSender.getPeerList(mGroupNumber);
        //    listen();
        //} catch (Exception e) {
        //    e.printStackTrace();
        //    mScanView.showErr(R.string.scan_content_invalid);
        //}
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
        if (mDecoder != null) {
            mDecoder.destroy();
            mDecoder = null;
        }
        stopListen();
    }
}
