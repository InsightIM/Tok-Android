package com.client.tok.ui.splash;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.base.BaseTitleFullScreenActivity;
import com.client.tok.utils.ViewUtil;
import com.lwj.widget.viewpagerindicator.ViewPagerIndicator;
import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends BaseTitleFullScreenActivity
    implements SplashContract.ISplashView {
    private ViewPagerIndicator mIndicator;
    private ViewPager mViewPager;
    private SplashContract.ISplashPresenter mSplashPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
        new SplashPresenter(this);
        mSplashPresenter.start();
    }

    private void initView() {
        mViewPager = $(R.id.id_splash_vp);
        mIndicator = $(R.id.id_splash_indicator);
    }

    @Override
    public void showGuideView() {
        mViewPager.setVisibility(View.VISIBLE);
        List<View> viewList = new ArrayList<>();
        viewList.add(ViewUtil.inflateViewById(this, R.layout.layout_guide1));
        viewList.add(ViewUtil.inflateViewById(this, R.layout.layout_guide2));
        View lastView = ViewUtil.inflateViewById(this, R.layout.layout_guide3);
        TextView enter = lastView.findViewById(R.id.id_guide_enter_btn);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSplashPresenter.enter();
            }
        });
        viewList.add(lastView);
        PagerAdapter vpAdapter = new GuideAdapter(viewList);
        mViewPager.setAdapter(vpAdapter);
        mIndicator.setViewPager(mViewPager, viewList.size());
    }

    @Override
    public void setPresenter(SplashContract.ISplashPresenter iLoginSignUpPresenter) {
        mSplashPresenter = iLoginSignUpPresenter;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public int getStatusBarStyle() {
        return STATUS_BAR_FULL_SCREEN_TRANSLATE;
    }

    @Override
    public void viewDestroy() {
        this.onFinish();
    }

    private class GuideAdapter extends PagerAdapter {
        private List<View> viewList;

        public GuideAdapter(List<View> viewList) {
            this.viewList = viewList;
        }

        @Override
        public int getCount() {
            return viewList == null ? 0 : viewList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position));
            return viewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewList.get(position));
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
