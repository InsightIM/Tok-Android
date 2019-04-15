package com.client.tok.ui.filecore;

import com.client.tok.utils.LogUtil;
import com.client.tok.utils.StringUtils;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import im.tox.proto.Group;

public class TransFilePbUtil {
    private static String TAG = "TransFilePbUtil";
    public static byte[] builderPb(TransFileInfo transFileInfo) {
        Group.FileTransfer.Builder builder = Group.FileTransfer.newBuilder();
        builder.setFileType(transFileInfo.getChatType());
        builder.setToGroup(transFileInfo.getToGroupNumber());
        builder.setRealName(ByteString.copyFrom(transFileInfo.getFileName().getBytes()));
        if (transFileInfo.getToOrFromPk() != null) {
            builder.setToPk(
                ByteString.copyFrom(transFileInfo.getToOrFromPk().toUpperCase().getBytes()));
        }
        return builder.build().toByteArray();
    }

    public static TransFileInfo parserPb(byte[] pbBytes) {
        TransFileInfo transFileInfo = new TransFileInfo();
        try {
            Group.FileTransfer fileTransfer = Group.FileTransfer.parseFrom(pbBytes);
            transFileInfo.setChatType(fileTransfer.getFileType());
            transFileInfo.setToGroupNumber(fileTransfer.getToGroup());
            transFileInfo.setFileName(StringUtils.byte2Str(fileTransfer.getRealName()));
            transFileInfo.setToOrFromPk(StringUtils.byte2Str(fileTransfer.getToPk()).toUpperCase());
            transFileInfo.setCreateTime(fileTransfer.getCreateTime());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        LogUtil.i(TAG,"TransFilePb:"+transFileInfo.toString());
        return transFileInfo;
    }
}
