package assign2.message;

import java.util.Arrays;

public class JoinMessage extends Message{

    public JoinMessage(int order, int[] vectorClocks, String peerName) {
        super(order, vectorClocks, peerName);
    }

    @Override
    public String toString() {
        return "JoinMessage{" +
                "order=" + getOrder() +
                ", sender=" + getPeerName() +
                ", vectorClocks=" + Arrays.toString(getVectorClocks()) +
                '}';
    }
}
