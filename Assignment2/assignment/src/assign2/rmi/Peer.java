package assign2.rmi;



import assign2.message.*;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static assign2.Utils.getPeerSystem;

public class Peer implements IPeer {

    // Assume peer name is unique
    private final String name;

    private final String systemName;

    private int order;

    private int[] vectorClocks;

    // Structure for snapshot
    private boolean ownTaken;
    private final Set<String> neighbors = new HashSet<>();
    // make sure snapshot request can be requested after launch
    private Instant snapShotTakenTime = Instant.now().minus(20, ChronoUnit.SECONDS);
    private final List<String> neighborsSnapShotTaken = new ArrayList<>();
    private final Map<String, List<Message>> pendingMessages = new HashMap<>();

    // Fields for voting
    private String nextPeer;
    private boolean participant = false;
    private long ownId;
    private long leaderId = -1;

    private final List<String> logs = new ArrayList<>();
    private final Registry registry;

    public Peer(String name, String systemName, Registry registry) {
        this.name = String.format("%s-%s", systemName, name);
        this.systemName = systemName;
        this.registry = registry;
    }

    @Override
    public String getName() throws RemoteException {
        return name;
    }

    @Override
    public void join() throws RemoteException, NotBoundException {
        IPeerSystem system = getPeerSystem(registry, systemName);

        order = system.registerPeer(name);
        ownId = system.nextLong();
        vectorClocks = new int[order];
        Arrays.fill(vectorClocks, 0);

        List<String> allPeers = system.getAllPeers();

        // First peer
        if (order == 1) {
            logJoinTime();
            return;
        }

        //increase logic call lock to 1 before sending message
        increaseClock();

        logJoinTime();

        // update neighbors when joining the system for global snapshot
        updateNeighbors(system);

        // notify all peers except itself
        for (String peerName : allPeers) {
            if (!peerName.equals(name)) {
                notifyPeer(peerName, () -> new JoinMessage(order, copyVectorClock(), name));
            }
        }

        logState();
    }

    void logState() {
        logs.add(String.format(
                "State of peer %s is: ownId %s, leader %s vectorClocks %s and neighbors %s at time %s " +
                        "in the system %s%n",
                name,
                ownId,
                leaderId,
                Arrays.toString(vectorClocks),
                neighbors,
                now(),
                systemName));
    }

    void logJoinTime() {
        logs.add(String.format("Peer %s join system with vectorClocks: %s at time %s%n",
                name,
                Arrays.toString(vectorClocks),
                now()));
    }

    @Override
    public List<String> getLogs() {
        synchronized (logs) {
            List<String> flush = new ArrayList<>(logs);
            logs.clear();
            return flush;
        }
    }

    void increaseClock() {
        vectorClocks[order - 1] += 1;
    }

    int[] copyVectorClock() {
        int[] copyOfVectorClocks = new int[vectorClocks.length];
        System.arraycopy(vectorClocks, 0, copyOfVectorClocks, 0, vectorClocks.length);
        return copyOfVectorClocks;
    }

    void notifyPeer(
            String peerName,
            Supplier<? extends Message> message) throws NotBoundException {
        try {
            IPeer peer = (IPeer) registry.lookup(peerName);
            peer.process(message.get());
        } catch (RemoteException ex) {
            System.out.printf("Peer %s is not alive%n", peerName);
        }
    }

    @Override
    public void leave() throws RemoteException, NotBoundException {

        IPeerSystem system = getPeerSystem(registry, systemName);
        system.remove(name);
        // notify all peers except itself
        for (String peerName : system.getAllPeers()) {
            if (!peerName.equals(name)) {
                notifyPeer(peerName, () -> new LeaveMessage(order, copyVectorClock(), name));
            }
        }

        registry.unbind(name);
    }

    @Override
    public void process(Message message) throws RemoteException, NotBoundException {
        logs.add(String.format("Peer %s receives message %s at time %s%n", name, message, now()));

        IPeerSystem system = getPeerSystem(registry, systemName);

        updateVectorClock(message.getVectorClocks());

        if (message instanceof SnapShotMessage) {
            increaseClock();
            notifyPeer(
                    message.getPeerName(),
                    () -> new ResetStateMessage(order, copyVectorClock(), name));
            snapShotTakenTime = Instant.now();
            ownTaken = false;
        }

        if (message instanceof ResetStateMessage) {
            neighborsSnapShotTaken.clear();
            pendingMessages.clear();
            snapShotTakenTime = Instant.now();
            ownTaken = false;
        }

        /*
         * Implement of Chandy-Lamport algorithm
         */
        if (message instanceof MarkerMessage) {

            MarkerMessage markerMessage = (MarkerMessage) message;
            // update states of each neighbors
            if (!ownTaken && snapShotWasTakenForAWhile()) {
                takeSnapShot();
                ownTaken = true;

                // send marker message to each neighbors to notify done
                sendMarkerMessageToAllNeighbors(markerMessage.getInitializer(), true);
            }

            if (markerMessage.isSenderSnapshotDone()) {
                neighborsSnapShotTaken.add(markerMessage.getPeerName());
            }

            if (ownTaken && !neighborsStillNotFinishSnapshot()) {
                sendSnapShot(markerMessage.getInitializer());
            }

        } else {

            String sender = message.getPeerName();
            if (ownTaken && !neighborsSnapShotTaken.contains(sender)) {
                if (pendingMessages.containsKey(sender)) {
                    pendingMessages.get(sender).add(message);
                } else {
                    List<Message> messages = new ArrayList<>();
                    messages.add(message);
                    pendingMessages.putIfAbsent(sender, messages);
                }
            } else {

                if (message instanceof JoinMessage) {
                    logs.add(String.format("Peer %s joins system %s%n", message.getPeerName(), systemName));
                    updateNeighbors(system);
                }

                if (message instanceof LeaveMessage) {
                    logs.add(String.format("Peer %s leaves system %s%n", message.getPeerName(), systemName));
                    handleLeaveMessage((LeaveMessage) message);
                    updateNeighbors(system);
                }

                /*
                 * This start Change-Roberts algorithm
                 */
                if (message instanceof ElectionMessage) {
                    vote((ElectionMessage) message);
                }

                if (message instanceof LeaderElectionMessage) {
                    leaderId = ((LeaderElectionMessage) message).getMessageId();
                    participant = false;
                    if (leaderId != ownId) {
                        sendLeaderElectionMessageToNextPeer(leaderId);
                    }
                }

                /*
                 * END Change-Roberts algorithm
                 */
            }
        }
        logState();
    }

    private boolean snapShotWasTakenForAWhile() {
        // 20 seconds to accept snapshot request
        return Instant.now().minus(20, ChronoUnit.SECONDS).isAfter(snapShotTakenTime);
    }

    private void handleLeaveMessage(LeaveMessage message) {
        int indexToRemove = message.getOrder() - 1;
        while (indexToRemove < vectorClocks.length - 1) {
            vectorClocks[indexToRemove] = vectorClocks[indexToRemove + 1];
            indexToRemove++;
        }

        int[] temp = new int[vectorClocks.length - 1];
        System.arraycopy(vectorClocks, 0, temp, 0, temp.length);
        vectorClocks = temp;

        if(order > message.getOrder()) {
            order = order - 1;
        }
    }

    private void vote(ElectionMessage message) throws NotBoundException {
        long messageId = message.getMessageId();
        if (messageId > ownId) {
            participant = true;
            sendElectionMessageToNextPeer(messageId);
        } else if (messageId == ownId) {
            sendLeaderElectionMessageToNextPeer(ownId);
        } else {
            if (!participant) {
                participant = true;
                sendElectionMessageToNextPeer(ownId);
            }
        }
    }

    @Override
    public void selfNominate() throws RemoteException, NotBoundException {
        sendElectionMessageToNextPeer(ownId);
    }

    private void sendLeaderElectionMessageToNextPeer(long messageId) throws NotBoundException {
        increaseClock();
        notifyPeer(
                nextPeer,
                () -> new LeaderElectionMessage(order, copyVectorClock(), name, messageId));
    }

    private void sendElectionMessageToNextPeer(long messageId) throws NotBoundException {
        increaseClock();
        notifyPeer(
                nextPeer,
                () -> new ElectionMessage(order, copyVectorClock(), name, messageId));
    }

    @Override
    public void initializeSnapShot() throws RemoteException, NotBoundException {
        sendMarkerMessageToAllNeighbors(name, false);
    }

    private String now() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
                .withZone(ZoneId.from(ZoneOffset.ofHours(-12))).format(Instant.now());
    }

    private void sendMarkerMessageToAllNeighbors(
            String initializer,
            boolean isDone) throws NotBoundException {
        sendMarkerMessageToRecipients(
                initializer,
                isDone,
                neighbors);
    }

    private void sendMarkerMessageToRecipients(String initializer,
                                               boolean isDone,
                                               Set<String> recipients) throws NotBoundException {
        logs.add(String.format("Sending marker message to all receipts: %s at time: %s%n",
                recipients,
                now()));

        for (String peer : recipients) {
            if (!peer.equals(name)) {
                increaseClock();
                notifyPeer(
                        peer,
                        () -> new MarkerMessage(
                                order,
                                copyVectorClock(),
                                name, //sender
                                initializer,
                                isDone));
            }
        }
    }

    private void updateNeighbors(IPeerSystem system) throws RemoteException {
        List<String> peers = system.getAllPeers();

        if (peers.isEmpty()) {
            return;
        }

        if (peers.size() == 1) {
            neighbors.clear();
        }

        synchronized (neighbors) {
            neighbors.clear();
            // because this peer is at index order-1
            if (order - 2 >= 0) {
                neighbors.add(peers.get(order - 2));
            } else {
                neighbors.add(peers.get(peers.size() - 1));
            }

            if (order < peers.size()) {
                nextPeer = peers.get(order);
                neighbors.add(nextPeer);
            } else {
                //circular ring
                nextPeer = peers.get(0);
                neighbors.add(nextPeer);
            }
        }

        logs.add(String.format("Peer %s has neighbors: %s at time %s%n", name, neighbors, now()));
    }

    private void updateNextPeer(IPeerSystem system) throws RemoteException {
        List<String> peers = system.getAllPeers();

        if (peers.isEmpty()) {
            return;
        }

        if (peers.size() == 1) {
            nextPeer = null;
        }

        if (order < peers.size()) {
            nextPeer = peers.get(order);
        } else {
            //circular ring
            nextPeer = peers.get(0);
        }
    }

    private void sendSnapShot(String initializer) throws NotBoundException {
        if (!initializer.equals(name)) {
            logs.add(String.format("Peer %s send snap shot to %s at time %s%n", name, initializer, now()));
            increaseClock();
            notifyPeer(
                    initializer,
                    () -> new SnapShotMessage(order, copyVectorClock(), name, pendingMessages));
        }

        neighborsSnapShotTaken.clear();
        pendingMessages.clear();
    }

    private void updateVectorClock(int[] updateClocks) {
        if (updateClocks == null) {
            return;
        }

        int minLength = Math.min(vectorClocks.length, updateClocks.length);
        int maxLength = Math.max(vectorClocks.length, updateClocks.length);

        if (minLength != vectorClocks.length) {
            for (int i = 0; i < updateClocks.length; i++) {
                vectorClocks[i] = Math.max(vectorClocks[i], updateClocks[i]);
            }
        } else {
            int[] temp = new int[maxLength];
            for (int i = 0; i < minLength; i++) {
                temp[i] = Math.max(vectorClocks[i], updateClocks[i]);
            }
            System.arraycopy(updateClocks, minLength, temp, minLength, maxLength - minLength);
            vectorClocks = temp;
        }

        increaseClock();
    }

    void takeSnapShot() {
        System.out.printf("Peer %s take snapshot at time %s%n", name, now());
        logs.add(String.format("Peer %s take snapshot at time %s%n", name, now()));
        increaseClock();
    }

    boolean neighborsStillNotFinishSnapshot() {
        for (String neighbor : neighbors) {
            if (!neighborsSnapShotTaken.contains(neighbor)) {
                return true;
            }
        }
        return false;
    }
}
