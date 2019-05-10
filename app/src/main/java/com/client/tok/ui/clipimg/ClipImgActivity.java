package com.client.tok.ui.clipimg;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.base.BaseTitleFullScreenActivity;
import com.client.tok.utils.FileUtilsJ;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * author: https://github.com/msdx/clip-image
 */
public class ClipImgActivity extends BaseTitleFullScreenActivity implements View.OnClickListener {

    private ClipImageView mClipImageView;
    private TextView mCancel;
    private TextView mClip;

    private String mOutput;
    private String mInput;
    private int mMaxWidth;

    private int mDegree;
    private int mSampleSize;
    private int mSourceWidth;
    private int mSourceHeight;

    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_clip_image);
        mClipImageView = findViewById(R.id.clip_image_view);
        mCancel = findViewById(R.id.cancel);
        mClip = findViewById(R.id.clip);

        mCancel.setOnClickListener(this);
        mClip.setOnClickListener(this);

        ClipOptions clipOptions = ClipOptions.createFromBundle(getIntent());
        mOutput = clipOptions.getOutputPath();
        mInput = clipOptions.getInputPath();
        mMaxWidth = clipOptions.getMaxWidth();
        mClipImageView.setAspect(clipOptions.getAspectX(), clipOptions.getAspectY());
        mClipImageView.setTip(clipOptions.getTip());
        mClipImageView.setMaxOutputWidth(mMaxWidth);

        setImageAndClipParams();
        mClipImageView.setImageURI(Uri.fromFile(new File(mInput)));
        mDialog = new ProgressDialog(this);
        mDialog.setMessage(getString(R.string.clipping_image));
    }

    private void setImageAndClipParams() {
        mClipImageView.post(new Runnable() {
            @Override
            public void run() {
                mClipImageView.setMaxOutputWidth(mMaxWidth);

                mDegree = readPictureDegree(mInput);

                final boolean isRotate = (mDegree == 90 || mDegree == 270);

                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(mInput, options);

                mSourceWidth = options.outWidth;
                mSourceHeight = options.outHeight;

                int w = isRotate ? options.outHeight : options.outWidth;

                mSampleSize = findBestSample(w, mClipImageView.getClipBorder().width());

                options.inJustDecodeBounds = false;
                options.inSampleSize = mSampleSize;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                final Bitmap source = BitmapFactory.decodeFile(mInput, options);

                Bitmap target;
                if (mDegree == 0) {
                    target = source;
                } else {
                    final Matrix matrix = new Matrix();
                    matrix.postRotate(mDegree);
                    target =
                        Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                            matrix, false);
                    if (target != source && !source.isRecycled()) {
                        source.recycle();
                    }
                }
                mClipImageView.setImageBitmap(target);
            }
        });
    }

    private static int findBestSample(int origin, int target) {
        int sample = 1;
        for (int out = origin / 2; out > target; out /= 2) {
            sample *= 2;
        }
        return sample;
    }

    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.cancel) {
            onBackPressed();
        }
        if (id == R.id.clip) {
            clipImage();
        }
    }

    private void clipImage() {
        if (mOutput != null) {
            mDialog.show();
            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(mOutput);
                        Bitmap bitmap = createClippedBitmap();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        if (!bitmap.isRecycled()) {
                            bitmap.recycle();
                        }
                        setResult(Activity.RESULT_OK, getIntent());
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        FileUtilsJ.close(fos);
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    mDialog.dismiss();
                    finish();
                }
            };
            task.execute();
        } else {
            finish();
        }
    }

    public static void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap createClippedBitmap() {
        //if (mSampleSize <= 1) {
        // TODO has problem, this method is not useful on some picture
        //    return mClipImageView.clip();
        //}

        final float[] matrixValues = mClipImageView.getClipMatrixValues();
        final float scale = matrixValues[Matrix.MSCALE_X];
        final float transX = matrixValues[Matrix.MTRANS_X];
        final float transY = matrixValues[Matrix.MTRANS_Y];

        final Rect border = mClipImageView.getClipBorder();
        final float cropX = ((-transX + border.left) / scale) * mSampleSize;
        final float cropY = ((-transY + border.top) / scale) * mSampleSize;
        final float cropWidth = (border.width() / scale) * mSampleSize;
        final float cropHeight = (border.height() / scale) * mSampleSize;

        final RectF srcRect = new RectF(cropX, cropY, cropX + cropWidth, cropY + cropHeight);
        final Rect clipRect = getRealRect(srcRect);

        final BitmapFactory.Options ops = new BitmapFactory.Options();
        final Matrix outputMatrix = new Matrix();

        outputMatrix.setRotate(mDegree);
        if (mMaxWidth > 0 && cropWidth > mMaxWidth) {
            ops.inSampleSize = findBestSample((int) cropWidth, mMaxWidth);

            final float outputScale = mMaxWidth / (cropWidth / ops.inSampleSize);
            outputMatrix.postScale(outputScale, outputScale);
        }

        BitmapRegionDecoder decoder = null;
        try {
            decoder = BitmapRegionDecoder.newInstance(mInput, false);
            final Bitmap source = decoder.decodeRegion(clipRect, ops);
            recycleImageViewBitmap();
            return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                outputMatrix, false);
        } catch (Exception e) {
            return mClipImageView.clip();
        } finally {
            if (decoder != null && !decoder.isRecycled()) {
                decoder.recycle();
            }
        }
    }

    private Rect getRealRect(RectF srcRect) {
        switch (mDegree) {
            case 90:
                return new Rect((int) srcRect.top, (int) (mSourceHeight - srcRect.right),
                    (int) srcRect.bottom, (int) (mSourceHeight - srcRect.left));
            case 180:
                return new Rect((int) (mSourceWidth - srcRect.right),
                    (int) (mSourceHeight - srcRect.bottom), (int) (mSourceWidth - srcRect.left),
                    (int) (mSourceHeight - srcRect.top));
            case 270:
                return new Rect((int) (mSourceWidth - srcRect.bottom), (int) srcRect.left,
                    (int) (mSourceWidth - srcRect.top), (int) srcRect.right);
            default:
                return new Rect((int) srcRect.left, (int) srcRect.top, (int) srcRect.right,
                    (int) srcRect.bottom);
        }
    }

    private void recycleImageViewBitmap() {
        mClipImageView.post(new Runnable() {
            @Override
            public void run() {
                mClipImageView.setImageBitmap(null);
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED, getIntent());
        super.onBackPressed();
    }

    public static ClipOptions prepare() {
        return new ClipOptions();
    }

    public static class ClipOptions {
        private int aspectX;
        private int aspectY;
        private int maxWidth;
        private String tip;
        private String inputPath;
        private String outputPath;

        private ClipOptions() {
        }

        public ClipOptions aspectX(int aspectX) {
            this.aspectX = aspectX;
            return this;
        }

        public ClipOptions aspectY(int aspectY) {
            this.aspectY = aspectY;
            return this;
        }

        public ClipOptions maxWidth(int maxWidth) {
            this.maxWidth = maxWidth;
            return this;
        }

        public ClipOptions tip(String tip) {
            this.tip = tip;
            return this;
        }

        public ClipOptions inputPath(String path) {
            this.inputPath = path;
            return this;
        }

        public ClipOptions outputPath(String path) {
            this.outputPath = path;
            return this;
        }

        public int getAspectX() {
            return aspectX;
        }

        public int getAspectY() {
            return aspectY;
        }

        public int getMaxWidth() {
            return maxWidth;
        }

        public String getTip() {
            return tip;
        }

        public String getInputPath() {
            return inputPath;
        }

        public String getOutputPath() {
            return outputPath;
        }

        public void startForResult(Activity activity, int requestCode) {
            checkValues();
            Intent intent = new Intent(activity, ClipImgActivity.class);
            intent.putExtra("aspectX", aspectX);
            intent.putExtra("aspectY", aspectY);
            intent.putExtra("maxWidth", maxWidth);
            intent.putExtra("tip", tip);
            intent.putExtra("inputPath", inputPath);
            intent.putExtra("outputPath", outputPath);
            activity.startActivityForResult(intent, requestCode);
        }

        private void checkValues() {
            if (TextUtils.isEmpty(inputPath)) {
                throw new IllegalArgumentException("The input path could not be empty");
            }
            if (TextUtils.isEmpty(outputPath)) {
                throw new IllegalArgumentException("The output path could not be empty");
            }
        }

        public static ClipOptions createFromBundle(Intent intent) {
            return new ClipOptions().aspectX(intent.getIntExtra("aspectX", 1))
                .aspectY(intent.getIntExtra("aspectY", 1))
                .maxWidth(intent.getIntExtra("maxWidth", 0))
                .tip(intent.getStringExtra("tip"))
                .inputPath(intent.getStringExtra("inputPath"))
                .outputPath(intent.getStringExtra("outputPath"));
        }
    }
}