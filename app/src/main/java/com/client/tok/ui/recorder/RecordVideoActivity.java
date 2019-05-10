package com.client.tok.ui.recorder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import com.client.tok.R;
import com.client.tok.base.BaseCommonTitleActivity;
import com.client.tok.utils.FileUtilsJ;

@Deprecated
public class RecordVideoActivity extends BaseCommonTitleActivity {
    private JCameraView mJCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_video);

        mJCameraView = (JCameraView) findViewById(R.id.cameraView);
        mJCameraView.setActivity(this);
        //设置视频保存路径（如果不设置默认为Environment.getExternalStorageDirectory().getPath()）
        mJCameraView.setAutoFocus(false);
        //        mJCameraView.setSaveVideoPath(Environment.getExternalStorageDirectory().getPath());
        mJCameraView.setCameraViewListener(new JCameraView.CameraViewListener() {
            @Override
            public void quit() {
                RecordVideoActivity.this.finish();
            }

            @Override
            public void captureSuccess(Bitmap bitmap) {//拍照成功
                String url = FileUtilsJ.saveBitmap(bitmap);
                Intent intent = new Intent();
                //把返回数据存入Intent 0是照片,1是视频
                intent.putExtra("url", url);
                intent.putExtra("type", 0);
                //设置返回数据
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void recordSuccess(String url) {//录视频成功
                Intent intent = new Intent();
                //把返回数据存入Intent 0是照片,1是视频
                intent.putExtra("url", url);
                intent.putExtra("type", 1);
                //设置返回数据
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public int getToolBarStyle() {
        return STATUS_BAR_FULL_SCREEN_TRANSLATE;
    }

    @Override
    public boolean isShowToolBar() {
        return true;
    }

    @Override
    public boolean isShowBackIcon() {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mJCameraView.onResume();
    }

    @Override
    protected void onPause() {
        mJCameraView.onPause();
        super.onPause();
    }
}
