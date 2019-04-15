package com.client.tok.tox;

import android.content.Context;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.utils.ByteUtil;
import com.client.tok.utils.FileUtilsJ;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.StringUtils;
import im.tox.tox4j.core.options.SaveDataOptions;
import im.tox.tox4j.impl.jni.ToxCryptoImpl;
import java.io.File;

public class ToxDataFile {
    private static String TAG = "ToxDataFile";
    private Context context;
    private String fileName;
    private String FILE_SUFFIX = GlobalParams.ACCOUNT_FILE_SUFFIX;
    private String profileName;

    public ToxDataFile(Context context, String fileName) {
        this.context = context;
        this.fileName = fileName;
        profileName = this.fileName + FILE_SUFFIX;
    }

    private File getFile() {
        return context.getFileStreamPath(fileName);
    }

    public static boolean isEncrypted(String file) {
        byte[] data = FileUtilsJ.readToBytes(file);
        return ToxCryptoImpl.isDataEncrypted(data);
    }

    public SaveDataOptions loadAsSaveType() {
        if (doesFileExist()) {
            return SaveDataOptions.ToxSave(loadFile());
        } else {
            return SaveDataOptions.None;
        }
    }

    public boolean doesFileExist() {
        return getFile().exists();
    }

    public static byte[] decrypt(String sourceFile, String pwd) {
        try {
            if (isEncrypted(sourceFile)) {
                byte[] data = FileUtilsJ.readToBytes(sourceFile);
                byte[] saltData = ToxCryptoImpl.getSalt(data);
                boolean hasSalt = saltData != null && saltData.length > 0;
                byte[] passKey;
                if (hasSalt) {
                    passKey =
                        ToxCryptoImpl.passKeyDeriveWithSalt(StringUtils.getBytes(pwd), saltData);
                } else {
                    passKey = ToxCryptoImpl.passKeyDerive(StringUtils.getBytes(pwd));
                }
                return ToxCryptoImpl.decrypt(data, passKey);
            } else {
                return FileUtilsJ.readToBytes(sourceFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * export file with password
     * if password is empty, no need encrpyt
     *
     * @param destDir dest file path
     * @param pwd password need to encrypt
     */
    public boolean exportFile(String destDir, String pwd) {
        File resourceFile = getFile();
        File destFile = new File(destDir + File.separator + profileName);
        if (StringUtils.isEmpty(pwd)) {
            FileUtilsJ.copy(resourceFile, destFile);
            return true;
        } else {
            try {
                byte[] passphrase = StringUtils.getBytes(pwd);
                //1.append magic number
                byte[] magic = StringUtils.getBytes("toxEsave");//don't change this magic string
                //2.get data(not encrypt)
                byte[] noEncryptData = FileUtilsJ.readToBytes(resourceFile);
                LogUtil.i("exportFile", "noEncrypt data length:" + noEncryptData.length);
                //3.combine magic number and data
                byte[] data = ByteUtil.byteMergerAll(magic, noEncryptData);
                //3.get salt
                byte[] salt = ToxCryptoImpl.getSalt(data); //TODO has bug, get salt exception
                //byte[] salt = null;
                //4.get passkey(has salt/not has salt)
                boolean hasSalt = salt != null && salt.length > 0;
                LogUtil.i("exportFile", "hasSalt:" + hasSalt);
                byte[] passKey;
                if (hasSalt) {
                    passKey = ToxCryptoImpl.passKeyDeriveWithSalt(passphrase, salt);
                } else {
                    passKey = ToxCryptoImpl.passKeyDerive(passphrase);
                }
                //4.encrypt with passkey
                byte[] encryptData = ToxCryptoImpl.encrypt(noEncryptData, passKey);
                FileUtilsJ.writeFile(destFile, encryptData);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public String getProfileName() {
        return profileName;
    }

    public void deleteFile() {
        context.deleteFile(fileName);
    }

    public byte[] loadFile() {
        return FileUtilsJ.readToBytes(getFile());
    }

    public void saveFile(byte[] dataToBeSaved) {
        FileUtilsJ.writeFile(getFile(), dataToBeSaved);
    }
}
