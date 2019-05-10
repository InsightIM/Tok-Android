package com.client.tok.msg.callbacks;

import com.client.tok.bean.ContactInfo;
import com.client.tok.tox.ToxManager;
import com.client.tok.tox.State;
import com.client.tok.transfer.FileStatus;
import com.client.tok.transfer.FileTransfer;
import com.client.tok.utils.LogUtil;

public class AntoxOnFileChunkRequestCallback {
    private String TAG = "AntoxOnFileChunkRequestCallback";

    public void fileChunkRequest(ContactInfo friendInfo, int fileNumber, long position,
        int length) {
        FileTransfer fileTransfer = State.transfers.get(friendInfo.getKey(), fileNumber);

        if (fileTransfer != null) {
            fileTransfer.setStatus(FileStatus.IN_PROGRESS);
            if (length <= 0) {
                State.transfers.fileFinished(friendInfo.getKey(), fileTransfer.getFileNumber());
                State.infoRepo().clearFileNumber(fileTransfer.getRealReceiverKey(), fileNumber);
            } else {
                LogUtil.i(TAG, "RequestCallback position:" + position + ",length:" + length);
                byte[] data = fileTransfer.readData(position, length);
                if (data != null) {
                    ToxManager.getManager().toxBase.fileSendChunk(friendInfo.getKey(), fileNumber,
                        position, data);
                }
            }
        } else {
            LogUtil.i(TAG, "Can't find file transfer");
        }
    }
}
