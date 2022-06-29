package assign2.message;

import java.io.Serializable;
import java.util.Arrays;

public class Message implements Serializable {

    // peer order
    private int order;

    private int []vectorClocks;

    private String peerName;

    public Message(int order, int[] vectorClocks, String peerName) {
        this.order = order;
        this.vectorClocks = vectorClocks;
        this.peerName = peerName;
    }

    public String getPeerName() {
        return peerName;
    }

    public int getOrder() {
        return order;
    }

    public int[] getVectorClocks() {
        return vectorClocks;
    }

    @Override
    public String toString() {
        return "Message{" +
                "order=" + order +
                ", vectorClocks=" + Arrays.toString(vectorClocks) +
                '}';
    }
}
