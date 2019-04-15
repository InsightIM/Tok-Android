package com.client.tok.base;

import com.client.tok.R;

/**
 * BaseTitleActivity can Collapse
 */

public class BaseCollapsingTitleActivity extends BaseTitleActivity {

    @Override
    public int getContentViewId() {
        return R.layout.activity_base_collapsing_title;
    }

    @Override
    public boolean isShowToolBar() {
        return true;
    }

    @Override
    public int getToolBarStyle() {
        return TOOL_BAR_STYLE_WHITE;
    }
}
