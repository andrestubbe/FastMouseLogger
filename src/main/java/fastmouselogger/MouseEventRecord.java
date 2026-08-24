package fastmouselogger;

/**
 * Immutable, high-density raw mouse event record.
 * Packed as compact binary entry: [flags (1 byte)][dx (2 bytes)][dy (2 bytes)][timestamp_delta (varLong)].
 *
 * @param timestamp Epoch timestamp in milliseconds.
 * @param flags Button/state bitmask (0 = Move, 1 = Left Down, 2 = Left Up, 4 = Right Down, 8 = Right Up, 16 = Wheel).
 * @param dx Raw delta X or absolute X coordinate.
 * @param dy Raw delta Y or absolute Y coordinate.
 * @param wheelDelta Scroll wheel offset (if flags has wheel bit set).
 */
public record MouseEventRecord(long timestamp, int flags, int dx, int dy, int wheelDelta) {
    public static final int FLAG_MOVE = 0x00;
    public static final int FLAG_LEFT_DOWN = 0x01;
    public static final int FLAG_LEFT_UP = 0x02;
    public static final int FLAG_RIGHT_DOWN = 0x04;
    public static final int FLAG_RIGHT_UP = 0x08;
    public static final int FLAG_MIDDLE_DOWN = 0x10;
    public static final int FLAG_MIDDLE_UP = 0x20;
    public static final int FLAG_WHEEL = 0x40;

    public boolean isButtonPress() {
        return (flags & (FLAG_LEFT_DOWN | FLAG_RIGHT_DOWN | FLAG_MIDDLE_DOWN)) != 0;
    }

    public boolean isLeftClick() {
        return (flags & FLAG_LEFT_DOWN) != 0;
    }

    public boolean isRightClick() {
        return (flags & FLAG_RIGHT_DOWN) != 0;
    }
}
