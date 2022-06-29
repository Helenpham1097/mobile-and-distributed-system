package assign2.message;

public class LeaderElectionMessage extends Message {

    private final long messageId;

    public LeaderElectionMessage(int order, int[] vectorClocks, String peerName, long messageId) {
        super(order, vectorClocks, peerName);
        this.messageId = messageId;
    }

    public long getMessageId() {
        return messageId;
    }
}
