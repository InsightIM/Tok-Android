package com.client.tok.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.utils.ImageLoadUtils;
import com.client.tok.utils.StringUtils;
import com.client.tok.utils.ViewUtil;

public class ShareView extends FrameLayout {
    private PortraitView mPortraitView;
    private TextView mAddMeInfoTv;
    private ImageView mQrCodeIv;

    public ShareView(Context context) {
        super(context);
    }

    public ShareView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ShareView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public ShareView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        View rootView = ViewUtil.inflateViewById(context, R.layout.view_share_info);
        this.addView(rootView);
        mPortraitView = this.findViewById(R.id.id_view_share_pv);
        mAddMeInfoTv = this.findViewById(R.id.id_view_share_my_info_tv);
        mQrCodeIv = this.findViewById(R.id.id_view_share_my_qr_iv);
    }

    public void setInfo(String key, String name, String qrPath) {
        mPortraitView.setFriendText(key, name);
        mAddMeInfoTv.setText(StringUtils.formatHtmlTxFromResId(R.string.share_info_add_me, name));
        ImageLoadUtils.loadImg(getContext(), qrPath, mQrCodeIv);
    }


}
