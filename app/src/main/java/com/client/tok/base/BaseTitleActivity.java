package com.client.tok.base;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.pagejump.PageJumpIn;
import com.client.tok.permission.BasePermissionActivity;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.ScreenUtils;
import com.client.tok.utils.StatusBarUtil;
import com.client.tok.utils.StringUtils;
import com.client.tok.utils.SystemUtils;

/**
 * Base Title ,and it's subClass are {@link BaseCollapsingTitleActivity,BaseCommonTitleActivity,BaseTitleFullScreenActivity}
 */
public abstract class BaseTitleActivity extends BasePermissionActivity {
    private String TAG = "BaseTitleActivity";
    private Toolbar mToolbar;
    private ImageView mBackIv;
    private TextView mMainTitle;
    private View mSubTitleLayout;
    private TextView mSubTitle;
    protected View mMenuLayout;
    private ImageView mSubTitleLogo;
    private ImageView mMenuIv;
    private TextView mMenuTv;
    private ActionBar mActionbar;
    //private MenuItem mMenuItem;
    private int mMenuTxtId;
    private int mMenuImgId;
    private ViewGroup mContainerView;
    private ViewGroup mBottomContainer;
    private boolean isAllowTouchHideKeyboard = true;

    //toolbar style
    protected int TOOL_BAR_STYLE_WHITE = 1;
    //toolBar bg:white(app bg white)，the content such as "back icon" "title" are black
    protected int TOOL_BAR_STYLE_BLACK = 2;
    //toolBar bg:black，the content such as "back icon" "title" are white
    protected int TOOL_BAR_STYLE_WHITE_BIG = 3;
    //toolBar bg:white，he content such as "back icon" "title" are white，but font is bigger
    protected int TOOL_BAR_TRANSLATE = 4;//toolBar bg transparent

    //statusBar style
    protected int STATUS_BAR_NORMAL_WHITE = 10;
    protected int STATUS_BAR_NORMAL_DARK = 11;
    protected int STATUS_BAR_FULL_SCREEN_WHITE = 12;
    protected int STATUS_BAR_FULL_SCREEN_TRANSLATE = 13;

    //gravity of the title
    protected int GRAVITY_CENTER = 1;
    protected int GRAVITY_LEFT = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTransition();
        setStatusBarStyle();//根据基类设置的样式来设置statusBar
        super.onCreate(savedInstanceState);
        super.setContentView(getContentViewId());
        initViews();
        initToolbar();
        initMenu();
    }

    public <T extends View> T $(int id) {
        return (T) findViewById(id);
    }

    public abstract int getContentViewId();

    public void setTransition() {
        if (isHasTransition() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            // transition animation
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            Transition explode =
                TransitionInflater.from(this).inflateTransition(android.R.transition.explode);
            getWindow().setEnterTransition(explode);
        }
    }

    /**
     * is support transition animation
     *
     * @return true/false
     */
    public boolean isHasTransition() {
        return false;
    }

    @Override
    public void setContentView(View view) {
        if (mContainerView == null) {
            super.setContentView(view);
        } else {
            mContainerView.removeAllViews();
            mContainerView.addView(view);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        if (mContainerView == null) {
            super.setContentView(layoutResID);
        } else {
            mContainerView.removeAllViews();
            final LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {
                inflater.inflate(layoutResID, mContainerView, true);
            }
        }
    }

    private void initViews() {
        mToolbar = this.findViewById(R.id.id_toolbar);
        mBackIv = this.findViewById(R.id.id_back_iv);
        mMainTitle = findViewById(R.id.id_main_title_tv);
        setTitleGravity();
        mSubTitleLayout = findViewById(R.id.id_sub_title_layout);
        mSubTitle = findViewById(R.id.id_sub_title_tv);
        mSubTitleLogo = findViewById(R.id.id_sub_title_logo_iv);
        mMenuLayout = findViewById(R.id.id_menu_layout);
        mMenuIv = findViewById(R.id.id_menu_iv);
        mMenuTv = findViewById(R.id.id_menu_tv);
        mContainerView = this.findViewById(R.id.id_container);
        mBottomContainer = findViewById(R.id.id_bottom_container);
    }

    public int getTitleGravity() {
        return GRAVITY_CENTER;
    }

    private void setTitleGravity() {
        if (GRAVITY_CENTER == getTitleGravity()) {
            //default
        } else if (GRAVITY_LEFT == getTitleGravity()) {
            //left
            LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.START;
            lp.setMarginStart(ScreenUtils.dimen2px(this, R.dimen.s_16));
            mMainTitle.setLayoutParams(lp);
        }
    }

    private void initToolbar() {
        if (isShowToolBar()) {
            setSupportActionBar(mToolbar);
            mBackIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BaseTitleActivity.this.onBackClick();
                }
            });
            mActionbar = getSupportActionBar();
            //set toolbar style
            setToolBarStyle();
            //set title
            setPageTitle(getTitleId());
        } else {
            hideToolbar();
        }
    }

    private void initMenu() {
        setMenuImgId(getMenuImgId());
        setMenuTxtId(getMenuTxtId());
        mMenuLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseTitleActivity.this.onMenuClick();
            }
        });
    }

    /**
     * is show toolBar
     */
    public boolean isShowToolBar() {
        return true;
    }

    /**
     * is show back icon
     */
    public boolean isShowBackIcon() {
        return true;
    }

    /**
     * is support task back(not destroy activity)
     *
     * @return true false
     */
    public boolean isSupportTaskBack() {
        return false;
    }

    /**
     * get back icon, sub class Override this method,can set back icon
     */
    public int getBackIcon() {
        return 0;
    }

    /**
     * is show title
     */
    public boolean isShowTitle() {
        return true;
    }

    private void hideToolbar() {
        mToolbar.setVisibility(View.GONE);
    }

    protected void setPageTitle(int titleResId) {
        if (isShowTitle() && mMainTitle != null && titleResId > 0) {
            setPageTitle(getString(titleResId));
        } else {
            setPageTitle("");
        }
    }

    protected void setPageTitle(CharSequence title) {
        mMainTitle.setText(title);
    }

    protected void setSubTitle(int subTitleResId) {
        if (subTitleResId > 0) {
            setSubTitle(StringUtils.getTextFromResId(subTitleResId));
        }
    }

    protected void setSubTitle(CharSequence subTitle) {
        if (!TextUtils.isEmpty(subTitle)) {
            mSubTitleLayout.setVisibility(View.VISIBLE);
            mSubTitle.setText(subTitle);
        } else {
            mSubTitleLayout.setVisibility(View.GONE);
        }
    }

    protected void setUserStatus(boolean isOnline) {
        mSubTitleLogo.setVisibility(View.VISIBLE);
        mSubTitleLogo.setSelected(isOnline);
    }

    /**
     * Sub class Override this method , can set text menu
     *
     * @return resId
     */
    public int getMenuTxtId() {
        return 0;
    }

    /**
     * Sub class Override this method , can set image menu
     *
     * @return resId
     */
    public int getMenuImgId() {
        return 0;
    }

    /**
     * when you want chang menu content(text), call this method
     *
     * @param menuTxtId string id; menuTxtId <0, hide menu
     */
    public void setMenuTxtId(int menuTxtId) {
        if (mMenuTv != null) {
            mMenuTxtId = menuTxtId;
            if (mMenuTxtId <= 0 && mMenuImgId <= 0) {
                mMenuTv.setVisibility(View.GONE);
            } else {
                if (mMenuTxtId > 0) {
                    mMenuTv.setText(mMenuTxtId);
                    mMenuTv.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void setMenuEnable(boolean enable) {
        if (mMenuTv != null) {
            mMenuTv.setEnabled(enable);
        }
        if (mMenuIv != null) {
            mMenuIv.setEnabled(enable);
        }
    }

    /**
     * when you want chang menu content(image), call this method
     *
     * @param menuImgId img id,; menuTxtId <0, hide menu
     */
    public void setMenuImgId(int menuImgId) {
        if (mMenuIv != null) {
            mMenuImgId = menuImgId;
            if (mMenuTxtId <= 0 && mMenuImgId <= 0) {
                mMenuIv.setVisibility(View.GONE);
            } else {
                if (mMenuImgId > 0) {
                    mMenuIv.setImageResource(menuImgId);
                    mMenuIv.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public int getStatusBarStyle() {
        return STATUS_BAR_NORMAL_WHITE;
    }

    private void setStatusBarStyle() {
        int style = getStatusBarStyle();
        if (STATUS_BAR_NORMAL_WHITE == style) {//white statusBar,icon and text are black
            StatusBarUtil.setTranslucentDark(this);
        } else if (STATUS_BAR_FULL_SCREEN_WHITE == style) {
            StatusBarUtil.setFullScreenTranslucentDark(this);
        } else if (STATUS_BAR_FULL_SCREEN_TRANSLATE == style) {
            StatusBarUtil.setTranslucent(this);
        } else if (STATUS_BAR_NORMAL_DARK == style) {
            //
        }
    }

    public int getToolBarStyle() {
        return TOOL_BAR_STYLE_WHITE;
    }

    private void setToolBarStyle() {
        int style = getToolBarStyle();
        if (mToolbar != null) {
            int backIconId = -1;
            int mainTitleStyle = -1;
            int subTitleStyle = -1;
            int toolbarBg = -1;
            int menuStyle = -1;
            if (TOOL_BAR_STYLE_WHITE == style) {
                //as default
                backIconId = R.drawable.arrow_back_black;
                mainTitleStyle = R.style.MainTitle;
                subTitleStyle = R.style.SubTitle;
                toolbarBg = R.color.toolbar_bg;
            } else if (TOOL_BAR_STYLE_WHITE_BIG == style) {
                backIconId = R.drawable.arrow_back_black;
                mainTitleStyle = R.style.MainTitleMax;
                toolbarBg = R.color.toolbar_bg;
            } else if (TOOL_BAR_STYLE_BLACK == style) {
                backIconId = R.drawable.arrow_back_white;
            } else if (TOOL_BAR_TRANSLATE == style) {
                backIconId = R.drawable.arrow_back_white;
                toolbarBg = R.color.transparent;
            }
            if (getBackIcon() > 0) {
                backIconId = getBackIcon();
            }
            if (isShowBackIcon()) {
                mBackIv.setImageResource(backIconId);
                mBackIv.setVisibility(View.VISIBLE);
            } else {
                mBackIv.setVisibility(View.GONE);
            }

            if (toolbarBg > 0) {
                mToolbar.setBackgroundResource(toolbarBg);
                try {
                    //this method is useful above android 21,but crash at SamSung Galaxy TabA6(from google play console)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().setStatusBarColor(getColor(toolbarBg));
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            if (mainTitleStyle > 0) {
                TextViewCompat.setTextAppearance(mMainTitle, mainTitleStyle);
            }
            if (subTitleStyle > 0) {
                //setTextAppearance is deprecation,and crash on some device of android 5.0
                //mSubTitle.setTextAppearance(subTitleStyle);
                TextViewCompat.setTextAppearance(mSubTitle, subTitleStyle);
            }
            //TODO how to set menuItem and collsapingLayout color
        }
    }

    /**
     * is show meunu visible
     *
     * @param isVisible true,false
     */
    public void setMenuVisible(boolean isVisible) {
        if (mMenuLayout != null) {
            mMenuLayout.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
        }
    }

    /**
     * menu click listener
     */
    public void onMenuClick() {
    }

    public boolean isAllowTouchHideSoft() {
        return isAllowTouchHideKeyboard;
    }

    public void setTouchHideSoft(boolean allow) {
        isAllowTouchHideKeyboard = allow;
    }

    public void hideOther() {

    }

    /**
     * @param textView
     */
    public void addKeyboardAction(TextView textView) {
        if (textView != null) {
            textView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    SystemUtils.hideSoftKeyBoard(BaseTitleActivity.this);
                    BaseTitleActivity.this.onKeyboardAction(v, actionId);
                    return false;
                }
            });
        }
    }

    public void onKeyboardAction(TextView textView, int actionId) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean isBottomLayout = isBottomLayoutShowing();
        LogUtil.i(TAG, "baseTitleActivity isBottomLayout:" + isBottomLayout);
        if (isBottomLayout) {
            hideBottomLayout();
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    public boolean isBottomLayoutShowing() {
        return SystemUtils.isSoftShowing(this);
    }

    public void hideBottomLayout() {
        SystemUtils.hideSoftKeyBoard(this);
    }

    public void onBackClick() {
        onFinish();
    }

    /**
     * @return resId
     */
    public int getTitleId() {
        return 0;
    }

    public void setBottomView(View view) {
        if (mBottomContainer != null && view != null) {
            mBottomContainer.removeAllViews();
            mBottomContainer.setVisibility(View.VISIBLE);
            mBottomContainer.addView(view);
        }
    }

    /**
     * default method to finish activity,
     * it can be changed by override
     */
    public void onFinish() {
        if (isSupportTaskBack()) {
            moveTaskToBack(true);
        } else {
            String parentActivityName = NavUtils.getParentActivityName(this);
            if (parentActivityName != null && !SystemUtils.isActivityRunning(this,
                parentActivityName)) {
                PageJumpIn.jumpActivity(this, parentActivityName);
                finish();
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                finishAfterTransition();
            } else {
                finish();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SystemUtils.hideSoftKeyBoard(this);
    }

    @Override
    public void onBackPressed() {
        onFinish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SystemUtils.hideSoftKeyBoard(this);
    }
}
