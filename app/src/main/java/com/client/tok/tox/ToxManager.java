package com.client.tok.tox;

import android.content.Context;
import com.client.tok.R;
import com.client.tok.bean.DhtNode;
import com.client.tok.bean.DhtNodeList;
import com.client.tok.bean.UserInfo;
import com.client.tok.db.repository.InfoRepository;
import com.client.tok.db.repository.UserRepository;
import com.client.tok.msg.UserStatus;
import com.client.tok.msg.callbacks.ToxCallbackListener;
import com.client.tok.pagejump.SharePKeys;
import com.client.tok.utils.FileUtilsJ;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.PreferenceUtils;
import com.google.gson.Gson;
import im.tox.tox4j.core.enums.ToxUserStatus;
import im.tox.tox4j.core.exceptions.ToxBootstrapException;
import im.tox.tox4j.core.options.ToxOptions;
import im.tox.tox4j.impl.jni.ToxAvImpl;
import java.util.Collections;
import java.util.List;

public class ToxManager {
    private String TAG = "ToxCoreManager";
    private static ToxManager sInstance;
    public ToxCoreBase toxBase;
    public ToxAvBase toxAvBase;
    public ToxDataFile toxData;

    private boolean mIsInited = false;

    private ToxManager() {
    }

    public static ToxManager getManager() {
        if (sInstance == null) {
            synchronized (ToxManager.class) {
                if (sInstance == null) {
                    sInstance = new ToxManager();
                }
            }
        }
        return sInstance;
    }

    public void initTox(Context context) {
        if (!mIsInited) {
            mIsInited = true;
            UserRepository userRepository = State.userRepo();
            toxData = new ToxDataFile(context, userRepository.getActiveUser());
            ToxOptions options = new ToxOptions(toxData.loadAsSaveType());
            toxBase = new ToxCoreBase(options);
            LogUtil.i(TAG, "coremanager toxBase:" + toxBase.hashCode());
            if (!toxData.doesFileExist()) {
                toxData.saveFile(toxBase.getSaveData());
            }
            PreferenceUtils.saveString(SharePKeys.CHAT_ID, toxBase.getSelfAddress().toString());

            State.setDb(new InfoRepository());
            InfoRepository infoRepo = State.infoRepo();
            infoRepo.clearAllFileNumbers();
            infoRepo.setAllOffline();
            infoRepo.synchroniseWithTox(toxBase.getFriendList());

            UserInfo userInfo = userRepository.getActiveUserDetails();
            toxBase.setName(userInfo.getNickname());
            toxBase.setSignature(userInfo.getStatusMessage());

            ToxUserStatus newStatus = UserStatus.getUserStatusFromString(userInfo.getStatus());
            toxBase.setStatus(newStatus);

            bootStrap(context);

            //init toxAvBase
            //toxAvBase = new ToxAvBase(toxBase.getInstanceNumber());
        }
    }

    private boolean bootStrap(Context context) {
        boolean bootStraped = false;
        List<DhtNode> list = readDhtNodes(context);
        LogUtil.i(TAG, "node list size:" + list.size());
        if (list != null && list.size() > 0) {
            Collections.shuffle(list);
            try {
                for (DhtNode dhtNode : list) {
                    LogUtil.i(TAG, "node infor:" + dhtNode.toString());
                    toxBase.bootStrap(dhtNode.getIpv4(), dhtNode.getPort(), dhtNode.getPublicKey());
                    bootStraped = true;
                }
            } catch (ToxBootstrapException e) {
                e.printStackTrace();
            }
        }
        LogUtil.i(TAG, "bootStrap success:" + bootStraped);
        return bootStraped;
    }

    private List<DhtNode> readDhtNodes(Context context) {
        String nodeFileStr = FileUtilsJ.readFromRaw(context, R.raw.node_file);
        DhtNodeList list = new Gson().fromJson(nodeFileStr, DhtNodeList.class);
        return list.getNodes();
    }

    public void iterate(ToxCallbackListener listener) {
        if (toxBase != null) {
            LogUtil.i(TAG, "CoreManager iterate:" + toxBase.hashCode());
            toxBase.iterate(listener);
        }
    }

    public boolean exportDataFile(String destDir, String pwd) {
        save();
        return toxData.exportFile(destDir, pwd);
    }

    public void save() {
        if (toxData != null && toxBase != null) {
            toxData.saveFile(toxBase.getSaveData());
        }
    }

    public void saveAndClose() {
        LogUtil.i(TAG, "save and close,isInit:" + mIsInited);
        if (mIsInited) {
            save();
            mIsInited = false;
        }
        if (toxBase != null) {
            toxBase.close();
            toxBase = null;
        }

        sInstance = null;
    }
}
