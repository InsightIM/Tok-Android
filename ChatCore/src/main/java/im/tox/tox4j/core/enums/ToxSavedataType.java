package im.tox.tox4j.core.enums;

/**
 * Type of savedata to create the Tox instance from.
 */
public enum ToxSavedataType {
    /**
     * No savedata.
     */
    NONE,
    /**
     * Savedata is one that was obtained from {@link im.tox.tox4j.core.ToxCore#getSavedata}.
     */
    TOX_SAVE,
    /**
     * Savedata is a secret key of length {@link ToxCoreConstants#SecretKeySize}.
     */
    SECRET_KEY,
}
