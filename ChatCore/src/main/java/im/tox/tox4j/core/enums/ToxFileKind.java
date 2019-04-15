package im.tox.tox4j.core.enums;

public final class ToxFileKind {
    /**
     * Arbitrary file data. Clients can choose to handle it based on the file name
     * or magic or any other way they choose.
     */
    public static final int DATA = 0;
    /**
     * Avatar image data.
     *
     * <p/>
     * Avatars can be sent at any time the client wishes. Generally, a client will
     * send the avatar to a friend when that friend comes online, and to all
     * friends when the avatar changed. A client can save some traffic by
     * remembering which friend received the updated avatar already and only send
     * it if the friend has an out of date avatar.
     *
     * <p/>
     * Clients who receive avatar send requests can reject it (by sending
     * {@link FileControl#CANCEL} before any other controls), or accept it
     * (by sending {@link FileControl#RESUME}).
     * The file_id of length {@link ToxCryptoConstants#HASH_LENGTH} bytes (same
     * length as {@link ToxCoreConstants#FILE_ID_LENGTH}) will contain the hash.
     * A client can compare this hash with a saved hash and send
     * {@link FileControl#CANCEL} to terminate the avatar transfer if it matches.
     *
     * <p/>
     * When fileSize is set to 0 in the transfer request it means that the client
     * has no avatar.
     */
    public static final int AVATAR = 1;
}
