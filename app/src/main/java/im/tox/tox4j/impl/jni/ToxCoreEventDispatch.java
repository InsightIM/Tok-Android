package im.tox.tox4j.impl.jni;

import com.client.tok.utils.LogUtil;
import im.tox.proto.Core;
import im.tox.tox4j.core.callbacks.ToxCoreEventListener;
import im.tox.tox4j.core.data.ToxFilename;
import im.tox.tox4j.core.data.ToxFriendMessage;
import im.tox.tox4j.core.data.ToxFriendNumber;
import im.tox.tox4j.core.data.ToxFriendRequestMessage;
import im.tox.tox4j.core.data.ToxLosslessPacket;
import im.tox.tox4j.core.data.ToxLossyPacket;
import im.tox.tox4j.core.data.ToxNickname;
import im.tox.tox4j.core.data.ToxPublicKey;
import im.tox.tox4j.core.data.ToxStatusMessage;
import im.tox.tox4j.core.enums.ToxConnection;
import im.tox.tox4j.core.enums.ToxFileControl;
import im.tox.tox4j.core.enums.ToxMessageType;
import im.tox.tox4j.core.enums.ToxUserStatus;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;

public class ToxCoreEventDispatch {
    private static String TAG = "ToxCoreEventDispatch";

    public static void dispatch(ToxCoreEventListener listener, byte[] eventData) {
        try {
            if (eventData != null) { // scala style:ignore null
                Core.CoreEvents events = Core.CoreEvents.parseFrom(eventData);
                dispatchEvents(listener, events);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void dispatchEvents(ToxCoreEventListener listener, Core.CoreEvents events) {
        dispatchSelfConnectionStatus(listener, events.getSelfConnectionStatusList());
        dispatchFriendName(listener, events.getFriendNameList());
        dispatchFriendStatusMessage(listener, events.getFriendStatusMessageList());
        dispatchFriendStatus(listener, events.getFriendStatusList());
        dispatchFriendConnectionStatus(listener, events.getFriendConnectionStatusList());
        dispatchFriendTyping(listener, events.getFriendTypingList());
        dispatchFriendReadReceipt(listener, events.getFriendReadReceiptList());
        dispatchFriendRequest(listener, events.getFriendRequestList());
        dispatchFriendMessage(listener, events.getFriendMessageList());
        dispatchFileRecvControl(listener, events.getFileRecvControlList());
        dispatchFileChunkRequest(listener, events.getFileChunkRequestList());
        dispatchFileRecv(listener, events.getFileRecvList());
        dispatchFileRecvChunk(listener, events.getFileRecvChunkList());
        dispatchFriendLossyPacket(listener, events.getFriendLossyPacketList());
        dispatchFriendLosslessPacket(listener, events.getFriendLosslessPacketList());
        dispatchOfflineMessage(listener, events.getFriendMessageOfflineList());
    }

    private static void dispatchSelfConnectionStatus(ToxCoreEventListener handler,
        List<Core.SelfConnectionStatus> selfConnectionStatus) {
        LogUtil.i(TAG, "dispatchSelfConnectionStatus" + selfConnectionStatus);
        if (selfConnectionStatus != null && selfConnectionStatus.size() > 0) {
            for (Core.SelfConnectionStatus status : selfConnectionStatus) {
                handler.selfConnectionStatus(convert(status.getConnectionStatus()));
            }
        }
    }

    private static void dispatchFriendName(ToxCoreEventListener handler,
        List<Core.FriendName> friendNameList) {
        LogUtil.i(TAG, "dispatchFriendName" + friendNameList);
        if (friendNameList != null && friendNameList.size() > 0) {
            for (Core.FriendName name : friendNameList) {
                handler.friendName(ToxFriendNumber.unsafeFromInt(name.getFriendNumber()),
                    ToxNickname.unsafeFromValue(name.getName().toByteArray()));
            }
        }
    }

    private static void dispatchFriendStatusMessage(ToxCoreEventListener handler,
        List<Core.FriendStatusMessage> statusMsgList) {
        LogUtil.i(TAG, "dispatchFriendStatusMessage" + statusMsgList);
        if (statusMsgList != null && statusMsgList.size() > 0) {
            for (Core.FriendStatusMessage statusMsg : statusMsgList) {
                handler.friendStatusMessage(
                    ToxFriendNumber.unsafeFromInt(statusMsg.getFriendNumber()),
                    ToxStatusMessage.unsafeFromValue(statusMsg.getMessage().toByteArray()));
            }
        }
    }

    private static void dispatchFriendStatus(ToxCoreEventListener handler,
        List<Core.FriendStatus> friendStatus) {
        LogUtil.i(TAG, "dispatchFriendStatus" + friendStatus);
        if (friendStatus != null && friendStatus.size() > 0) {
            for (Core.FriendStatus status : friendStatus) {
                handler.friendStatus(ToxFriendNumber.unsafeFromInt(status.getFriendNumber()),
                    convert(status.getStatus()));
            }
        }
    }

    private static void dispatchFriendConnectionStatus(ToxCoreEventListener handler,
        List<Core.FriendConnectionStatus> connectionStatusList) {
        LogUtil.i(TAG, "dispatchFriendConnectionStatus" + connectionStatusList);
        if (connectionStatusList != null && connectionStatusList.size() > 0) {
            for (Core.FriendConnectionStatus status : connectionStatusList) {
                handler.friendConnectionStatus(
                    ToxFriendNumber.unsafeFromInt(status.getFriendNumber()),
                    convert(status.getConnectionStatus()));
            }
        }
    }

    private static void dispatchFriendTyping(ToxCoreEventListener handler,
        List<Core.FriendTyping> typingList) {
        LogUtil.i(TAG, "dispatchFriendTyping" + typingList);
        if (typingList != null && typingList.size() > 0) {
            for (Core.FriendTyping typing : typingList) {
                handler.friendTyping(ToxFriendNumber.unsafeFromInt(typing.getFriendNumber()),
                    typing.getIsTyping());
            }
        }
    }

    private static void dispatchFriendReadReceipt(ToxCoreEventListener handler,
        List<Core.FriendReadReceipt> friendReadReceiptList) {
        LogUtil.i(TAG, "dispatchFriendReadReceipt" + friendReadReceiptList);
        if (friendReadReceiptList != null && friendReadReceiptList.size() > 0) {
            for (Core.FriendReadReceipt receipt : friendReadReceiptList) {
                handler.friendReadReceipt(ToxFriendNumber.unsafeFromInt(receipt.getFriendNumber()),
                    receipt.getMessageId());
            }
        }
    }

    private static void dispatchFriendRequest(ToxCoreEventListener handler,
        List<Core.FriendRequest> friendRequestList) {
        LogUtil.i(TAG, "dispatchFriendRequest" + friendRequestList);
        if (friendRequestList != null && friendRequestList.size() > 0) {
            for (Core.FriendRequest request : friendRequestList) {
                handler.friendRequest(
                    ToxPublicKey.unsafeFromValue(request.getPublicKey().toByteArray()),
                    request.getTimeDelta(),
                    ToxFriendRequestMessage.unsafeFromValue(request.getMessage().toByteArray()));
            }
        }
    }

    private static void dispatchFriendMessage(ToxCoreEventListener handler,
        List<Core.FriendMessage> friendMsgList) {
        LogUtil.i(TAG, "dispatchFriendMessage" + friendMsgList);
        if (friendMsgList != null && friendMsgList.size() > 0) {
            for (Core.FriendMessage friendMsg : friendMsgList) {
                handler.friendMessage(ToxFriendNumber.unsafeFromInt(friendMsg.getFriendNumber()),
                    convert(friendMsg.getType()), friendMsg.getTimeDelta(),
                    ToxFriendMessage.unsafeFromValue(friendMsg.getMessage().toByteArray()));
            }
        }
    }

    private static char[] getChars(byte[] bytes) {
        Charset cs = Charset.forName("UTF-8");
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(bytes);
        bb.flip();
        CharBuffer cb = cs.decode(bb);
        return cb.array();
    }

    private static void dispatchFileRecvControl(ToxCoreEventListener handler,
        List<Core.FileRecvControl> fileRecvControlList) {
        LogUtil.i(TAG, "dispatchFileRecvControl" + fileRecvControlList);
        if (fileRecvControlList != null && fileRecvControlList.size() > 0) {
            for (Core.FileRecvControl control : fileRecvControlList) {
                handler.fileRecvControl(ToxFriendNumber.unsafeFromInt(control.getFriendNumber()),
                    control.getFileNumber(), convert(control.getControl()));
            }
        }
    }

    private static void dispatchFileChunkRequest(ToxCoreEventListener handler,
        List<Core.FileChunkRequest> fileChunkRequestList) {
        LogUtil.i(TAG, "dispatchFileChunkRequest" + fileChunkRequestList);
        if (fileChunkRequestList != null && fileChunkRequestList.size() > 0) {
            for (Core.FileChunkRequest request : fileChunkRequestList) {
                handler.fileChunkRequest(ToxFriendNumber.unsafeFromInt(request.getFriendNumber()),
                    request.getFileNumber(), request.getPosition(), request.getLength());
            }
        }
    }

    private static void dispatchFileRecv(ToxCoreEventListener handler,
        List<Core.FileRecv> fileRecvList) {
        LogUtil.i(TAG, "dispatchFileRecv" + fileRecvList);
        if (fileRecvList != null && fileRecvList.size() > 0) {
            for (Core.FileRecv fileRecv : fileRecvList) {
                handler.fileRecv(ToxFriendNumber.unsafeFromInt(fileRecv.getFriendNumber()),
                    fileRecv.getFileNumber(), fileRecv.getKind(), fileRecv.getFileSize(),
                    ToxFilename.unsafeFromValue(fileRecv.getFilename().toByteArray()));
            }
        }
    }

    private static void dispatchFileRecvChunk(ToxCoreEventListener handler,
        List<Core.FileRecvChunk> fileRecvChunkList) {
        LogUtil.i(TAG, "dispatchFileRecvChunk" + fileRecvChunkList);
        if (fileRecvChunkList != null && fileRecvChunkList.size() > 0) {
            for (Core.FileRecvChunk chunk : fileRecvChunkList) {
                handler.fileRecvChunk(ToxFriendNumber.unsafeFromInt(chunk.getFriendNumber()),
                    chunk.getFileNumber(), chunk.getPosition(), chunk.getData().toByteArray());
            }
        }
    }

    private static void dispatchFriendLossyPacket(ToxCoreEventListener handler,
        List<Core.FriendLossyPacket> friendLossyPacketList) {
        LogUtil.i(TAG, "dispatchFriendLossyPacket" + friendLossyPacketList);
        if (friendLossyPacketList != null && friendLossyPacketList.size() > 0) {
            for (Core.FriendLossyPacket packet : friendLossyPacketList) {
                handler.friendLossyPacket(ToxFriendNumber.unsafeFromInt(packet.getFriendNumber()),
                    ToxLossyPacket.unsafeFromValue(packet.getData().toByteArray()));
            }
        }
    }

    private static void dispatchFriendLosslessPacket(ToxCoreEventListener handler,
        List<Core.FriendLosslessPacket> friendLosslessPacketList) {
        LogUtil.i(TAG, "dispatchFriendLosslessPacket" + friendLosslessPacketList);
        if (friendLosslessPacketList != null && friendLosslessPacketList.size() > 0) {
            for (Core.FriendLosslessPacket packet : friendLosslessPacketList) {
                handler.friendLosslessPacket(
                    ToxFriendNumber.unsafeFromInt(packet.getFriendNumber()),
                    ToxLosslessPacket.unsafeFromValue(packet.getData().toByteArray()));
            }
        }
    }

    private static void dispatchOfflineMessage(ToxCoreEventListener handler,
        List<Core.FriendMessageOffline> offlineInfos) {
        LogUtil.i(TAG,
            "dispatchOfflineMessage" + offlineInfos + ",size:" + (offlineInfos == null ? "null"
                : offlineInfos.size()));
        if (offlineInfos != null && offlineInfos.size() > 0) {
            for (Core.FriendMessageOffline offlineInfo : offlineInfos) {
                handler.offlineMessage(offlineInfo.getCmd(),
                    offlineInfo.getMessage().toByteArray());
            }
        }
    }

    private static ToxConnection convert(Core.Connection.Type status) {
        if (Core.Connection.Type.NONE == status) {
            return ToxConnection.NONE;
        } else if (Core.Connection.Type.TCP == status) {
            return ToxConnection.TCP;
        } else if (Core.Connection.Type.UDP == status) {
            return ToxConnection.UDP;
        } else {
            return ToxConnection.NONE;
        }
    }

    private static ToxUserStatus convert(Core.UserStatus.Type status) {
        if (Core.UserStatus.Type.NONE == status) {
            return ToxUserStatus.NONE;
        } else if (Core.UserStatus.Type.AWAY == status) {
            return ToxUserStatus.AWAY;
        } else if (Core.UserStatus.Type.BUSY == status) {
            return ToxUserStatus.BUSY;
        } else {
            return ToxUserStatus.NONE;
        }
    }

    private static Core.UserStatus.Type convert(ToxUserStatus status) {
        if (ToxUserStatus.NONE == status) {
            return Core.UserStatus.Type.NONE;
        } else if (ToxUserStatus.AWAY == status) {
            return Core.UserStatus.Type.AWAY;
        } else if (ToxUserStatus.BUSY == status) {
            return Core.UserStatus.Type.BUSY;
        } else {
            return Core.UserStatus.Type.NONE;
        }
    }

    private static ToxFileControl convert(Core.FileControl.Type control) {
        if (Core.FileControl.Type.RESUME == control) {
            return ToxFileControl.RESUME;
        } else if (Core.FileControl.Type.PAUSE == control) {
            return ToxFileControl.PAUSE;
        } else if (Core.FileControl.Type.CANCEL == control) {
            return ToxFileControl.CANCEL;
        } else {
            return ToxFileControl.RESUME;
        }
    }

    private static ToxMessageType convert(Core.MessageType.Type messageType) {
        if (Core.MessageType.Type.NORMAL == messageType) {
            return ToxMessageType.NORMAL;
        } else if (Core.MessageType.Type.ACTION == messageType) {
            return ToxMessageType.ACTION;
        } else if (Core.MessageType.Type.BOT == messageType) {
            return ToxMessageType.BOT;
        } else if (Core.MessageType.Type.FORWARD == messageType) {
            return ToxMessageType.FORWARD;
        } else if (Core.MessageType.Type.GROUP == messageType) {
            return ToxMessageType.GROUP;
        } else if (Core.MessageType.Type.OFFLINE == messageType) {
            return ToxMessageType.OFF_LINE;
        } else {
            return ToxMessageType.NORMAL;
        }
    }

    private static void log(String method, List list) {
        LogUtil.i(TAG, "method:" + method + "size:" + list.size());
    }
}
