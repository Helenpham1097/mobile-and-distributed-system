package assign2.message;

import java.util.Arrays;

/**
 * This is the message to request to take snapshot
 */
public class MarkerMessage extends Message {

    // peers that inits the snapshot request
    private String initializer;

    private boolean senderSnapshotDone;

    public MarkerMessage(int order,
                         int[] vectorClocks,
                         String peerName,
                         String initializer,
                         boolean senderSnapshotDone) {
        super(order, vectorClocks, peerName);
        this.initializer = initializer;
        this.senderSnapshotDone = senderSnapshotDone;
    }

    public String getInitializer() {
        return initializer;
    }

    public boolean isSenderSnapshotDone() {
        return senderSnapshotDone;
    }

    @Override
    public String toString() {
        return "MarkerMessage{" +
                "initializer='" + initializer + '\'' +
                ", sender='" + getPeerName() + '\'' +
                ", senderSnapshotDone=" + senderSnapshotDone +
                ", vectorClocks=" + Arrays.toString(getVectorClocks()) +
                '}';
    }
}
