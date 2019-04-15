package im.tox.tox4j.impl.jni;

public class ToxCryptoJni {
    static {
        ToxLoadJniLibrary.load("tox4j-c");
    }

    public static native byte[] toxPassKeyEncrypt(byte[] data, byte[] passKey);

    public static native byte[] toxGetSalt(byte[] data);

    public static native boolean toxIsDataEncrypted(byte[] data);

    public static native byte[] toxPassKeyDeriveWithSalt(byte[] passphrase, byte[] salt);

    public static native byte[] toxPassKeyDerive(byte[] passphrase);

    public static native byte[] toxPassKeyDecrypt(byte[] data, byte[] passKey);

    public static native byte[] toxHash(byte[] data);
}
