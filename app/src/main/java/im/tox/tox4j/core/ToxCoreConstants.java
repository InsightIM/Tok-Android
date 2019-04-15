package im.tox.tox4j.core;

import im.tox.tox4j.crypto.ToxCryptoConstants;

public class ToxCoreConstants {
    /**
     * The size of a Tox Public Key in bytes.
     */
    public static int PublicKeySize = ToxCryptoConstants.PublicKeyLength;

    /**
     * The size of a Tox Secret Key in bytes.
     */
    public static int SecretKeySize = ToxCryptoConstants.SecretKeyLength;

    /**
     * The size of a Tox address in bytes. Tox addresses are in the format
     * [Public Key ([[PublicKeySize]] bytes)][nospam (4 bytes)][checksum (2 bytes)].
     *
     * The checksum is computed over the Public Key and the nospam value. The first
     * byte is an XOR of all the odd bytes, the second byte is an XOR of all the
     * even bytes of the Public Key and nospam.
     */
    public static int AddressSize = PublicKeySize + 4 + 2;

    /**
     * Maximum length of a nickname in bytes.
     */
    public static int MaxNameLength = 128;

    /**
     * Maximum length of a status message in bytes.
     */
    public static int MaxStatusMessageLength = 1007;

    /**
     * Maximum length of a friend request message in bytes.
     */
    public static int MaxFriendRequestLength = 1016;

    /**
     * Maximum length of a single message after which it should be split.
     */
    public static int MaxMessageLength = 1372;

    /**
     * Maximum size of custom packets. TODO: should be LENGTH?
     */
    public static int MaxCustomPacketSize = 1373;

    /**
     * Maximum file name length for file transferManager.
     */
    public static int MaxFilenameLength = 255;

    /**
     * Maximum hostname length. This is determined by calling `getconf HOST_NAME_MAX` on the console. The value
     * presented here is valid for most systems.
     */
    public static int MaxHostnameLength = 255;

    /**
     * The number of bytes in a file id.
     */
    public static int FileIdLength = ToxCryptoConstants.HashLength;

    /**
     * Default port for HTTP proxies.
     */
    public static int DefaultProxyPort = 8080;

    /**
     * Default start port for Tox UDP sockets.
     */
    public static int DefaultStartPort = 33445;

    /**
     * Default end port for Tox UDP sockets.
     */
    public static int DefaultEndPort = DefaultStartPort + 100;

    /**
     * Default port for Tox TCP relays. A value of 0 means disabled.
     */
    public static int DefaultTcpPort = 0;
}
