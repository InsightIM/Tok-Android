package com.client.tok.ui.addfriends;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.client.tok.R;
import com.client.tok.bean.ToxAddress;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.pagejump.IntentConstants;
import com.client.tok.utils.LocalBroaderUtils;
import java.io.Serializable;

public class AddFriendsPresenter extends AddFriendsBasePresenter
    implements AddFriendsContract.IAddFriendsPresenter {
    public AddFriendsContract.IAddFriendsView mAddFriendsView;
    public ScanReceiver mScanReceiver;

    public AddFriendsPresenter(AddFriendsContract.IAddFriendsView addFriendsView) {
        this.mAddFriendsView = addFriendsView;
        // mScanReceiver = new ScanReceiver();
        mAddFriendsView.setPresenter(this);
    }

    @Override
    public void start() {
        //LocalBroaderUtils.registerLocalReceiver(mScanReceiver, GlobalParams.ACTION_SCAN_RESULT);
    }

    @Override
    public void checkId(String tokId) {
        int checkResult = checkIdValid(tokId);
        if (checkResult == AddFriendsModel.TOK_ID_VALID) {
            mAddFriendsView.showMsgDialog(tokId, null, null);
        } else {
            mAddFriendsView.showErr(checkResult);
        }
    }

    @Override
    public void addFriend(String tokId, String alias, String msg) {
        int checkResult = checkIdValid(tokId);
        if (checkResult == AddFriendsModel.TOK_ID_VALID) {
            boolean addResult = super.addFriendById(tokId, alias, msg);
            if (addResult) {
                mAddFriendsView.showSuccess(R.string.add_friend_request_has_send);
                mAddFriendsView.viewDestroy();
            } else {
                mAddFriendsView.showErr(R.string.tok_id_invalid);
            }
        } else {
            mAddFriendsView.showErr(checkResult);
        }
    }

    @Override
    public void onDestroy() {
        if (mAddFriendsView != null) {
            mAddFriendsView = null;
        }
        if (mScanReceiver != null) {
            LocalBroaderUtils.unRegisterReceiver(mScanReceiver);
            mScanReceiver = null;
        }
    }

    private class ScanReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (GlobalParams.ACTION_SCAN_RESULT.equals(action)) {
                Serializable result = intent.getSerializableExtra(IntentConstants.NOTIFY_DATA);
                if (result instanceof String) {
                    mAddFriendsView.showTokId(new ToxAddress((String) result).toString());
                }
            }
        }
    }
}
