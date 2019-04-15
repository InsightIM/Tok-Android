package im.tox.tox4j.core.enums;

/**
 * Represents message types for {@link im.tox.tox4j.core.ToxCore#friendSendMessage} and group chat
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
}
