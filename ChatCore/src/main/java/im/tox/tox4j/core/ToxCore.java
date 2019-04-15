package im.tox.tox4j.core;

import im.tox.core.network.Port;
import im.tox.tox4j.core.callbacks.ToxCoreEventListener;
import im.tox.tox4j.core.callbacks.ToxCoreEventSynth;
import im.tox.tox4j.core.data.ToxFileId;
import im.tox.tox4j.core.data.ToxFilename;
import im.tox.tox4j.core.data.ToxFriendAddress;
import im.tox.tox4j.core.data.ToxFriendMessage;
import im.tox.tox4j.core.data.ToxFriendNumber;
import im.tox.tox4j.core.data.ToxFriendRequestMessage;
import im.tox.tox4j.core.data.ToxLosslessPacket;
import im.tox.tox4j.core.data.ToxLossyPacket;
import im.tox.tox4j.core.data.ToxNickname;
import im.tox.tox4j.core.data.ToxPublicKey;
import im.tox.tox4j.core.data.ToxSecretKey;
import im.tox.tox4j.core.data.ToxStatusMessage;
import im.tox.tox4j.core.enums.ToxFileControl;
import im.tox.tox4j.core.enums.ToxMessageType;
import im.tox.tox4j.core.enums.ToxUserStatus;
import im.tox.tox4j.core.exceptions.ToxBootstrapException;
import im.tox.tox4j.core.exceptions.ToxFileControlException;
import im.tox.tox4j.core.exceptions.ToxFileGetException;
import im.tox.tox4j.core.exceptions.ToxFileSeekException;
import im.tox.tox4j.core.exceptions.ToxFileSendChunkException;
import im.tox.tox4j.core.exceptions.ToxFileSendException;
import im.tox.tox4j.core.exceptions.ToxFriendAddException;
import im.tox.tox4j.core.exceptions.ToxFriendByPublicKeyException;
import im.tox.tox4j.core.exceptions.ToxFriendCustomPacketException;
import im.tox.tox4j.core.exceptions.ToxFriendDeleteException;
import im.tox.tox4j.core.exceptions.ToxFriendGetPublicKeyException;
import im.tox.tox4j.core.exceptions.ToxFriendSendMessageException;
import im.tox.tox4j.core.exceptions.ToxGetPortException;
import im.tox.tox4j.core.exceptions.ToxNewException;
import im.tox.tox4j.core.exceptions.ToxSetInfoException;
import im.tox.tox4j.core.exceptions.ToxSetTypingException;
import im.tox.tox4j.core.options.ToxOptions;

/**
 * Interface for a basic wrapper of tox chat functionality.
 *
 * This interface is designed to be thread-safe. However, once [[ToxCore.close]] has been called, all subsequent calls
 * will result in [[im.tox.tox4j.exceptions.ToxKilledException]] being thrown. When one thread invokes
 * [[ToxCore.close]], all other threads with pending calls will throw. The exception is unchecked, as it should not occur
 * in a normal execution flow. To prevent it from occurring in a multi-threaded environment, all additional threads
 * should be stopped or stop using the instance before one thread invokes [[ToxCore.close]] on it, or appropriate
 * exception handlers should be installed in all threads.
 */
public interface ToxCore extends ToxCoreEventSynth {
    /**
     * Store all information associated with the tox instance to a byte array.
     *
     * The data in the byte array can be used to create a new instance with [[load]] by passing it to the
     * [[ToxOptions]] constructor. The concrete format in this serialised instance is implementation-defined. Passing
     * save data created by one class to a different class may not work.
     *
     * @return a byte array containing the serialised tox instance.
     */

    byte[] getSavedata();

    /**
     * Create a new [[ToxCore]] instance with different options. The implementation may choose to create an object of
     * its own class or a different class. If the implementation was compatible with another subsystem implementation (e.g.
     * [[im.tox.tox4j.av.ToxAv]]), then the new object must be compatible with the same implementation.
     *
     * This function will bring the instance into a valid state. Running the event
     * loop with a new instance will operate correctly.
     *
     * If the [[ToxOptions.saveData]] field is not empty, this function will load the Tox instance
     * from a byte array previously filled by [[getSavedata]].
     *
     * If loading failed or succeeded only partially, an exception will be thrown.
     *
     * @return a new [[ToxCore]] instance.
     */

    ToxCore load(ToxOptions options) throws ToxNewException;

    /**
     * Shut down the tox instance.
     *
     * Releases all resources associated with the Tox instance and disconnects from
     * the network.
     *
     * Once this method has been called, all other calls on this instance will throw
     * [[im.tox.tox4j.exceptions.ToxKilledException]]. A closed instance cannot be reused; a new instance must be
     * created.
     */
    void close();

    void finalize();

    /**
     * Bootstrap into the tox network.
     *
     * Sends a "get nodes" request to the given bootstrap node with IP, port, and
     * public key to setup connections.
     *
     * This function will only attempt to connect to the node using UDP. If you want
     * to additionally attempt to connect using TCP, use [[addTcpRelay]] together with
     * this function.
     *
     * @param address the hostname, or an IPv4/IPv6 address of the node.
     * @param port the port of the node.
     * @param publicKey the public key of the node.
     */
    void bootstrap(String address, Port port, ToxPublicKey publicKey) throws ToxBootstrapException;

    /**
     * Connect to a TCP relay to forward traffic.
     *
     * This function can be used to initiate TCP connections to different ports on
     * the same bootstrap node, or to add TCP relays without using them as
     * bootstrap nodes.
     *
     * @param address the hostname, or an IPv4/IPv6 address of the node.
     * @param port the TCP port the node is running a relay on.
     * @param publicKey the public key of the node.
     */
    void addTcpRelay(String address, Port port, ToxPublicKey publicKey)
        throws ToxBootstrapException;

    /**
     * Get the UDP port this instance is bound to.
     *
     * @return a port number between 1 and 65535.
     */
    Port getUdpPort() throws ToxGetPortException;

    /**
     * Return the TCP port this Tox instance is bound to. This is only relevant if
     * the instance is acting as a TCP relay.
     *
     * @return a port number between 1 and 65535.
     */
    Port getTcpPort() throws ToxGetPortException;

    /**
     * Writes the temporary DHT public key of this instance to a byte array.
     *
     * This can be used in combination with an externally accessible IP address and
     * the bound port (from [[getUdpPort]]}) to run a temporary bootstrap node.
     *
     * Be aware that every time a new instance is created, the DHT public key
     * changes, meaning this cannot be used to run a permanent bootstrap node.
     *
     * @return a byte array of size [[ToxCoreConstants.PublicKeySize]]
     */

    ToxPublicKey getDhtId();

    /**
     * Get the time in milliseconds until [[iterate]] should be called again for optimal performance.
     *
     * @return the time in milliseconds until [[iterate]] should be called again.
     */
    int iterationInterval();

    /**
     * The main loop.
     *
     * This should be invoked every [[iterationInterval]] milliseconds.
     */
    void iterate(ToxCoreEventListener listener);

    /**
     * Copy the Tox Public Key (long term) from the Tox object.
     *
     * @return a byte array of size [[ToxCoreConstants.PublicKeySize]]
     */

    ToxPublicKey getPublicKey();

    /**
     * Copy the Tox Secret Key from the Tox object.
     *
     * @return a byte array of size [[ToxCoreConstants.SecretKeySize]]
     */

    ToxSecretKey getSecretKey();

    /**
     * Set the 4-byte nospam part of the address.
     *
     * Setting the nospam makes it impossible for others to send us friend requests that contained the
     * old nospam number.
     *
     * @param nospam the new nospam number.
     */
    void setNospam(int nospam);

    /**
     * Get our current nospam number.
     */
    int getNospam();

    /**
     * Get our current tox address to give to friends.
     *
     * The format is the following: [Public Key (32 bytes)][noSpam number (4 bytes)][checksum (2 bytes)].
     * After a call to [[setNospam]], the old address can no longer be used to send friend requests to
     * this instance.
     *
     * Note that it is not in a human-readable format. To display it to users, it needs to be formatted.
     *
     * @return a byte array of size [[ToxCoreConstants.AddressSize]]
     */

    ToxFriendAddress getAddress();

    /**
     * Set the nickname for the Tox client.
     *
     * Cannot be longer than [[ToxCoreConstants.MaxNameLength]] bytes. Can be empty (zero-length).
     *
     * @param name A byte array containing the new nickname..
     */
    void setName(ToxNickname name) throws ToxSetInfoException;

    /**
     * Get our own nickname.
     */

    ToxNickname getName();

    /**
     * Set our status message.
     *
     * Cannot be longer than [[ToxCoreConstants.MaxStatusMessageLength]] bytes.
     *
     * @param message the status message to set.
     */

    void setStatusMessage(ToxStatusMessage message) throws ToxSetInfoException;

    /**
     * Gets our own status message. May be null if the status message was empty.
     */

    ToxStatusMessage getStatusMessage();

    /**
     * Set our status.
     *
     * @param status status to set.
     */
    void setStatus(ToxUserStatus status);

    /**
     * Get our status.
     */

    ToxUserStatus getStatus();

    /**
     * Add a friend to the friend list and send a friend request.
     *
     * A friend request message must be at least 1 byte long and at most
     * [[ToxCoreConstants.MaxFriendRequestLength]].
     *
     * Friend numbers are unique identifiers used in all functions that operate on
     * friends. Once added, a friend number is stable for the lifetime of the Tox
     * object. After saving the state and reloading it, the friend numbers may not
     * be the same as before. Deleting a friend creates a gap in the friend number
     * set, which is filled by the next adding of a friend. Any pattern in friend
     * numbers should not be relied on.
     *
     * If more than [[Int.MaxValue]] friends are added, this function throws
     * an exception.
     *
     * @param address the address to add as a friend ([[ToxCoreConstants.AddressSize]] bytes).
     * This is the byte array the friend got from their own [[getAddress]].
     * @param message the message to send with the friend request (must not be empty).
     * @return the new friend's friend number.
     */

    ToxFriendNumber addFriend(ToxFriendAddress address, ToxFriendRequestMessage message)
        throws ToxFriendAddException, IllegalArgumentException;

    /**
     * Add a friend without sending a friend request.
     *
     * This function is used to add a friend in response to a friend request. If the
     * client receives a friend request, it can be reasonably sure that the other
     * client added this client as a friend, eliminating the need for a friend
     * request.
     *
     * This function is also useful in a situation where both instances are
     * controlled by the same entity, so that this entity can perform the mutual
     * friend adding. In this case, there is no need for a friend request, either.
     *
     * @param publicKey the Public Key to add as a friend ([[ToxCoreConstants.PublicKeySize]] bytes).
     * @return the new friend's friend number.
     */
    ToxFriendNumber addFriendNorequest(ToxPublicKey publicKey)
        throws ToxFriendAddException, IllegalArgumentException;

    /**
     * Remove a friend from the friend list.
     *
     * This does not notify the friend of their deletion. After calling this
     * function, this client will appear offline to the friend and no communication
     * can occur between the two.
     *
     * @param friendNumber the friend number to delete.
     */

    void deleteFriend(ToxFriendNumber friendNumber) throws ToxFriendDeleteException;

    /**
     * Gets the friend number for the specified Public Key.
     *
     * @param publicKey the Public Key.
     * @return the friend number that is associated with the Public Key.
     */

    ToxFriendNumber friendByPublicKey(ToxPublicKey publicKey) throws ToxFriendByPublicKeyException;

    /**
     * Gets the Public Key for the specified friend number.
     *
     * @param friendNumber the friend number.
     * @return the Public Key associated with the friend number.
     */

    ToxPublicKey getFriendPublicKey(ToxFriendNumber friendNumber)
        throws ToxFriendGetPublicKeyException;

    /**
     * Checks whether a friend with the specified friend number exists.
     *
     * If this function returns <code>true</code>, the return value is valid until the friend is deleted. If
     * <code>false</code> is returned, the return value is valid until either of [[addFriend]] or
     * [[addFriendNorequest]] is invoked.
     *
     * @param friendNumber the friend number to check.
     * @return true if such a friend exists.
     */
    Boolean friendExists(ToxFriendNumber friendNumber);

    /**
     * Get an array of currently valid friend numbers.
     *
     * This list is valid until either of the following is invoked: [[deleteFriend]],
     * [[addFriend]], [[addFriendNorequest]].
     *
     * @return an array containing the currently valid friend numbers, the empty int array if there are no friends.
     */

    int[] getFriendList();

    /**
     * Get an array of [[ToxFriendNumber]] objects with the same values as [[getFriendList]].
     *
     * This method exists for Java compatibility, because [[getFriendList]] must return an
     * int array.
     *
     * @return [[getFriendList]] mapped to [[ToxFriendNumber]].
     */

    ToxFriendNumber[] getFriendNumbers();

    /**
     * Tell friend number whether or not we are currently typing.
     *
     * The client is responsible for turning it on or off.
     *
     * @param friendNumber the friend number to set typing status for.
     * @param typing <code>true</code> if we are currently typing.
     */

    void setTyping(ToxFriendNumber friendNumber, Boolean typing) throws ToxSetTypingException;

    /**
     * Send a text chat message to an online friend.
     *
     * This function creates a chat message packet and pushes it into the send
     * queue.
     *
     * The message length may not exceed [[ToxCoreConstants.MaxMessageLength]].
     * Larger messages must be split by the client and sent as separate messages.
     * Other clients can then reassemble the fragments. Messages may not be empty.
     *
     * The return value of this function is the message ID. If a read receipt is
     * received, the triggered [[FriendReadReceiptCallback]] event will be passed this message ID.
     *
     * Message IDs are unique per friend per instance. The first message ID is 0. Message IDs
     * are incremented by 1 each time a message is sent. If [[Int.MaxValue]] messages were
     * sent, the next message ID is [[Int.MinValue]].
     *
     * Message IDs are not stored in the array returned by [[getSavedata]].
     *
     * @param friendNumber The friend number of the friend to send the message to.
     * @param messageType Message type (normal, action, ...).
     * @param timeDelta The time between composition (user created the message) and calling this function.
     * @param message The message text
     * @return the message ID.
     */

    int friendSendMessage(ToxFriendNumber friendNumber, ToxMessageType messageType, int timeDelta,
        ToxFriendMessage message) throws ToxFriendSendMessageException;

    /**
     * Sends a file control command to a friend for a given file transfer.
     *
     * @param friendNumber The friend number of the friend the file is being transferred to or received from.
     * @param fileNumber The friend-specific identifier for the file transfer.
     * @param control The control command to send.
     */
    void fileControl(ToxFriendNumber friendNumber, int fileNumber, ToxFileControl control)
        throws ToxFileControlException;

    /**
     * Sends a file seek control command to a friend for a given file transfer.
     *
     * This function can only be called to resume a file transfer right before
     * [[ToxFileControl.RESUME]] is sent.
     *
     * @param friendNumber The friend number of the friend the file is being received from.
     * @param fileNumber The friend-specific identifier for the file transfer.
     * @param position The position that the file should be seeked to.
     */
    void fileSeek(ToxFriendNumber friendNumber, int fileNumber, Long position)
        throws ToxFileSeekException;

    /**
     * Return the file id associated to the file transfer as a byte array.
     *
     * @param friendNumber The friend number of the friend the file is being transferred to or received from.
     * @param fileNumber The friend-specific identifier for the file transfer.
     */
    ToxFileId getFileFileId(ToxFriendNumber friendNumber, int fileNumber)
        throws ToxFileGetException;

    /**
     * Send a file transmission request.
     *
     * Maximum filename length is [[ToxCoreConstants.MaxFilenameLength]] bytes. The filename
     * should generally just be a file name, not a path with directory names.
     *
     * If a non-negative file size is provided, it can be used by both sides to
     * determine the sending progress. File size can be set to a negative value for streaming
     * data of unknown size.
     *
     * File transmission occurs in chunks, which are requested through the
     * [[FileChunkRequestCallback]] event.
     *
     * When a friend goes offline, all file transfers associated with the friend are
     * purged from core.
     *
     * If the file contents change during a transfer, the behaviour is unspecified
     * in general. What will actually happen depends on the mode in which the file
     * was modified and how the client determines the file size.
     *
     * - If the file size was increased
     * - and sending mode was streaming (fileSize = -1), the behaviour
     * will be as expected.
     * - and sending mode was file (fileSize != -1), the
     * [[FileChunkRequestCallback]] callback will receive length = 0 when Core thinks
     * the file transfer has finished. If the client remembers the file size as
     * it was when sending the request, it will terminate the transfer normally.
     * If the client re-reads the size, it will think the friend cancelled the
     * transfer.
     * - If the file size was decreased
     * - and sending mode was streaming, the behaviour is as expected.
     * - and sending mode was file, the callback will return 0 at the new
     * (earlier) end-of-file, signalling to the friend that the transfer was
     * cancelled.
     * - If the file contents were modified
     * - at a position before the current read, the two files (local and remote)
     * will differ after the transfer terminates.
     * - at a position after the current read, the file transfer will succeed as
     * expected.
     * - In either case, both sides will regard the transfer as complete and
     * successful.
     *
     * @param friendNumber The friend number of the friend the file send request should be sent to.
     * @param kind The meaning of the file to be sent.
     * @param fileSize Size in bytes of the file the client wants to send, -1 if unknown or streaming.
     * @param fileId A file identifier of length [[ToxCoreConstants.FileIdLength]] that can be used to
     * uniquely identify file transfers across core restarts. If empty, a random one will
     * be generated by core. It can then be obtained by using [[getFileFileId]]
     * @param filename Name of the file. Does not need to be the actual name. This
     * name will be sent along with the file send request.
     * @return A file number used as an identifier in subsequent callbacks. This
     * number is per friend. File numbers are reused after a transfer terminates.
     * Any pattern in file numbers should not be relied on.
     */

    int fileSend(ToxFriendNumber friendNumber, int kind, Long fileSize, ToxFileId fileId,
        ToxFilename filename) throws ToxFileSendException;

    /**
     * Send a chunk of file data to a friend.
     *
     * This function is called in response to the [[FileChunkRequestCallback]] callback. The
     * length parameter should be equal to the one received though the callback.
     * If it is zero, the transfer is assumed complete. For files with known size,
     * Core will know that the transfer is complete after the last byte has been
     * received, so it is not necessary (though not harmful) to send a zero-length
     * chunk to terminate. For streams, core will know that the transfer is finished
     * if a chunk with length less than the length requested in the callback is sent.
     *
     * @param friendNumber The friend number of the receiving friend for this file.
     * @param fileNumber The file transfer identifier returned by [[fileSend]].
     * @param position The file or stream position from which the friend should continue writing.
     * @param data The chunk data.
     */

    void fileSendChunk(ToxFriendNumber friendNumber, int fileNumber, Long position, byte[] data)
        throws ToxFileSendChunkException;

    /**
     * Send a custom lossy packet to a friend.
     *
     * The first byte of data must be in the range 200-254. Maximum length of a
     * custom packet is [[ToxCoreConstants.MaxCustomPacketSize]].
     *
     * Lossy packets behave like UDP packets, meaning they might never reach the
     * other side or might arrive more than once (if someone is messing with the
     * connection) or might arrive in the wrong order.
     *
     * Unless latency is an issue, it is recommended that you use lossless custom
     * packets instead.
     *
     * @param friendNumber The friend number of the friend this lossy packet should be sent to.
     * @param data A byte array containing the packet data including packet id.
     */
    void friendSendLossyPacket(ToxFriendNumber friendNumber, ToxLossyPacket data)
        throws ToxFriendCustomPacketException;

    /**
     * Send a custom lossless packet to a friend.
     *
     * The first byte of data must be in the range 160-191. Maximum length of a
     * custom packet is [[ToxCoreConstants.MaxCustomPacketSize]].
     *
     * Lossless packet behaviour is comparable to TCP (reliability, arrive in order)
     * but with packets instead of a stream.
     *
     * @param friendNumber The friend number of the friend this lossless packet should be sent to.
     * @param data A byte array containing the packet data including packet id.
     */

    void friendSendLosslessPacket(ToxFriendNumber friendNumber, ToxLosslessPacket data)
        throws ToxFriendCustomPacketException;
}
