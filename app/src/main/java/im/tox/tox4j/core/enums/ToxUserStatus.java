package im.tox.tox4j.core.enums;

/**
 * Represents the possible statuses a client can have.
 */
public enum ToxUserStatus {
    /**
     * User is online and available.
     */
    NONE,
    /**
     * User is away. Clients can set this e.g. after a user defined
     * inactivity time.
     */
    AWAY,
    /**
     * User is busy. Signals to other clients that this client does not
     * currently wish to communicate.
     */
    BUSY,
}
