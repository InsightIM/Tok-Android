package com.client.tok.base;

import com.client.tok.R;

/**
 * Base Common Title Activity
 */
public class BaseCommonTitleActivity extends BaseTitleActivity {

    @Override
    public int getContentViewId() {
        return R.layout.activity_base_common_title;
    }

    @Override
    public boolean isShowToolBar() {
        return true;
    }

    @Override
    public int getToolBarStyle() {
        return TOOL_BAR_STYLE_WHITE;
    }

    @Override
    public int getStatusBarStyle() {
        return STATUS_BAR_NORMAL_WHITE;
    }
}
