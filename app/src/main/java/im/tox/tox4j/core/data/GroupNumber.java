package im.tox.tox4j.core.data;

/**
 * 新增 wxf todo
 */
public class GroupNumber {
    public int value;

    private GroupNumber(int value) {
        this.value = value;
    }

    public static GroupNumber unsafeFromInt(int value) {
        return new GroupNumber(value);
    }

    public static int toInt(GroupNumber toxFriendNumber) {
        return toxFriendNumber.value;
    }
}
