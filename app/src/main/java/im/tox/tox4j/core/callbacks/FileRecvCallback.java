package im.tox.tox4j.core.callbacks;

import im.tox.tox4j.core.data.ToxFilename;
import im.tox.tox4j.core.data.ToxFriendNumber;

/**
 * This event is triggered when a file transfer request is received.
 */
public interface FileRecvCallback {
    /**
     * The client should acquire resources to be associated with the file transfer.
     * Incoming file transferManager start in the PAUSED state. After this callback
     * returns, a transfer can be rejected by sending a [[ToxFileControl.CANCEL]]
     * control command before any other control commands. It can be accepted by
     * sending [[ToxFileControl.RESUME]].
     *
     * @param friendNumber The friend number of the friend who is sending the file transfer request.
     * @param fileNumber The friend-specific file number the data received is associated with.
     * @param kind The meaning of the file to be sent.
     * @param fileSize Size in bytes of the file the client wants to send, -1 if unknown or streaming.
     * @param filename Name of the file. May not be the actual name. This name was sent along with the file send request.
     */
    void fileRecv(ToxFriendNumber friendNumber, int fileNumber, int kind, long fileSize,
        ToxFilename filename);
}
