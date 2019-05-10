package com.client.tok.media.recorder.video;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.EGL14;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import com.client.tok.utils.LogUtil;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Sub class of GLSurfaceView to display camera preview and write video frame to capturing surface
 */
public class CameraGLView extends GLSurfaceView {
    private String TAG = "CameraGLView";

    public static String FLASH_OFF = Camera.Parameters.FLASH_MODE_OFF;
    public static String FLASH_ON = Camera.Parameters.FLASH_MODE_TORCH;
    public static String FLASH_AUTO = Camera.Parameters.FLASH_MODE_AUTO;

    public static int CAMERA_BACK = 0;
    public static int CAMERA_FRONT = 1;

    private static String mFlashMode = FLASH_OFF;
    private static int mCameraId = 0;

    private CameraSurfaceRenderer mRenderer;
    private CameraHandler mCameraHandler;
    protected boolean mHasSurface;

    protected int mVideoWidth, mVideoHeight;
    private int mRotation;
    protected int mScaleMode = CameraSurfaceRenderer.SCALE_CROP_CENTER;

    public CameraGLView(Context context) {
        this(context, null);
    }

    public CameraGLView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraGLView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs);
        LogUtil.i(TAG, "CameraGLView constructor");
        mRenderer = new CameraSurfaceRenderer(this);
        setEGLContextClientVersion(2);
        setRenderer(mRenderer);
        // the frequency of refreshing of camera preview is at most 15 fps TODO how much fps
        // and RENDERMODE_WHEN_DIRTY is better to reduce power consumption*/
        //setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.i(TAG,
            "onResume camearHandler=" + (mCameraHandler == null) + ",hasSurface:" + mHasSurface);
        if (mHasSurface) {
            if (mCameraHandler == null) {
                LogUtil.i(TAG, "onResume startPreview");
                startPreview(getWidth(), getHeight());
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.i(TAG, "onPause");
        if (mCameraHandler != null) {
            mCameraHandler.stopPreview(false);
        }
    }

    public void setScaleMode(int mode) {
        LogUtil.i(TAG, "setScaleMode mScaleMode:" + mScaleMode + ",newMode:" + mode);
        if (mScaleMode != mode) {
            mScaleMode = mode;
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    mRenderer.updateViewport();
                }
            });
        }
    }

    public void setFlashMode(String mode) {
        if (mFlashMode.equals(mode)) {
            return;
        }
        mFlashMode = mode;
        startPreview(getWidth(), getHeight());
    }

    public void setCameraId(int cameraId) {
        if (cameraId == mCameraId) {
            return;
        }
        mCameraId = cameraId;
        startPreview(getWidth(), getHeight());
    }

    public int getScaleMode() {
        return mScaleMode;
    }

    public void setVideoSize(final int width, final int height) {
        LogUtil.i(TAG, "mRotation:" + mRotation);
        if ((mRotation % 180) == 0) {
            mVideoWidth = width;
            mVideoHeight = height;
        } else {
            mVideoWidth = height;
            mVideoHeight = width;
        }
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mRenderer.updateViewport();
            }
        });
    }

    public int getVideoWidth() {
        return mVideoWidth;
    }

    public int getVideoHeight() {
        return mVideoHeight;
    }

    public SurfaceTexture getSurfaceTexture() {
        LogUtil.i(TAG, "getSurfaceTexture");
        return mRenderer != null ? mRenderer.mSTexture : null;
    }

    protected void startPreview(int width, int height) {
        if (mCameraHandler == null) {
            final CameraThread thread = new CameraThread(this);
            thread.start();
            mCameraHandler = thread.getHandler();
        }
        mCameraHandler.startPreview(width, height);
    }

    public void setVideoEncoder(final MediaVideoEncoder encoder) {
        LogUtil.i(TAG, "setVideoEncoder:tex_id=" + mRenderer.hTex + ",encoder=" + encoder);
        queueEvent(new Runnable() {
            @Override
            public void run() {
                synchronized (mRenderer) {
                    if (encoder != null) {
                        encoder.setEglContext(EGL14.eglGetCurrentContext(), mRenderer.hTex);
                    }
                    mRenderer.mVideoEncoder = encoder;
                }
            }
        });
    }

    @Override
    public void surfaceDestroyed(final SurfaceHolder holder) {
        LogUtil.i(TAG, "surfaceDestroyed");
        if (mCameraHandler != null) {
            // wait for finish previewing here
            // otherwise camera try to display on un-exist Surface and some error will occure
            mCameraHandler.stopPreview(true);
        }
        mCameraHandler = null;
        mHasSurface = false;
        mRenderer.onSurfaceDestroyed();
        super.surfaceDestroyed(holder);
    }

    public class CameraHandler extends Handler {

        private static final int MSG_PREVIEW_START = 1;
        private static final int MSG_PREVIEW_STOP = 2;
        private CameraThread mThread;

        public CameraHandler(final CameraThread thread) {
            mThread = thread;
        }

        public void startPreviewDelay(final int width, final int height) {
            sendMessageDelayed(obtainMessage(MSG_PREVIEW_START, width, height), 500);
        }

        public void startPreview(final int width, final int height) {
            sendMessage(obtainMessage(MSG_PREVIEW_START, width, height));
        }

        /**
         * request to stop camera preview
         *
         * @param needWait need to wait for stopping camera preview
         */
        public void stopPreview(final boolean needWait) {
            synchronized (this) {
                sendEmptyMessage(MSG_PREVIEW_STOP);
                if (needWait && mThread.mIsRunning) {
                    try {
                        LogUtil.i(TAG, "wait for terminating of camera thread");
                        wait();
                    } catch (final InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        /**
         * message handler for camera thread
         */
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case MSG_PREVIEW_START:
                    mThread.startPreview(msg.arg1, msg.arg2);
                    break;
                case MSG_PREVIEW_STOP:
                    mThread.stopPreview();
                    synchronized (this) {
                        notifyAll();
                    }
                    Looper.myLooper().quit();
                    mThread = null;
                    break;
                default:
                    throw new RuntimeException("unknown message:what=" + msg.what);
            }
        }
    }

    /**
     * Thread for asynchronous operation of camera preview
     */
    public final class CameraThread extends Thread {
        private String TAG = "CameraGLView";
        private final Object mReadyFence = new Object();
        private final WeakReference<CameraGLView> mWeakParent;
        private CameraGLView.CameraHandler mHandler;
        private volatile boolean mIsRunning = false;
        protected Camera mCamera;
        private boolean mIsFrontFace;

        public CameraThread(final CameraGLView parent) {
            super("Camera thread");
            mWeakParent = new WeakReference<CameraGLView>(parent);
        }

        public CameraGLView.CameraHandler getHandler() {
            synchronized (mReadyFence) {
                try {
                    mReadyFence.wait();
                } catch (final InterruptedException e) {
                }
            }
            return mHandler;
        }

        /**
         * message loop
         * prepare Looper and create Handler for this thread
         */
        @Override
        public void run() {
            LogUtil.i(TAG, "Camera thread start");
            Looper.prepare();
            synchronized (mReadyFence) {
                mHandler = new CameraGLView.CameraHandler(this);
                mIsRunning = true;
                mReadyFence.notify();
            }
            Looper.loop();
            LogUtil.i(TAG, "Camera thread finish");
            synchronized (mReadyFence) {
                mHandler = null;
                mIsRunning = false;
            }
        }

        /**
         * start camera preview
         */
        private final void startPreview(int width, int height) {
            width = mVideoWidth;
            height = mVideoHeight;
            LogUtil.i(TAG, "startPreview:width:" + width + ",height:" + height);
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
            final CameraGLView parent = mWeakParent.get();
            if ((parent != null) && (mCamera == null)) {
                // This is a sample project so just use 0 as camera ID.
                // it is better to selecting camera is available
                try {
                    mCamera = Camera.open(mCameraId);
                    final Camera.Parameters params = mCamera.getParameters();
                    final List<String> focusModes = params.getSupportedFocusModes();
                    if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                    } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    } else {
                        LogUtil.i(TAG, "Camera does not support autofocus");
                    }
                    //flash mode
                    LogUtil.i(TAG, "curFlashMode:" + mFlashMode);
                    List<String> flashModes = params.getSupportedFlashModes();
                    for (String mode : flashModes) {
                        LogUtil.i(TAG, "flash mode:" + mode);
                    }
                    if (flashModes.contains(mFlashMode)) {
                        params.setFlashMode(mFlashMode);
                    }
                    // let's try fastest frame rate. You will get near 60fps, but your device become hot.
                    final List<int[]> supportedFpsRange = params.getSupportedPreviewFpsRange();
                    //final int n = supportedFpsRange != null ? supportedFpsRange.size() : 0;
                    //int[] range;
                    //for (int i = 0; i < n; i++) {
                    //range = supportedFpsRange.get(i);
                    //}
                    final int[] max_fps = supportedFpsRange.get(supportedFpsRange.size() - 1);
                    Log.i(TAG, String.format("fps:%d-%d", max_fps[0], max_fps[1]));
                    params.setPreviewFpsRange(max_fps[0], max_fps[1]);
                    params.setRecordingHint(true);
                    // request closest supported preview size
                    final Camera.Size closestSize =
                        getClosestSupportedSize(params.getSupportedPreviewSizes(), width, height);
                    params.setPreviewSize(closestSize.width, closestSize.height);

                    //for (Camera.Size size : params.getSupportedPreviewSizes()) {
                    //    Log.i(TAG,
                    //        String.format("support previewSize(%d, %d)", size.width, size.height));
                    //}
                    //
                    //for (Camera.Size size : params.getSupportedPictureSizes()) {
                    //    Log.i(TAG,
                    //        String.format("support pictureSize(%d, %d)", size.width, size.height));
                    //}

                    // request closest picture size for an aspect ratio issue on Nexus7
                    final Camera.Size pictureSize =
                        getClosestSupportedSize(params.getSupportedPictureSizes(), width, height);
                    params.setPictureSize(pictureSize.width, pictureSize.height);

                    // rotate camera preview according to the device orientation
                    setRotation(params);
                    mCamera.setParameters(params);
                    // get the actual preview size
                    final Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
                    LogUtil.i(TAG,
                        "previewSize width:" + previewSize.width + ",height:" + previewSize.height);
                    LogUtil.i(TAG,
                        "picture width:" + pictureSize.width + ",height:" + pictureSize.height);
                    // adjust view size with keeping the aspect ration of camera preview.
                    // here is not a UI thread and we should request parent view to execute.
                    parent.post(new Runnable() {
                        @Override
                        public void run() {
                            parent.setVideoSize(pictureSize.width, pictureSize.height);
                        }
                    });
                    final SurfaceTexture st = parent.getSurfaceTexture();
                    st.setDefaultBufferSize(pictureSize.width, pictureSize.height);
                    mCamera.setPreviewTexture(st);
                } catch (final IOException e) {
                    Log.e(TAG, "startPreview:", e);
                    if (mCamera != null) {
                        mCamera.release();
                        mCamera = null;
                    }
                } catch (RuntimeException e) {
                    Log.e(TAG, "startPreview:", e);
                    if (mCamera != null) {
                        mCamera.release();
                        mCamera = null;
                    }
                }
                if (mCamera != null) {
                    // start camera preview display
                    mCamera.startPreview();
                }
            }
        }

        private Camera.Size getClosestSupportedSize(List<Camera.Size> supportedSizes,
            final int requestedWidth, final int requestedHeight) {
            return (Camera.Size) Collections.min(supportedSizes, new Comparator<Camera.Size>() {

                private int diff(final Camera.Size size) {
                    return Math.abs(requestedWidth - size.width) + Math.abs(
                        requestedHeight - size.height);
                }

                @Override
                public int compare(final Camera.Size lhs, final Camera.Size rhs) {
                    return diff(lhs) - diff(rhs);
                }
            });
        }

        /**
         * stop camera preview
         */
        private void stopPreview() {
            LogUtil.i(TAG, "stopPreview:");
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
            final CameraGLView parent = mWeakParent.get();
            if (parent == null) return;
            parent.mCameraHandler = null;
        }

        /**
         * rotate preview screen according to the device orientation
         */
        private final void setRotation(final Camera.Parameters params) {
            LogUtil.i(TAG, "setRotation:");
            final CameraGLView parent = mWeakParent.get();
            if (parent == null) return;

            final Display display = ((WindowManager) parent.getContext()
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            final int rotation = display.getRotation();
            int degrees = 0;
            switch (rotation) {
                case Surface.ROTATION_0:
                    degrees = 0;
                    break;
                case Surface.ROTATION_90:
                    degrees = 90;
                    break;
                case Surface.ROTATION_180:
                    degrees = 180;
                    break;
                case Surface.ROTATION_270:
                    degrees = 270;
                    break;
            }
            // get whether the camera is front camera or back camera
            final Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
            android.hardware.Camera.getCameraInfo(mCameraId, info);
            mIsFrontFace = (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT);
            if (mIsFrontFace) {    // front camera
                degrees = (info.orientation + degrees) % 360;
                degrees = (360 - degrees) % 360;  // reverse
            } else {  // back camera
                degrees = (info.orientation - degrees + 360) % 360;
            }
            // apply rotation setting
            mCamera.setDisplayOrientation(degrees);
            parent.mRotation = degrees;
            // XXX This method fails to call and camera stops working on some devices.
            //params.setRotation(degrees);
        }
    }
}
