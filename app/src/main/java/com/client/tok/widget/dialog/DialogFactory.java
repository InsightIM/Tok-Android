package com.client.tok.widget.dialog;

import android.app.Activity;
import android.support.v4.widget.TextViewCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.tox.State;
import com.client.tok.ui.addfriends.AddFriendsModel;
import com.client.tok.utils.FilePicker;
import com.client.tok.utils.StringUtils;
import com.client.tok.utils.ToastUtils;
import com.client.tok.utils.VerifyUtils;
import com.client.tok.utils.ViewUtil;

public class DialogFactory {
    private static final String TAG = "TokDialog";

    public static DialogView addFriendDialog(Activity activity, final String friendId,
        final String alias, boolean showFriendId, String msg, View.OnClickListener leftListener,
        final View.OnClickListener rightListener) {
        View view = ViewUtil.inflateViewById(activity, R.layout.dialog_layout_input);
        //
        TextView promptTv = view.findViewById(R.id.id_prompt_tv);
        TextView idTv = view.findViewById(R.id.id_tok_id_tv);
        if (showFriendId) {
            promptTv.setVisibility(View.VISIBLE);
            idTv.setVisibility(View.VISIBLE);
            idTv.setText(friendId);
        } else {
            promptTv.setVisibility(View.GONE);
            idTv.setVisibility(View.GONE);
        }
        final EditText inputEt = view.findViewById(R.id.id_input_et);
        inputEt.setHint(R.string.add_friend_msg_hint);
        if (StringUtils.isEmpty(msg)) {
            msg = StringUtils.formatTxFromResId(R.string.add_friend_default_message,
                new String(State.userRepo().getActiveUserDetails().getNickname().value));
        }
        inputEt.setText(msg);
        inputEt.setSelection(msg.length());
        DialogView dialogView = new DialogView(activity, view, false);
        dialogView.setLeftOnClickListener(leftListener);
        dialogView.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddFriendsModel model = new AddFriendsModel();
                boolean success =
                    model.addFriendById(friendId, alias, inputEt.getText().toString());
                if (success) {
                    if (rightListener != null) {
                        rightListener.onClick(v);
                    }
                } else {
                    ToastUtils.show(R.string.tok_id_invalid);
                }
            }
        });
        dialogView.setTitle_(R.string.confirm_add_friends);
        if (showFriendId) {
            dialogView.setTitleCenter();
        }
        dialogView.setCanCancel(false);
        dialogView.show();
        return dialogView;
    }

    public static DialogView importAccountDialog(Activity activity, final String userName,
        final boolean encrypt, final InputCallBack inputCallBack) {
        View view = ViewUtil.inflateViewById(activity, R.layout.dialog_import_account);
        //
        final EditText nameEt = view.findViewById(R.id.id_user_name_et);
        nameEt.setText(userName);
        nameEt.setSelection(nameEt.length());

        LinearLayout pwdLayout = view.findViewById(R.id.id_pwd_layout);
        final EditText pwdEt = view.findViewById(R.id.id_pwd_et);

        LinearLayout newPwdLayout = view.findViewById(R.id.id_new_pwd_layout);
        final EditText newPwdEt = view.findViewById(R.id.id_new_pwd_et);
        final EditText repeatPwdEt = view.findViewById(R.id.id_new_pwd_again_et);

        EditText actionEt;
        if (encrypt) {
            pwdLayout.setVisibility(View.VISIBLE);
            newPwdLayout.setVisibility(View.GONE);
            actionEt = pwdEt;
        } else {
            pwdLayout.setVisibility(View.GONE);
            newPwdLayout.setVisibility(View.VISIBLE);
            actionEt = repeatPwdEt;
        }

        actionEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                importConfirm(userName, encrypt, inputCallBack, nameEt, pwdEt, newPwdEt,
                    repeatPwdEt);
                return false;
            }
        });

        DialogView dialogView = new DialogView(activity, view, false);
        dialogView.setAutoDismiss(false);
        dialogView.setLeftButtonTxt(R.string.cancel);
        dialogView.setRightButtonTxt(R.string.confirm);

        dialogView.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importConfirm(userName, encrypt, inputCallBack, nameEt, pwdEt, newPwdEt,
                    repeatPwdEt);
            }
        });
        dialogView.setTitle_(R.string.import_account2);
        dialogView.setCanCancel(false);
        dialogView.show();
        return dialogView;
    }

    private static void importConfirm(String userName, boolean encrypt, InputCallBack inputCallBack,
        EditText nameEt, EditText pwdEt, EditText newPwdEt, EditText repeatPwdEt) {
        if (inputCallBack != null) {
            String name = nameEt.getText().toString().trim();
            String pwd = pwdEt.getText().toString().trim();
            String newPwd = newPwdEt.getText().toString().trim();
            String repeatPwd = repeatPwdEt.getText().toString().trim();
            if (StringUtils.isEmpty(name)) {
                ToastUtils.show(R.string.input_user_name_prompt);
                return;
            }
            if (!VerifyUtils.isUserNameValid(userName)) {
                ToastUtils.show(R.string.name_invalid);
                return;
            }
            if (encrypt) {
                if (StringUtils.isEmpty(pwd)) {
                    ToastUtils.show(R.string.input_user_pwd_prompt);
                    return;
                }
                inputCallBack.input(name, pwd);
            } else {
                if (StringUtils.isEmpty(newPwd)) {
                    ToastUtils.show(R.string.new_pwd_empty);
                    return;
                }
                if (!newPwd.equals(repeatPwd)) {
                    ToastUtils.show(R.string.pwd_not_match);
                    return;
                }
                inputCallBack.input(name, repeatPwd);
            }
        }
    }

    /**
     * rename dialog
     */
    public static DialogView inputDialog(Activity activity, String title, final String originData,
        final InputCallBack inputCallBack) {
        View view = ViewUtil.inflateViewById(activity, R.layout.dialog_layout_input);
        final EditText inputEt = view.findViewById(R.id.id_input_et);
        if (!StringUtils.isEmpty(originData)) {
            inputEt.setText(originData);
            inputEt.setSelection(originData.length());
        }

        DialogView dialogView = new DialogView(activity, view, false);
        dialogView.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = inputEt.getText().toString();
                if (StringUtils.isEmpty(data)) {
                    ToastUtils.show(R.string.input_msg);
                    return;
                }
                if (data.equals(originData)) {
                    ToastUtils.show(R.string.input_msg_same_to_origin);
                    return;
                }
                if (inputCallBack != null) {
                    inputCallBack.input(data);
                }
            }
        });
        dialogView.setTitle_(title);
        dialogView.show();
        return dialogView;
    }

    public static DialogView showTwoBtErrorDialog(Activity activity, CharSequence content,
        View.OnClickListener leftListener, View.OnClickListener rightListener) {
        DialogView dialogView = new DialogView(activity);
        dialogView.setLeftOnClickListener(leftListener);
        dialogView.setRightOnClickListener(rightListener);
        dialogView.setTitle_(R.string.oops);
        dialogView.setContent(content);
        dialogView.show();
        return dialogView;
    }

    public static DialogView showDelDialog(Activity activity, CharSequence title,
        View.OnClickListener rightListener) {
        DialogView dialogView = new DialogView(activity, false);
        dialogView.setTitle_(title)
            .setRightOnClickListener(rightListener)
            .setLeftButtonTxt(R.string.cancel)
            .setRightButtonTxt(R.string.ok);
        dialogView.show();
        return dialogView;
    }

    public static DialogView showSelImgMethodDialog(final Activity activity, String title,
        View.OnClickListener delListener) {
        return showMenuDialog(activity, STYLE_NORMAL_2_CHOOSE_WARNING, title,
            StringUtils.getTextFromResId(R.string.album),
            StringUtils.getTextFromResId(R.string.camera),
            StringUtils.getTextFromResId(R.string.delete), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FilePicker.openGallery(activity, true);
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FilePicker.openCamera(activity);
                }
            }, delListener);
    }

    public static DialogView show2BtnDialog(Activity activity, String title, String content,
        String rightTxt, View.OnClickListener rightListener) {
        DialogView dialogView = new DialogView(activity, false);
        dialogView.setTitle_(title)
            .setContent(content)
            .setRightOnClickListener(rightListener)
            .setLeftButtonTxt(R.string.cancel)
            .setRightButtonTxt(rightTxt);
        dialogView.show();
        return dialogView;
    }

    public static DialogView showGroupMenuDialog(Activity activity, final String title,
        final String menu1, String menu2, final View.OnClickListener menu1Listener,
        final View.OnClickListener menu2Listener) {
        return showNormal2ChooseDialog(activity, title, menu1, menu2, menu1Listener, menu2Listener);
    }

    public static DialogView showNormal2ChooseDialog(Activity activity, final String title,
        final String menu1, String menu2, final View.OnClickListener menu1Listener,
        final View.OnClickListener menu2Listener) {
        return showMenuDialog(activity, STYLE_NORMAL_2_CHOOSE, title, menu1, menu2,
            StringUtils.getTextFromResId(R.string.cancel), menu1Listener, menu2Listener, null);
    }

    public static DialogView showNormal1ChooseDialog(Activity activity, final String title,
        final String menu1, final View.OnClickListener menu1Listener) {
        return showMenuDialog(activity, STYLE_NORMAL_1_CHOOSE, title, menu1, null,
            StringUtils.getTextFromResId(R.string.cancel), menu1Listener, null, null);
    }

    public static DialogView showPromptDialog(Activity activity, final String title,
        final String menu1, String cancel, final View.OnClickListener menu1Listener) {
        return showMenuDialog(activity, STYLE_PROMPT, title, menu1, null, cancel, menu1Listener,
            null, null);
    }

    public static DialogView showWarningDialog(Activity activity, final String title,
        final String menu1, final View.OnClickListener menu1Listener) {
        return showMenuDialog(activity, STYLE_WARNING, title, menu1, null,
            StringUtils.getTextFromResId(R.string.cancel), menu1Listener, null, null);
    }

    private static final int STYLE_NORMAL_1_CHOOSE = 1;
    private static final int STYLE_NORMAL_2_CHOOSE = 2;
    private static final int STYLE_NORMAL_2_CHOOSE_WARNING = 3;
    private static final int STYLE_PROMPT = 4;
    private static final int STYLE_WARNING = 5;

    private static DialogView showMenuDialog(Activity activity, int style, final String title,
        final String menu1, String menu2, String cancel, final View.OnClickListener menu1Listener,
        final View.OnClickListener menu2Listener, final View.OnClickListener cancelListener) {
        View view = ViewUtil.inflateViewById(activity, R.layout.dialog_layout_menu);
        final TextView titleTv = view.findViewById(R.id.id_menu_title);
        final TextView menu1Tv = view.findViewById(R.id.id_menu1_tv);
        final TextView menu2Tv = view.findViewById(R.id.id_menu2_tv);
        final TextView cancelTv = view.findViewById(R.id.id_menu_cancel_tv);

        int titleStyle = -1;
        int menu1Style = -1;
        int menu2Style = -1;
        int cancelStyle = -1;
        switch (style) {
            case STYLE_NORMAL_1_CHOOSE:
                titleStyle = R.style.DialogTitleNormal;
                menu1Style = R.style.DialogMenuDo;
                menu2Style = R.style.DialogMenuNormal;
                cancelStyle = R.style.DialogMenuNormal;
                break;
            case STYLE_NORMAL_2_CHOOSE:
                titleStyle = R.style.DialogTitleNormal;
                menu1Style = R.style.DialogMenuNormal;
                menu2Style = R.style.DialogMenuNormal;
                cancelStyle = R.style.DialogMenuNormal;
                break;
            case STYLE_NORMAL_2_CHOOSE_WARNING:
                titleStyle = R.style.DialogTitleNormal;
                menu1Style = R.style.DialogMenuNormal;
                menu2Style = R.style.DialogMenuNormal;
                cancelStyle = R.style.DialogMenuWarning;
                break;
            case STYLE_PROMPT:
                titleStyle = R.style.DialogTitlePrompt;
                menu1Style = R.style.DialogMenuDo;
                menu2Style = R.style.DialogMenuDo;
                cancelStyle = R.style.DialogMenuNormal;
                break;
            case STYLE_WARNING:
                titleStyle = R.style.DialogTitlePrompt;
                menu1Style = R.style.DialogMenuWarning;
                menu2Style = R.style.DialogMenuWarning;
                cancelStyle = R.style.DialogMenuNormal;
                break;
        }

        TextViewCompat.setTextAppearance(titleTv, titleStyle);
        TextViewCompat.setTextAppearance(menu1Tv, menu1Style);
        TextViewCompat.setTextAppearance(menu2Tv, menu2Style);
        TextViewCompat.setTextAppearance(cancelTv, cancelStyle);

        if (!StringUtils.isEmpty(title)) {
            titleTv.setText(title);
            titleTv.setVisibility(View.VISIBLE);
        } else {
            titleTv.setVisibility(View.GONE);
        }

        if (!StringUtils.isEmpty(menu1)) {
            menu1Tv.setText(menu1);
            menu1Tv.setVisibility(View.VISIBLE);
        } else {
            menu1Tv.setVisibility(View.GONE);
        }

        if (!StringUtils.isEmpty(menu2)) {
            menu2Tv.setText(menu2);
            menu2Tv.setVisibility(View.VISIBLE);
        } else {
            menu2Tv.setVisibility(View.GONE);
        }

        if (!StringUtils.isEmpty(cancel)) {
            cancelTv.setText(cancel);
            cancelTv.setVisibility(View.VISIBLE);
        } else {
            cancelTv.setVisibility(View.GONE);
        }

        final DialogView dialogView = new DialogView(activity, view, true);

        //listener
        menu1Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogView.dismiss();
                if (menu1Listener != null) {
                    menu1Listener.onClick(v);
                }
            }
        });

        menu2Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogView.dismiss();
                if (menu2Listener != null) {
                    menu2Listener.onClick(v);
                }
            }
        });

        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogView.dismiss();
                if (cancelListener != null) {
                    cancelListener.onClick(v);
                }
            }
        });
        dialogView.setBtnLayout(false);
        dialogView.show();

        return dialogView;
    }

    public interface InputCallBack {
        void input(String... input);
    }
}
