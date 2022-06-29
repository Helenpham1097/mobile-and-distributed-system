package assign2.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IPeerSystem extends Remote {

    int registerPeer(String peer) throws RemoteException;

    void remove(String peer) throws RemoteException;

    List<String> getAllPeers() throws RemoteException;

    long nextLong() throws RemoteException;
}
