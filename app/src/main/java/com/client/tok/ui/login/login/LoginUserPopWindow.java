package com.client.tok.ui.login.login;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import com.client.tok.R;
import com.client.tok.bean.UserInfo;
import com.client.tok.utils.ViewUtil;
import java.util.List;

public class LoginUserPopWindow {

    public static PopupWindow showLoginUsers(Context context, List<UserInfo> users,
        final AdapterView.OnItemClickListener listener) {
        final PopupWindow popWindow = new PopupWindow(context);
        View rootView = ViewUtil.inflateViewById(context, R.layout.layout_login_user_pop_window);

        ListView listView = rootView.findViewById(R.id.id_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null) {
                    listener.onItemClick(parent, view, position, id);
                }
                popWindow.dismiss();
            }
        });
        LoginUserAdapter adapter = new LoginUserAdapter(context, users);
        listView.setAdapter(adapter);

        popWindow.setContentView(rootView);
        popWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popWindow.setTouchable(true);
        popWindow.setBackgroundDrawable(new ColorDrawable(context.getColor(R.color.transparent)));
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(true);
        return popWindow;
    }
}
