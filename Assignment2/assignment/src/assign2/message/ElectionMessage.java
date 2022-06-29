package assign2.message;

public class ElectionMessage extends Message {

    private long messageId;

    public ElectionMessage(int order, int[] vectorClocks, String peerName, long messageId) {
        super(order, vectorClocks, peerName);
        this.messageId = messageId;
    }

    public long getMessageId() {
        return messageId;
    }
}
