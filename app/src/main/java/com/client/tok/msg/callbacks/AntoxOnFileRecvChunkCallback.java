package com.client.tok.msg.callbacks;

import com.client.tok.bean.ContactInfo;
import com.client.tok.tox.State;
import com.client.tok.transfer.FileTransfer;

public class AntoxOnFileRecvChunkCallback {
    private String TAG = "AntoxOnFileRecvChunkCallback";

    public void fileRecvChunk(ContactInfo friendInfo, int fileNumber, long position, byte[] data) {
        FileTransfer transfers = State.transferManager().get(friendInfo.getKey(), fileNumber);
        if (transfers != null && position >= transfers.getSize()) {
            State.transferManager().fileFinished(friendInfo.getKey(), fileNumber);
        } else {
            State.transferManager().receiveFileData(friendInfo.getKey(), fileNumber, data);
        }
    }
}
