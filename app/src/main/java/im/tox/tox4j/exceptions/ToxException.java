package im.tox.tox4j.exceptions;

public class ToxException extends Exception {
    public Enum code;
    public String msg;

    public ToxException(Enum t, String message) {
        code = t;
        msg = message;
    }

    @Override
    public String getMessage() {
        return "Error code:" + code.name() + ",Error message:" + msg;
    }
}
