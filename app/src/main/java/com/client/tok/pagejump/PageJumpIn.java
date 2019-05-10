package com.client.tok.pagejump;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.client.tok.R;
import com.client.tok.TokApplication;
import com.client.tok.ui.about.AboutUsActivity;
import com.client.tok.ui.addfriends.AddFriendsActivity;
import com.client.tok.ui.chat2.Chat2Activity;
import com.client.tok.ui.chatid.TokIdActivity;
import com.client.tok.ui.clipimg.ClipImgActivity;
import com.client.tok.ui.contactreq.ContactReqActivity;
import com.client.tok.ui.contactreqdetail.ContactReqDetailActivity;
import com.client.tok.ui.home.HomeActivity;
import com.client.tok.ui.imgshow.ImgShowActivity;
import com.client.tok.ui.info.friend.FriendInfoActivity;
import com.client.tok.ui.info.mine.MyInfoActivity;
import com.client.tok.ui.info.offlinebot.OfflineBotActivity;
import com.client.tok.ui.info.offlinebot.OfflineBotDetailActivity;
import com.client.tok.ui.login.home.LoginSignUpHomeActivity;
import com.client.tok.ui.login.login.LoginActivity;
import com.client.tok.ui.login.signup.SignUpActivity;
import com.client.tok.ui.profileedit.ProfileEditActivity;
import com.client.tok.ui.pwd.ChangePwdActivity;
import com.client.tok.ui.scan.ScanActivity;
import com.client.tok.ui.setting.SettingActivity;
import com.client.tok.ui.setting.notify.NotifySetActivity;
import com.client.tok.ui.share.ShareActivity;
import com.client.tok.ui.video.VideoRecordActivity;
import com.client.tok.ui.web.WebViewActivity;
import com.client.tok.utils.StringUtils;

public class PageJumpIn extends BasePageJump {

    public static void jumpLoginHomePage(Context context) {
        Intent intent = createIntent(context, LoginSignUpHomeActivity.class);
        jump(context, intent);
    }

    public static void jumpCreateAccountPage(Context context) {
        Intent intent = createIntent(context, SignUpActivity.class);
        jump(context, intent);
    }

    public static void jumpLoginPage(Context context) {
        Intent intent = createIntent(context, LoginActivity.class);
        jump(context, intent);
    }

    public static void jumpHomePage(Context context) {
        Intent intent = createIntent(context, HomeActivity.class);
        jump(context, intent);
    }

    public static void jumpSettingPage(Context context) {
        Intent intent = createIntent(context, SettingActivity.class);
        jump(context, intent);
    }

    public static void jumpSetNotificationPage(Context context) {
        Intent intent = createIntent(context, NotifySetActivity.class);
        jump(context, intent);
    }

    public static void jumpMyTokIdPage(Context context) {
        jumpTokIdPage(context, GlobalParams.TYPE_MINE, null);
    }

    /**
     * @param type id type, friend or group
     * @param key key
     */
    private static void jumpTokIdPage(Context context, String type, String key) {
        Intent intent = createIntent(context, TokIdActivity.class);
        if (!StringUtils.isEmpty(type)) {
            intent.putExtra(IntentConstants.ID_TYPE, type);
        }
        if (!StringUtils.isEmpty(key)) {
            intent.putExtra(IntentConstants.TOK_ID, key);
        }
        jump(context, intent);
    }

    public static void jumpAddFriendsPage(Context context) {
        jumpAddFriendsPage(context, null);
    }

    public static void jumpAddFriendsPage(Context context, String tokId) {
        Intent intent = createIntent(context, AddFriendsActivity.class);
        intent.putExtra(IntentConstants.TOK_ID, tokId);
        jump(context, intent);
    }

    public static void jumpScanPage(Context context) {
        Intent intent = createIntent(context, ScanActivity.class);
        jump(context, intent);
    }

    public static void jumpContactRequestPage(Context context) {
        Intent intent = createIntent(context, ContactReqActivity.class);
        jump(context, intent);
    }

    public static void jumpContactReqDetailPage(Context context, String key) {
        Intent intent = createIntent(context, ContactReqDetailActivity.class);
        intent.putExtra(IntentConstants.REQ_FRIEND_KEY, key);
        jump(context, intent);
    }

    public static void jumpFriendInfoPage(Context context, String groupNumber, String pk) {
        Intent intent = createIntent(context, FriendInfoActivity.class);
        intent.putExtra(IntentConstants.GROUP_ID, groupNumber);
        intent.putExtra(IntentConstants.PK, pk);
        jump(context, intent);
    }

    public static void jumpOfflineBotInfoPage(Context context) {
        Intent intent = createIntent(context, OfflineBotActivity.class);
        jump(context, intent);
    }

    public static void jumpOfflineBotDetailPage(Context context) {
        Intent intent = createIntent(context, OfflineBotDetailActivity.class);
        jump(context, intent);
    }

    public static void jumpFriendChatPage(Context context, String pk) {
        jumpChatPage(context, pk, GlobalParams.CHAT_FRIEND);
    }

    private static void jumpChatPage(Context context, String tokId, String chatType) {
        Intent intent = createIntent(context, Chat2Activity.class);
        intent.putExtra(IntentConstants.PK, tokId);
        intent.putExtra(IntentConstants.CHAT_TYPE, chatType);
        jump(context, intent);
    }

    public static void jumpAboutUsPage(Context context) {
        Intent intent = createIntent(context, AboutUsActivity.class);
        jump(context, intent);
    }

    public static void jumpMyInfoPage(Context context) {
        Intent intent = createIntent(context, MyInfoActivity.class);
        jump(context, intent);
    }

    public static void jumpImgShowPage(Context context, String path) {
        Intent intent = createIntent(context, ImgShowActivity.class);
        intent.putExtra(IntentConstants.FILE, path);
        jump(context, intent);
    }

    public static void jumpProfileEditPage(Context context, String key, int whatToDo) {
        Intent intent = createIntent(context, ProfileEditActivity.class);
        intent.putExtra(IntentConstants.PK, key);
        intent.putExtra(IntentConstants.WHAT_TODO, whatToDo);
        jump(context, intent);
    }

    public static void jumpClipImgPage(Activity activity, String inPath, String outPath) {
        Intent intent = createIntent(activity, ClipImgActivity.class);
        intent.putExtra(IntentConstants.IMG_IN_PATH, inPath);
        intent.putExtra(IntentConstants.IMG_OUT_PATH, outPath);
        jumpForResult(activity, intent, GlobalParams.REQ_CODE_PORTRAIT);
    }

    public static void jumpSafePage(Context context) {
        Intent intent = createIntent(context, WebViewActivity.class);
        String safePath = "file:///android_asset/" + "chatsafe/" + StringUtils.getTextFromResId(
            R.string.chat_safe_file_name);
        intent.putExtra(IntentConstants.WEB_PATH, safePath);
        jump(context, intent);
    }

    public static void jumpSharePage(Context context) {
        Intent intent = createIntent(context, ShareActivity.class);
        jump(context, intent);
    }

    public static void jumpChangePwdPage(Context context, String userName) {
        Intent intent = createIntent(context, ChangePwdActivity.class);
        intent.putExtra(IntentConstants.USER_NAME, userName);
        jump(context, intent);
    }

    public static void jumpVideoRecordPage(Activity activity) {
        Intent intent = createIntent(activity, VideoRecordActivity.class);
        jumpForResult(activity, intent, GlobalParams.REQ_CODE_RECORD_VIDEO);
    }

    public static void jumpActivity(Context context, String activityName) {
        Intent intent = createIntent(context, activityName);
        jump(context, intent);
    }

    private static Intent createIntent(Context context, Class<?> cls) {
        if (context == null) {
            context = TokApplication.getInstance();
        }
        return new Intent(context, cls);
    }

    private static Intent createIntent(Context context, String className) {
        if (context == null) {
            context = TokApplication.getInstance();
        }
        Intent intent = new Intent();
        intent.setClassName(context, className);
        return intent;
    }
}
