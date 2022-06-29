package assign2.message;

import java.util.Arrays;

public class LeaveMessage extends Message{

    public LeaveMessage(int order, int[] vectorClocks, String peerName) {
        super(order, vectorClocks, peerName);
    }

    @Override
    public String toString() {
        return "LeaveMessage{" +
                "order=" + getOrder() +
                ", sender=" + getPeerName() +
                ", vectorClocks=" + Arrays.toString(getVectorClocks()) +
                '}';
    }
}
