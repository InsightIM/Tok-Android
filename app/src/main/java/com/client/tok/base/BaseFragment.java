package com.client.tok.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import com.client.tok.utils.LogUtil;

public abstract class BaseFragment extends Fragment {
    private String TAG = "baseFragment";

    @Override
    public void onSaveInstanceState(Bundle outState) {//don't save when fragment destroy

    }

    public <T extends View> T $(View view, int id) {
        return (T) view.findViewById(id);
    }

    /**
     * Create a static method of fragment to facilitate the transfer of parameters
     *
     * @param args parameters
     */
    public static <T extends Fragment> T newInstance(Class clazz, Bundle args) {
        T mFragment = null;
        try {
            mFragment = (T) clazz.newInstance();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        mFragment.setArguments(args);
        return mFragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "fragment onDestroy " + this.getClass().getName());
    }
}
