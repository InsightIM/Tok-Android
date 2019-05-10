package com.client.tok.ui.scan.decoder;

import android.content.Context;
import android.net.Uri;
import cn.simonlee.xcodescanner.core.GraphicDecoder;
import cn.simonlee.xcodescanner.core.ZBarDecoder;

public class Decoder {
    private int REQ_CODE = 3000;
    private int[] mCodeTypeArray = new int[] {
        ZBarDecoder.CODABAR, ZBarDecoder.CODE39, ZBarDecoder.CODE93, ZBarDecoder.CODE128,
        ZBarDecoder.DATABAR, ZBarDecoder.DATABAR_EXP, ZBarDecoder.EAN8, ZBarDecoder.EAN13,
        ZBarDecoder.I25, ZBarDecoder.ISBN10, ZBarDecoder.ISBN13, ZBarDecoder.PDF417,
        ZBarDecoder.QRCODE, ZBarDecoder.UPCA, ZBarDecoder.UPCE
    };

    private GraphicDecoder decoder;

    public Decoder(DecoderResultListener listener) {
        decoder = new ZBarDecoder(listener, getDefaultType());
    }

    public void decoder(Context context, Uri uri) {
        decoder.decodeForResult(context, uri, REQ_CODE);
    }

    public int[] getDefaultType() {
        return mCodeTypeArray;
    }

    public void destroy() {
        if (decoder != null) {
            decoder.setDecodeListener(null);
            decoder.detach();
        }
        decoder = null;
    }

    public interface DecoderResultListener extends GraphicDecoder.DecodeListener {

    }

    public GraphicDecoder getDecoder() {
        return decoder;
    }
}
