package com.client.tok.constant;

/**
 * https://toxme.io
 * upload to this website,every one can find you
 */
public enum ToxMeError {
    OK("0"),
    METHOD_UNSUPPORTED("-1"),
    NOTSECURE("-2"),
    BAD_PAYLOAD("-3"),
    NAME_TAKEN("-25"),
    DUPE_ID("-26"),
    UNKNOWN_NAME("-30"),
    INVALID_ID("-31"),
    LOOKUP_FAILED("-41"),
    NO_USER("-42"),
    LOOKUP_INTERNAL("-43"),
    RATE_LIMIT("-4"),
    KALIUM_LINK_ERROR("KALIUM"),
    INVALID_DOMAIN("INVALID_DOMAIN"),
    INTERNAL("INTERNAL"),
    JSON_ERROR("JSON"),
    ENCODING_ERROR("ENCODING"),
    CONNECTION_ERROR("CONNECTION"),
    UNKNOWN("");

    private String error;

    ToxMeError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public static String getErrMsg(ToxMeError toxMeError) {
        String msg = "";
        switch (toxMeError) {
            case OK:
                msg = "OK";
                break;
            case METHOD_UNSUPPORTED:
                msg = "Client didn't POST to /api";
                break;
            case NOTSECURE:
                msg = "Client is not using a secure connection";
                break;
            case BAD_PAYLOAD:
                msg = "Bad encrypted payload (not encrypted with public key)";
                break;
            case NAME_TAKEN:
                msg = "Name is taken";
                break;
            case DUPE_ID:
                msg = "The public key given is bound to a name already";
                break;
            case UNKNOWN_NAME:
                msg = "Name not found";
                break;
            case INVALID_ID:
                msg = "Sent invalid data in place of an ID";
                break;
            case LOOKUP_FAILED:
                msg = "Lookup failed because of an error on the other domain's side.";
                break;
            case NO_USER:
                msg = "Lookup failed because that user doesn't exist on the domain";
                break;
            case LOOKUP_INTERNAL:
                msg = "Lookup failed because of a server error";
                break;
            case RATE_LIMIT:
                msg = "Client is publishing IDs too fast";
                break;
            case KALIUM_LINK_ERROR:
                msg = "Kalium link error";
                break;
            case INVALID_DOMAIN:
                msg = "Invalid ToxMe domain";
                break;
            case INTERNAL:
                msg = "Internal error";
                break;
            case JSON_ERROR:
                msg = "Error constructing JSON";
                break;
            case ENCODING_ERROR:
                msg = "Encoding error";
                break;
            case UNKNOWN:
                msg = "Unknown error code";
                break;
            default:
                msg = toxMeError.getError();
        }
        return msg;
    }
}
