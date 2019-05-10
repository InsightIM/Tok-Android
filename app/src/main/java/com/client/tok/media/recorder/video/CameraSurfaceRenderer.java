package com.client.tok.media.recorder.video;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import com.client.tok.utils.LogUtil;
import java.lang.ref.WeakReference;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CameraSurfaceRenderer
    implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
    private String TAG = "CameraGLView";

    protected static final int SCALE_STRETCH_FIT = 0;
    protected static final int SCALE_KEEP_ASPECT_VIEWPORT = 1;
    protected static final int SCALE_KEEP_ASPECT = 2;
    protected static final int SCALE_CROP_CENTER = 3;

    private final WeakReference<CameraGLView> mWeakParent;
    protected SurfaceTexture mSTexture;
    protected MediaVideoEncoder mVideoEncoder;

    protected int hTex;
    private GLDrawer2D mDrawer;
    private final float[] mStMatrix = new float[16];
    private final float[] mMvpMatrix = new float[16];
    private volatile boolean requesrUpdateTex = false;
    private boolean flip = true;

    public CameraSurfaceRenderer(CameraGLView parent) {
        LogUtil.i(TAG, "cameraSurfaceRender contractor");
        mWeakParent = new WeakReference<CameraGLView>(parent);
        Matrix.setIdentityM(mMvpMatrix, 0);
    }

    public void updateViewport() {
        LogUtil.i(TAG, "updateViewPort");
        final CameraGLView parent = mWeakParent.get();
        if (parent != null) {
            final int view_width = parent.getWidth();
            final int view_height = parent.getHeight();
            GLES20.glViewport(0, 0, view_width, view_height);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            final double video_width = parent.mVideoWidth;
            final double video_height = parent.mVideoHeight;
            if (video_width == 0 || video_height == 0) return;
            Matrix.setIdentityM(mMvpMatrix, 0);
            final double view_aspect = view_width / (double) view_height;
            Log.i(TAG, String.format("view(%d,%d)%f,video(%1.0f,%1.0f)", view_width, view_height,
                view_aspect, video_width, video_height));
            switch (parent.mScaleMode) {
                case SCALE_STRETCH_FIT:
                    break;
                case SCALE_KEEP_ASPECT_VIEWPORT: {
                    final double req = video_width / video_height;
                    int x, y;
                    int width, height;
                    if (view_aspect > req) {
                        // if view is wider than camera image, calc width of drawing area based on view height
                        y = 0;
                        height = view_height;
                        width = (int) (req * view_height);
                        x = (view_width - width) / 2;
                    } else {
                        // if view is higher than camera image, calc height of drawing area based on view width
                        x = 0;
                        width = view_width;
                        height = (int) (view_width / req);
                        y = (view_height - height) / 2;
                    }
                    // set viewport to draw keeping aspect ration of camera image
                    LogUtil.i(TAG, "x:" + x + ",y:" + y + ",width:" + width + ",height:" + height);
                    GLES20.glViewport(x, y, width, height);
                    break;
                }
                case SCALE_KEEP_ASPECT:
                case SCALE_CROP_CENTER: {
                    final double scale_x = view_width / video_width;
                    final double scale_y = view_height / video_height;
                    final double scale =
                        (parent.mScaleMode == SCALE_CROP_CENTER ? Math.max(scale_x, scale_y)
                            : Math.min(scale_x, scale_y));
                    final double width = scale * video_width;
                    final double height = scale * video_height;
                    Log.v(TAG,
                        String.format("size(%1.0f,%1.0f),scale(%f,%f),mat(%f,%f)", width, height,
                            scale_x, scale_y, width / view_width, height / view_height));
                    Matrix.scaleM(mMvpMatrix, 0, (float) (width / view_width),
                        (float) (height / view_height), 1.0f);
                    break;
                }
            }
            if (mDrawer != null) mDrawer.setMatrix(mMvpMatrix, 0);
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        LogUtil.i(TAG, "onSurfaceCreated:");
        // This renderer required OES_EGL_image_external extension
        final String extensions = GLES20.glGetString(GLES20.GL_EXTENSIONS);    // API >= 8
        if (!extensions.contains("OES_EGL_image_external")) {
            throw new RuntimeException("This system does not support OES_EGL_image_external.");
        }
        // create textur ID
        hTex = GLDrawer2D.initTex();
        // create SurfaceTexture with texture ID.
        mSTexture = new SurfaceTexture(hTex);
        mSTexture.setOnFrameAvailableListener(this);
        // clear screen with yellow color so that you can see rendering rectangle
        GLES20.glClearColor(1.0f, 1.0f, 0.0f, 1.0f);
        final CameraGLView parent = mWeakParent.get();
        if (parent != null) {
            parent.mHasSurface = true;
        }
        // create object for preview display
        mDrawer = new GLDrawer2D();
        mDrawer.setMatrix(mMvpMatrix, 0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        LogUtil.i(TAG, "onSurfaceChanged  width=" + width + ",height:" + height);
        // if at least with or height is zero, initialization of this view is still progress.
        if ((width == 0) || (height == 0)) return;
        updateViewport();
        final CameraGLView parent = mWeakParent.get();
        if (parent != null) {
            parent.startPreview(width, height);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //LogUtil.i(TAG, "onDrawFrame.....");
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        if (requesrUpdateTex) {
            requesrUpdateTex = false;
            // update texture(came from camera)
            mSTexture.updateTexImage();
            // get texture matrix
            mSTexture.getTransformMatrix(mStMatrix);
        }
        // draw to preview screen
        mDrawer.draw(hTex, mStMatrix);
        flip = !flip;
        if (flip) {    // ~30fps
            synchronized (this) {
                if (mVideoEncoder != null) {
                    // notify to capturing thread that the camera frame is available.
                    mVideoEncoder.frameAvailableSoon(mStMatrix);
                    //mVideoEncoder.frameAvailableSoon(mStMatrix, mMvpMatrix);
                }
            }
        }
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        //LogUtil.i(TAG, "onFrameAvailable.....");
        requesrUpdateTex = true;
    }

    /**
     * when GLSurface context is soon destroyed
     */
    public void onSurfaceDestroyed() {
        LogUtil.i(TAG, "onSurfaceDestroyed:");
        if (mDrawer != null) {
            mDrawer.release();
            mDrawer = null;
        }
        if (mSTexture != null) {
            mSTexture.release();
            mSTexture = null;
        }
        GLDrawer2D.deleteTex(hTex);
    }
}
