package assign2;

import assign2.rmi.IPeer;
import assign2.rmi.IPeerSystem;
import assign2.rmi.Peer;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Utils {

    public static final int PORT = 1888;

    public static IPeer createPeer(Registry registry, String peerName, String systemName) {
        String name = String.format("%s-%s", systemName, peerName);
        Peer peer = new Peer(peerName, systemName, registry);
        IPeer stub = null;
        try {
            stub = (IPeer) UnicastRemoteObject.exportObject(peer, 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        bind(registry, name, stub);
        return stub;
    }

    public static <T extends Remote> void bind(Registry registry, String name, T object) {
        try {
            registry.bind(name, object);
        } catch (AlreadyBoundException | RemoteException e) {
           e.printStackTrace();
        }
    }

    public static IPeerSystem getPeerSystem(Registry registry, String name) throws RemoteException, NotBoundException {
        return (IPeerSystem) registry.lookup(name);
    }
}
