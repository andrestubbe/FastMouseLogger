package fastmouselogger;

/**
 * Functional callback listener for live mouse event streaming.
 */
@FunctionalInterface
public interface MouseEventListener {
    /**
     * Invoked when a mouse event is recorded.
     *
     * @param record The raw mouse event record.
     */
    void onMouseEvent(MouseEventRecord record);
}
