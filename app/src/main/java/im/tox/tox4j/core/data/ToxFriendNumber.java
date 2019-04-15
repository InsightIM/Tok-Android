package im.tox.tox4j.core.data;

public class ToxFriendNumber {
    public int value;

    private ToxFriendNumber(int value) {
        this.value = value;
    }

    public static ToxFriendNumber unsafeFromInt(int value) {
        return new ToxFriendNumber(value);
    }

    public static int toInt(ToxFriendNumber toxFriendNumber) {
        return toxFriendNumber.value;
    }
}
