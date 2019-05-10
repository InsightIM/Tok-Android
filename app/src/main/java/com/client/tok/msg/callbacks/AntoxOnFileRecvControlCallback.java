package com.client.tok.msg.callbacks;

import com.client.tok.bean.ContactInfo;
import com.client.tok.tox.State;
import com.client.tok.transfer.FileStatus;
import com.client.tok.transfer.FileTransfer;
import com.client.tok.utils.LogUtil;
import im.tox.tox4j.core.enums.ToxFileControl;

public class AntoxOnFileRecvControlCallback {
    private String TAG = "AntoxOnFileRecvControlCallback";

    public void fileRecvControl(ContactInfo friendInfo, int fileNumber, ToxFileControl control) {
        FileTransfer transfer = State.transfers.get(friendInfo.getKey(), fileNumber);
        if (transfer != null) {
            LogUtil.i(TAG, "fileRecvControl friendKey:"
                + friendInfo.getKey().getKey()
                + ",controlType:"
                + control.name()
                + ",transfer Status:"
                + transfer.getStatus());
            if (control == ToxFileControl.RESUME
                && transfer.getStatus() == FileStatus.REQUEST_SENT) {
                State.transfers.fileTransferStarted(transfer.getKey(), transfer.getFileNumber());
            } else if (control == ToxFileControl.RESUME
                && transfer.getStatus() == FileStatus.PAUSED) {
                State.transfers.fileTransferStarted(transfer.getKey(), transfer.getFileNumber());
            } else if (control == ToxFileControl.PAUSE) {
                State.transfers.pauseFile(transfer.getDbId());
            } else if (control == ToxFileControl.CANCEL) {
                State.transfers.cancelFile(transfer.getKey(), transfer.getFileNumber());
            } else {
                LogUtil.i(TAG, "not matched: " + control + ", " + transfer.getStatus());
            }
        } else {
            LogUtil.i(TAG, "Transfer not found");
        }
    }
}
