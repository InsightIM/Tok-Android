package com.client.tok.ui.imgshow;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.client.tok.R;
import com.client.tok.base.BaseTitleFullScreenActivity;
import com.client.tok.pagejump.IntentConstants;
import com.client.tok.widget.ScaleImageView;

public class ImgShowActivity extends BaseTitleFullScreenActivity {
    private ScaleImageView mScaleIv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_show);

        mScaleIv = $(R.id.id_img_show_iv);
        String file = getIntent().getStringExtra(IntentConstants.FILE);
        mScaleIv.setImgFile(file);
    }
}
