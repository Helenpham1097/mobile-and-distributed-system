package assign2.rmi;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PeerSystem implements IPeerSystem {

    private final List<String> peers;

    private final Random random = new Random();

    public PeerSystem() {
        peers = new ArrayList<>();
    }

    @Override
    public int registerPeer(String peer) throws RemoteException {
       synchronized (peers) {
           peers.add(peer);
           return peers.size();
       }
    }

    @Override
    public void remove(String peer) throws RemoteException {
        synchronized (peers) {
            peers.remove(peer);
        }
    }

    @Override
    public List<String> getAllPeers() throws RemoteException {
        return peers;
    }

    @Override
    public long nextLong() throws RemoteException {
        return random.nextLong();
    }
}
