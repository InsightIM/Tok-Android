package test;

import com.client.tok.utils.ByteUtil;
import junit.framework.TestCase;

public class ByteUtilTest extends TestCase {

    public void testBytes2IntHigh() {
        int value = 123;
        byte[] bytes = ByteUtil.int2BytesHigh(value);
        System.out.println("bytes to int Height:" + ByteUtil.bytes2IntHigh(bytes));
        System.out.println("bytes to int Low:" + ByteUtil.bytes2IntLow(bytes));
    }

    public void testInt2BytesHigh() {
    }
}