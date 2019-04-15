package im.tox.tox4j.core.callbacks;

import im.tox.tox4j.core.data.ToxFriendNumber;

/**
 * This event is triggered when Core is ready to send more file data.
 */
public interface FileChunkRequestCallback {
    /**
     * If the length parameter is 0, the file transfer is finished, and the client's
     * resources associated with the file number should be released. After a call
     * with zero length, the file number can be reused for future file transferManager.
     *
     * If the requested position is not equal to the client's idea of the current
     * file or stream position, it will need to seek. In case of read-once streams,
     * the client should keep the last read chunk so that a seek back can be
     * supported. A seek-back only ever needs to read from the last requested chunk.
     * This happens when a chunk was requested, but the send failed. A seek-back
     * request can occur an arbitrary number of times for any given chunk.
     *
     * In response to receiving this callback, the client should call the function
     * [[ToxCore.fileSendChunk]] with the requested chunk. If the number of bytes
     * sent through that function is zero, the file transfer is assumed complete.
     * A client must send the full length of data requested with this callback.
     *
     * @param friendNumber The friend number of the receiving friend for this file.
     * @param fileNumber The file transfer identifier returned by { @link im.tox.tox4j.core.ToxCore#fileSend}.
     * @param position The file or stream position from which to continue reading.
     * @param length The number of bytes requested for the current chunk.
     */
    void fileChunkRequest(ToxFriendNumber friendNumber, int fileNumber, long position, int length);
}
