package com.client.tok.transfer;

import com.client.tok.bean.ContactsKey;
import com.client.tok.constant.FileKind;
import com.client.tok.utils.LogUtil;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileTransfer {
    private String TAG = "FileTransfer";
    private ContactsKey key;
    private ContactsKey realReceiverKey;
    private File file;
    private int fileNumber;
    private long size;
    private long initialProgress;
    private boolean sending;
    private FileStatus status;
    private long dbId;
    private FileKind fileKind;
    private boolean stripExifData;

    /**
     * receive file and write to sdcard
     */
    private FileOutputStream ops;
    private BufferedOutputStream bufferOps;

    /**
     * send file and read from sdcard
     */
    private FileInputStream ips;
    private BufferedInputStream bufferIps;

    private List<Progress> progressHistory = new ArrayList<>();

    public FileTransfer(ContactsKey key, ContactsKey realReceiverKey, File file, int fileNumber,
        long size, long initialProgress, boolean sending, FileStatus initialStatus, long dbId,
        FileKind fileKind, boolean stripExifData) {
        this.key = key;
        this.realReceiverKey = realReceiverKey;
        this.file = file;
        this.fileNumber = fileNumber;
        this.size = size;
        this.initialProgress = initialProgress;
        this.sending = sending;
        this.status = initialStatus;
        this.dbId = dbId;
        this.fileKind = fileKind;
        this.stripExifData = stripExifData;
    }

    public FileStatus getStatus() {
        return status;
    }

    public void setStatus(FileStatus status) {
        this.status = status;
    }

    public void addProgress(long progress) {
        progressHistory.add(new Progress(progress));
    }

    public long getProgress() {
        if (progressHistory.size() > 0) {
            return progressHistory.get(progressHistory.size() - 1).progressSize;
        }
        return 0;
    }

    /**
     * progress recorder
     */
    public class Progress {
        private long timestamp;//timestamp
        private long progressSize;//file has transfored size at this time

        public Progress(long progress) {
            timestamp = System.currentTimeMillis();
            this.progressSize = progress;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public long getProgressSize() {
            return progressSize;
        }

        public void setProgressSize(long progressSize) {
            this.progressSize = progressSize;
        }
    }

    public byte[] readData(long position, int chunkSize) {
        try {
            if (sending) {
                ips = new FileInputStream(file);
                bufferIps = new BufferedInputStream(ips);
                if (position > 0) {
                    long skipped = bufferIps.skip(position);
                    LogUtil.i(TAG, "skipped length:" + skipped);
                }
            }

            byte[] data = new byte[chunkSize];
            int readSize = bufferIps.read(data, 0, chunkSize);

            LogUtil.i(TAG,
                "readSize:" + readSize + ",currProgress:" + getProgress() + ",fileLength:" + size);
            addProgress(position + readSize);
            bufferIps.close();
            ips.close();
            bufferIps = null;
            ips = null;
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * write file data
     *
     * @param data data in bytes
     * @return write success:true  or false
     */
    public boolean writeData(byte[] data) {
        try {
            if (!sending && ops == null) {
                ops = new FileOutputStream(file, true);
                bufferOps = new BufferedOutputStream(ops);
            }
            bufferOps.write(data, 0, data.length);
            long progress = getProgress();
            LogUtil.i(TAG, "writeData curProgress:" + progress + ",data length:" + data.length);
            addProgress(progress + data.length);
            if (getProgress() >= size) {
                bufferOps.flush();
                bufferOps.close();
                ops.close();
                bufferOps = null;
                ops = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ContactsKey getRealReceiverKey() {
        return realReceiverKey;
    }

    public int getFileNumber() {
        return fileNumber;
    }

    public ContactsKey getKey() {
        return key;
    }

    public File getFile() {
        return file;
    }

    public long getInitialProgress() {
        return initialProgress;
    }

    public boolean isSending() {
        return sending;
    }

    public long getDbId() {
        return dbId;
    }

    public FileKind getFileKind() {
        return fileKind;
    }

    public long getSize() {
        return size;
    }
}
