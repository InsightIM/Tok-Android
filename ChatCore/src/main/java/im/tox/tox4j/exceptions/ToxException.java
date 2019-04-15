package im.tox.tox4j.exceptions;

public class ToxException extends Exception {
    private Enum errorCode;
    private String msg;

    public ToxException(Enum t, String message) {
        errorCode = t;
        msg = message;
    }

    @Override
    public String getMessage() {
        return "Error code:" + errorCode.name() + ",Error message:" + msg;
    }
}
