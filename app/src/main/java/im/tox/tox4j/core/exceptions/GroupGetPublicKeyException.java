package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;

/*****Group相关增加  wxf******/
public final class GroupGetPublicKeyException extends ToxException {
    public GroupGetPublicKeyException(Code code) {
        this(code, "");
    }

    public GroupGetPublicKeyException(Code code, String message) {
        super(code, message);
    }

    public enum Code {
        /**
         * The friendNumber passed did not designate a valid friend.
         */
        GROUP_NOT_FOUND,
    }
}
