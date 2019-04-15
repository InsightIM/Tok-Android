package com.client.tok.ui.scan.widget;

import android.content.Context;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zbar.ZBarView;
import com.client.tok.R;
import com.client.tok.utils.ViewUtil;

import static android.content.Context.VIBRATOR_SERVICE;

public class ScanView extends FrameLayout implements QRCodeView.Delegate {
    private ScanListener mListener;
    private ZBarView mZbarView;

    public ScanView(Context context) {
        super(context);
        initView();
    }

    public ScanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ScanView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public ScanView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        ViewUtil.inflateViewById(this.getContext(), R.layout.view_scan_view, this, true);
        mZbarView = findViewById(R.id.id_zbar_view);
        mZbarView.setDelegate(this);
    }

    public void setScanListener(ScanListener listener) {
        this.mListener = listener;
    }

    public void startScan() {
        mZbarView.startSpotAndShowRect();
    }

    public void restartScan() {
        startScan();
    }

    public void stopScan() {
        mZbarView.stopSpot();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) this.getContext().getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    public void decodeFile(String path) {
        mZbarView.decodeQRCode(path);
    }

    public void destroy() {
        if (mZbarView != null) {
            mZbarView.stopCamera();
        }
        mZbarView = null;
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        vibrate();
        if (mListener != null) {
            mListener.onScanQRCodeSuccess(result);
        }
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
        if (mListener != null) {
            mListener.onCameraAmbientBrightnessChanged(isDark);
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        if (mListener != null) {
            mListener.onScanQRCodeOpenCameraError();
        }
    }

    public interface ScanListener {
        /**
         * scan result
         *
         * @param result result must not null。but parse img or Bitmap,may be null
         */
        void onScanQRCodeSuccess(String result);

        void onCameraAmbientBrightnessChanged(boolean isDark);

        void onScanQRCodeOpenCameraError();
    }
}
