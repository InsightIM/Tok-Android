package com.client.tok.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.utils.ScreenUtils;
import java.util.Arrays;
import java.util.List;

public class MenuPopWindow {
    //recent message long click menu: delete+mark read
    public static final String TYPE_RECENT = "1";
    private static List<Integer> TYPE_RECENT_MENU =
        Arrays.asList(R.id.id_menu_del_tv, R.id.id_menu_mark_read_tv);

    //contact long click menu: del
    public static final String TYPE_CONTACT = "2";
    private static List<Integer> TYPE_CONTACT_MENU = Arrays.asList(R.id.id_menu_del_tv);

    //chat: text message long click menu: copy+delete
    public static final String TYPE_MSG_TXT = "3";
    private static List<Integer> TYPE_MSG_TXT_MENU =
        Arrays.asList(R.id.id_menu_copy_tv, R.id.id_menu_del_tv);

    //chat:file long click menu: copy+delete
    public static final String TYPE_MSG_FILE = "4";
    private static List<Integer> TYPE_MSG_FILE_MENU =
        Arrays.asList(R.id.id_menu_del_tv, R.id.id_menu_save_tv);

    public static View mContentView;

    public static PopupWindow getMenuView(Context context, String type,
        final MenuClickListener listener) {
        LayoutInflater inflater =
            (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContentView = inflater.inflate(R.layout.view_click_menu, null);

        final PopupWindow popupWindow =
            new PopupWindow(mContentView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(
            new ColorDrawable(ContextCompat.getColor(context, R.color.pop_window_bg)));
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);

        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        List<Integer> typeMenu = null;
        switch (type) {
            case TYPE_RECENT:
                typeMenu = TYPE_RECENT_MENU;
                break;
            case TYPE_CONTACT:
                typeMenu = TYPE_CONTACT_MENU;
                break;
            case TYPE_MSG_TXT:
                typeMenu = TYPE_MSG_TXT_MENU;
                break;
            case TYPE_MSG_FILE:
                typeMenu = TYPE_MSG_FILE_MENU;
                break;
        }

        if (typeMenu != null) {
            mContentView.findViewById(R.id.id_menu_copy_layout)
                .setVisibility(typeMenu.contains(R.id.id_menu_copy_tv) ? View.VISIBLE : View.GONE);
            mContentView.findViewById(R.id.id_menu_del_layout)
                .setVisibility(typeMenu.contains(R.id.id_menu_del_tv) ? View.VISIBLE : View.GONE);
            mContentView.findViewById(R.id.id_menu_mark_layout)
                .setVisibility(
                    typeMenu.contains(R.id.id_menu_mark_read_tv) ? View.VISIBLE : View.GONE);
            mContentView.findViewById(R.id.id_menu_save_layout)
                .setVisibility(typeMenu.contains(R.id.id_menu_save_tv) ? View.VISIBLE : View.GONE);
        }

        TextView copyTv = mContentView.findViewById(R.id.id_menu_copy_tv);
        copyTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCopy();
                }
                popupWindow.dismiss();
            }
        });

        TextView delTv = mContentView.findViewById(R.id.id_menu_del_tv);
        delTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDel();
                }
                popupWindow.dismiss();
            }
        });

        TextView markRead = mContentView.findViewById(R.id.id_menu_mark_read_tv);
        markRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onMarkRead();
                }
                popupWindow.dismiss();
            }
        });

        TextView save = mContentView.findViewById(R.id.id_menu_save_tv);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onSave();
                }
                popupWindow.dismiss();
            }
        });
        return popupWindow;
    }

    public static class MenuClickListener {

        public void onCopy() {

        }

        public void onDel() {

        }

        public void onMarkRead() {

        }

        public void onSave() {

        }

        public void onFailed() {

        }
    }

    public static int[] calculatePopWindowPos(final View anchorView, final View contentView) {
        final int windowPos[] = new int[2];
        final int anchorLoc[] = new int[2];
        int yOff = 30;
        anchorView.getLocationOnScreen(anchorLoc);
        final int anchorHeight = anchorView.getHeight();
        final int anchorWidth = anchorView.getWidth();
        final int screenHeight = ScreenUtils.getScreenHeight(anchorView.getContext());
        final int screenWidth = ScreenUtils.getScreenWidth(anchorView.getContext());
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        final int windowHeight = contentView.getMeasuredHeight();
        final int windowWidth = contentView.getMeasuredWidth();
        final boolean isNeedShowUp = (screenHeight - anchorLoc[1] - anchorHeight < windowHeight);

        int popWindowX = anchorWidth - windowWidth;
        windowPos[0] = popWindowX > 0 ? anchorLoc[0] + anchorWidth - windowWidth
            : anchorLoc[0] + anchorWidth / 2;
        if (isNeedShowUp) {
            windowPos[1] = anchorLoc[1] - windowHeight + yOff;
        } else {
            windowPos[1] = anchorLoc[1] + anchorHeight - yOff;
        }

        return windowPos;
    }
}
