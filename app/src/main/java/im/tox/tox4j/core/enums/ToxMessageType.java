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
    NORMAL,
    /**
     * A message describing an user action. This is similar to /me (CTCP ACTION)
     * on IRC.
     */
    ACTION,
    BOT,
    FORWARD,
    GROUP,
    HELLO,
    DRAFT
}
