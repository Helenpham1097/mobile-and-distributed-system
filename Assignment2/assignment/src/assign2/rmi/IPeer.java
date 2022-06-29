package assign2.rmi;


import assign2.message.Message;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IPeer extends Remote {

    String getName() throws RemoteException;

    void join() throws RemoteException, NotBoundException;

    void leave() throws RemoteException, NotBoundException;

    void process(Message message) throws RemoteException, NotBoundException;

    void selfNominate() throws RemoteException, NotBoundException;

    void initializeSnapShot() throws RemoteException, NotBoundException;

    List<String> getLogs()  throws RemoteException;
}
