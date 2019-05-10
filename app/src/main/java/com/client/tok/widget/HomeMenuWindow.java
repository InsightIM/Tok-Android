package com.client.tok.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import com.client.tok.R;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.pagejump.PageJumpIn;
import com.client.tok.utils.ScreenUtils;

public class HomeMenuWindow {
    public static PopupWindow getHomeMenu(final Context context) {
        LayoutInflater inflater =
            (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.view_home_menu, null);

        final PopupWindow popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(
            new ColorDrawable(context.getColor(R.color.pop_window_bg)));
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        contentView.findViewById(R.id.id_menu_add_friend_tv).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                    PageJumpIn.jumpAddFriendsPage(context);
                }
            });

        contentView.findViewById(R.id.id_menu_scan_tv).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                    PageJumpIn.jumpScanPage(context);
                }
            });
        contentView.findViewById(R.id.id_menu_my_id_tv).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                    //myChatId
                    PageJumpIn.jumpMyTokIdPage(context);
                }
            });
        return popupWindow;
    }

    public static int[] calculatePopWindowPos(final View anchorView, final View contentView) {
        final int windowPos[] = new int[2];
        final int anchorLoc[] = new int[2];
        anchorView.getLocationOnScreen(anchorLoc);
        final int anchorHeight = anchorView.getHeight();
        final int screenHeight = ScreenUtils.getScreenHeight(anchorView.getContext());
        final int screenWidth = ScreenUtils.getScreenWidth(anchorView.getContext());
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        final int windowHeight = contentView.getMeasuredHeight();
        final int windowWidth = contentView.getMeasuredWidth();
        final boolean isNeedShowUp = (screenHeight - anchorLoc[1] - anchorHeight < windowHeight);
        if (isNeedShowUp) {
            windowPos[0] = screenWidth - windowWidth;
            windowPos[1] = anchorLoc[1] - windowHeight;
        } else {
            windowPos[0] = screenWidth - windowWidth;
            windowPos[1] = anchorLoc[1] + anchorHeight;
        }
        return windowPos;
    }
}
