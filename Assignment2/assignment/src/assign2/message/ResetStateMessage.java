package assign2.message;

import java.util.Arrays;

public class ResetStateMessage extends Message{

    public ResetStateMessage(int order, int[] vectorClocks, String peerName) {
        super(order, vectorClocks, peerName);
    }

    @Override
    public String toString() {
        return "ResetStateMessage{" +
                "order=" + getOrder() +
                ", sender=" + getPeerName() +
                ", vectorClocks=" + Arrays.toString(getVectorClocks()) +
                '}';
    }
}
