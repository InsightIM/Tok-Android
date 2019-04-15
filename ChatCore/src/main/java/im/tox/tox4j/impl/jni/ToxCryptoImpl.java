package im.tox.tox4j.impl.jni;

import im.tox.tox4j.crypto.ToxCrypto;
import im.tox.tox4j.crypto.ToxCryptoConstants;
import im.tox.tox4j.crypto.exceptions.ToxDecryptionException;
import im.tox.tox4j.crypto.exceptions.ToxEncryptionException;
import im.tox.tox4j.crypto.exceptions.ToxGetSaltException;
import im.tox.tox4j.crypto.exceptions.ToxKeyDerivationException;

public class ToxCryptoImpl implements ToxCrypto {
    public static boolean passKeyEquals(byte[] a, byte[] b) {
        //原scala a.deep == b.deep
        return a.toString().equals(b.toString());
    }


    public static byte[] passKeyToBytes(byte[] passKey) {
        return passKey;
    }

    public static byte[] passKeyFromBytes(byte[] bytes) {
        if (bytes.length == ToxCryptoConstants.KeyLength + ToxCryptoConstants.SaltLength) {
            return bytes;
        } else {
            return null;
        }
    }

    public static byte[] passKeyDerive(byte[] passphrase) throws ToxKeyDerivationException {
        return ToxCryptoJni.toxPassKeyDerive(passphrase);
    }

    public static byte[] passKeyDeriveWithSalt(byte[] passphrase, byte[] salt)
        throws ToxKeyDerivationException {
        return ToxCryptoJni.toxPassKeyDeriveWithSalt(passphrase, salt);
    }

    public static byte[] getSalt(byte[] data) throws ToxGetSaltException {
        return ToxCryptoJni.toxGetSalt(data);
    }

    public static byte[] encrypt(byte[] data, byte[] passKey) throws ToxEncryptionException {
        return ToxCryptoJni.toxPassKeyEncrypt(data, passKey);
    }

    public static byte[] decrypt(byte[] data, byte[] passKey) throws ToxDecryptionException {
        return ToxCryptoJni.toxPassKeyDecrypt(data, passKey);
    }

    public static boolean isDataEncrypted(byte[] data) {
        return ToxCryptoJni.toxIsDataEncrypted(data);
    }

    public static byte[] hash(byte[] data) {
        return ToxCryptoJni.toxHash(data);
    }
}
