package com.client.tok.ui.about;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.client.tok.BuildConfig;
import com.client.tok.R;
import com.client.tok.base.BaseCommonTitleActivity;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.pagejump.PageJumpIn;
import com.client.tok.utils.StringUtils;
import com.client.tok.widget.ItemInfoView;

public class AboutUsActivity extends BaseCommonTitleActivity {
    private TextView mVersionTv;
    private ItemInfoView mContactUsIiv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        initView();
    }

    public void initView() {
        mContactUsIiv = $(R.id.id_about_contact_iiv);
        mContactUsIiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PageJumpIn.jumpAddFriendsPage(AboutUsActivity.this,
                    GlobalParams.CUSTOMER_SERVICE_TOK_ID);
            }
        });
        mVersionTv = $(R.id.id_about_us_version_tv);
        mVersionTv.setText(
            StringUtils.formatTxFromResId(R.string.version, BuildConfig.VERSION_NAME));
    }

    @Override
    public int getTitleId() {
        return R.string.about_us;
    }
}