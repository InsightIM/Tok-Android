package com.client.tok.utils;

import java.nio.ByteBuffer;

public class ByteUtil {
    public static byte int2Byte(int x) {
        return (byte) x;
    }

    public static byte[] int2OneByte(int x) {
        return new byte[] { (byte) x };
    }

    public static int byte2Int(byte b) {
        return b & 0xFF;
    }

    /**
     * Low bytes apply to this project
     */
    public static int bytes2IntLow(byte[] bytes, int offset) {
        int value = -1;
        if (bytes != null && offset >= 0 && offset < bytes.length) {
            value = (bytes[offset] & 0xFF) | ((bytes[offset + 1] & 0xFF) << 8) | ((bytes[offset + 2]
                & 0xFF) << 16) | ((bytes[offset + 3] & 0xFF) << 24);
        }
        return value;
    }

    public static int bytes2IntLow(byte[] bytes) {
        return bytes2IntLow(bytes, 0);
    }

    /**
     * Height bytes
     */
    public static int bytes2IntHigh(byte[] bytes, int offset) {
        int value = -1;
        if (bytes != null && offset >= 0 && offset < bytes.length) {
            value = ((bytes[offset] & 0xFF) << 24) | ((bytes[offset + 1] & 0xFF) << 16) | ((bytes[
                offset
                    + 2] & 0xFF) << 8) | (bytes[offset + 3] & 0xFF);
        }
        return value;
    }

    /**
     * byte to int:height byte
     */
    public static int bytes2IntHigh(byte[] bytes) {
        return bytes2IntHigh(bytes, 0);
    }

    /**
     * low byte array
     */
    public static byte[] int2BytesLow(int value) {
        byte[] bytes = new byte[4];
        bytes[3] = (byte) ((value >> 24) & 0xFF);
        bytes[2] = (byte) ((value >> 16) & 0xFF);
        bytes[1] = (byte) ((value >> 8) & 0xFF);
        bytes[0] = (byte) (value & 0xFF);
        return bytes;
    }

    /**
     * height byte
     */
    public static byte[] int2BytesHigh(int value) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) ((value >> 24) & 0xFF);
        bytes[1] = (byte) ((value >> 16) & 0xFF);
        bytes[2] = (byte) ((value >> 8) & 0xFF);
        bytes[3] = (byte) (value & 0xFF);
        return bytes;
    }

    /**
     * long to bytes
     */
    public static byte[] long2Bytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(0, x);
        return buffer.array();
    }

    public static long bytes2Long(byte[] bytes) {
        if (bytes != null) {
            ByteBuffer buffer = ByteBuffer.allocate(8);
            buffer.put(bytes, 0, bytes.length);
            buffer.flip();//need flip
            return buffer.getLong();
        }
        return -1L;
    }

    public static byte[] byteSplitFirst(byte[] byteArray) {
        if (byteArray != null) {
            int index = 1;
            return byteSplit(byteArray, index);
        }
        return null;
    }

    public static byte[] byteSplit(byte[] byteArray, int index) {
        if (byteArray != null && index > 0 && index < byteArray.length - 1) {
            int length = byteArray.length - index;
            return byteSplit(byteArray, index, length);
        }
        return byteArray;
    }

    public static byte[] byteSplit(byte[] byteArray, int index, int length) {
        if (byteArray != null && index > 0 && index < byteArray.length - 1) {
            byte[] newArray = new byte[length];
            System.arraycopy(byteArray, index, newArray, 0, length);
            return newArray;
        }
        return byteArray;
    }

    public static String byte2Str(byte[] byteArray) {
        if (byteArray != null) {
            return new String(byteArray).trim();
        }
        return "";
    }

    public static byte[] str2Byte(String str) {
        if (str != null) {
            return str.getBytes();
        }
        return null;
    }

    public static byte[] byteMergerAll(byte[]... values) {
        int lengthByte = 0;
        for (byte[] bytes : values) {
            lengthByte += bytes.length;
        }
        byte[] allByte = new byte[lengthByte];
        int countLength = 0;
        for (byte[] bytes : values) {
            System.arraycopy(bytes, 0, allByte, countLength, bytes.length);
            countLength += bytes.length;
        }
        return allByte;
    }

    public static byte[] hexStr2Bytes(String hexStr) {
        if (hexStr == null) {
            return null;
        }
        if (hexStr.length() == 0) {
            return new byte[0];
        }
        byte[] byteArray = new byte[hexStr.length() / 2];
        for (int i = 0; i < byteArray.length; i++) {
            String subStr = hexStr.substring(2 * i, 2 * i + 2);
            byteArray[i] = ((byte) Integer.parseInt(subStr, 16));
        }
        return byteArray;
    }

    public static String bytes2HexStr(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String byteArrayToStr(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        return new String(byteArray);
    }
}
