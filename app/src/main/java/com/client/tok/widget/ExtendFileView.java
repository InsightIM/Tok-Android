package com.client.tok.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.client.tok.R;
import com.client.tok.pagejump.PageJumpIn;
import com.client.tok.ui.chat2.Contract;
import com.client.tok.utils.FilePicker;
import com.client.tok.utils.ViewUtil;

public class ExtendFileView extends FrameLayout implements View.OnClickListener {
    private Context mContext;
    private ImageView mAlbumIv;
    private ImageView mCameraIv;
    private ImageView mVideoIv;
    private ImageView mFileIv;
    private Contract.IChatPresenter mPresenter;

    public ExtendFileView(Context context) {
        super(context);
        mContext = context;
        initView(mContext);
    }

    public ExtendFileView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(mContext);
    }

    public ExtendFileView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView(mContext);
    }

    public ExtendFileView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        initView(mContext);
    }

    private void initView(Context context) {
        View rootView = ViewUtil.inflateViewById(context, R.layout.view_extend_file);
        this.addView(rootView);

        mAlbumIv = rootView.findViewById(R.id.id_album_iv);
        mCameraIv = rootView.findViewById(R.id.id_camera_iv);
        mVideoIv = rootView.findViewById(R.id.id_video_iv);
        mFileIv = rootView.findViewById(R.id.id_file_iv);
        mAlbumIv.setOnClickListener(this);
        mCameraIv.setOnClickListener(this);
        mVideoIv.setOnClickListener(this);
        mFileIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (mPresenter.canSendFile()) {
            switch (id) {
                case R.id.id_album_iv:
                    FilePicker.openGallery((Activity) mContext,false);
                    break;
                case R.id.id_camera_iv:
                    FilePicker.openCamera((Activity) mContext);
                    break;
                case R.id.id_video_iv:
                    PageJumpIn.jumpVideoRecordPage((Activity) mContext);
                    break;
                case R.id.id_file_iv:
                    FilePicker.openFileSel((Activity) mContext);
                    break;
            }
        }
    }

    public void setPresenter(Contract.IChatPresenter presenter) {
        mPresenter = presenter;
    }
}
