package com.client.tok.ui.info.offlinebot;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.base.BaseCommonTitleActivity;
import com.client.tok.utils.StringUtils;
import com.client.tok.utils.SystemUtils;
import com.client.tok.utils.ToastUtils;

public class OfflineBotDetailActivity extends BaseCommonTitleActivity {
    private TextView mGitHubTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_bot_detail);
        mGitHubTv = $(R.id.id_detail_git_hub_tv);
        initLink();
    }

    private void initLink() {
        final String github = StringUtils.getTextFromResId(R.string.git_hub);
        String copyLink = StringUtils.getTextFromResId(R.string.copy_link);
        String content = StringUtils.getTextFromResId(R.string.more_about_offline_bot_6)
            + github
            + "  "
            + copyLink;

        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append(content);

        int gitHubStart = content.indexOf(github);
        int gitHubEnd = gitHubStart + github.length();
        sb.setSpan(new UnderlineSpan(), gitHubStart, gitHubEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        int copyStart = content.indexOf(copyLink);
        int copyEnd = copyStart + copyLink.length();
        ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
        sb.setSpan(redSpan, copyStart, copyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        final ClickableSpan clickSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                SystemUtils.copyTxt2Clipboard(OfflineBotDetailActivity.this, github);
                ToastUtils.show(R.string.copy_success);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.auto_link_color));
                ds.setUnderlineText(false);
            }
        };
        sb.setSpan(clickSpan, copyStart, copyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mGitHubTv.setMovementMethod(LinkMovementMethod.getInstance());
        mGitHubTv.setText(sb);
    }

    @Override
    public int getTitleId() {
        return R.string.off_line_bot;
    }
}
