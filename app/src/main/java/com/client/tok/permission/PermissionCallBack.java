package com.client.tok.permission;

import android.support.annotation.NonNull;
import java.util.List;

public class PermissionCallBack {
    public void onPermissionsAllGranted(int requestCode, @NonNull List<String> grantedPers) {

    }

    public void onPermissionsGranted(int requestCode, @NonNull List<String> grantedPers) {

    }

    public void onPermissionsDenied(int requestCode, @NonNull List<String> deniedPers) {

    }

    public void onCancelPermissionRationale(int requestCode) {

    }
}
