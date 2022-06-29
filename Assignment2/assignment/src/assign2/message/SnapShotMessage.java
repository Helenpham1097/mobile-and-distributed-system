package assign2.message;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SnapShotMessage extends Message {

    private final Map<String, List<Message>> pendingMessages;

    public SnapShotMessage(int order, int[] vectorClocks, String peerName, Map<String, List<Message>> pendingMessages) {
        super(order, vectorClocks, peerName);
        this.pendingMessages = pendingMessages;
    }

    public Map<String, List<Message>> getPendingMessages() {
        return pendingMessages;
    }

    @Override
    public String toString() {
        return "SnapShotMessage{" +
                "pendingMessages=" + pendingMessages + '\'' +
                ", sender='" + getPeerName() + '\'' +
                ", vectorClocks=" + Arrays.toString(getVectorClocks()) +
                '}';
    }
}
