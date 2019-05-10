package im.tox.tox4j.core.enums;

import im.tox.tox4j.core.IToxCore;

/**
 * Represents message types for {@link IToxCore#friendSendMessage} and group chat
 * messages.
 */
public enum ToxMessageType {
    /**
     * Normal text message. Similar to PRIVMSG on IRC.
     */
    NORMAL(0),
    /**
     * A message describing an user action. This is similar to /me (CTCP ACTION)
     * on IRC.
     */
    ACTION(1),
    BOT(2),
    FORWARD(3),
    GROUP(4),
    OFF_LINE(6);

    private int type;

    ToxMessageType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }}
