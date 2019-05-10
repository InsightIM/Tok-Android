package com.client.tok.base;

import com.client.tok.R;

/**
 * base FullScreenActivity
 */
public class BaseTitleFullScreenActivity extends BaseTitleActivity {
    @Override
    public int getContentViewId() {
        return R.layout.activity_base_fullscreen_title;
    }

    @Override
    public boolean isShowToolBar() {
        return false;
    }

    @Override
    public int getToolBarStyle() {
        return TOOL_BAR_STYLE_WHITE;
    }

    @Override
    public int getStatusBarStyle() {
        return STATUS_BAR_FULL_SCREEN_TRANSLATE;
    }
}
