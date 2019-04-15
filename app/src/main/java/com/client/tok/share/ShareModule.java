package com.client.tok.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import com.client.tok.R;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.utils.StringUtils;
import java.io.File;

public class ShareModule {
    private static String TYPE_TXT = "text/*";
    private static String TYPE_IMG = "image/*";
    private static String TYPE_ANY = "*/*";

    public static void shareDownLoad(Context context) {
        String downLoadPath = StringUtils.getTextFromResId(R.string.app_down_load_url);
        share(context, TYPE_TXT, downLoadPath, null);
    }

    public static void shareText(Context context, String content) {
        share(context, TYPE_TXT, content, null);
    }

    public static void shareImg(Context context, String path) {
        share(context, TYPE_IMG, null, path);
    }

    /**
     * TODO:not useful，can't share text+img
     */
    public static void shareTextWithImg(Context context, String content, String path) {
        share(context, TYPE_ANY, content, path);
    }

    /**
     * TODO:not useful，can't share text+img
     */
    private static void share(Context context, String type, String content, String path) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (!StringUtils.isEmpty(content)) {
            intent.putExtra(Intent.EXTRA_TEXT, content);
        }
        if (!StringUtils.isEmpty(path)) {
            Uri shareFileUri =
                FileProvider.getUriForFile(context, GlobalParams.PROVIDER_AUTH, new File(path));
            intent.putExtra(Intent.EXTRA_STREAM, shareFileUri);
        }
        intent.setType(type);
        startActivity(context,
            Intent.createChooser(intent, StringUtils.getTextFromResId(R.string.share_with)));
    }

    private static void startActivity(Context context, Intent intent) {
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }
}
