package assign2.ui;


import assign2.Utils;
import assign2.rmi.IPeer;
import assign2.rmi.IPeerSystem;
import assign2.rmi.PeerSystem;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static assign2.Utils.PORT;


public class PeerUI extends JFrame {

    private final JButton joinSystem = new JButton("Join");
    private final JButton leaveSystem = new JButton("Leave");
    private final JButton createDistributedSystem = new JButton("Create System");
    private final JButton selfElection = new JButton("Trigger election");
    private final JButton initializeSnapShot = new JButton("Trigger global snapshot");

    private final JTextArea messageArea = new JTextArea();

    private final DefaultListModel<String> distributedSystemListModel = new DefaultListModel<>();
    private final JList<String> distributeSystems = new JList<>(distributedSystemListModel);

    private String distributedSystemName;
    private final int DISTRIBUTED_SYSTEM_PORT = 1888;
    private Registry registry;
    private final Map<String, IPeer> peerInstances = new HashMap<>();

    private final Map<String, List<String>> logGroupBySystem = new HashMap<>();

    public PeerUI(String title) {
        super(title);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 400);
        setFramePosition();

        init();
    }

    private void setFramePosition() {

        Random r = new Random();
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(
                (int) Math.abs(r.nextLong()) % size.width,
                (int) Math.abs(r.nextLong()) % size.height);
    }

    private void init() {

        JPanel southPanel = new JPanel();

        JPanel southPanelPart1 = new JPanel();
        southPanelPart1.add(createDistributedSystem);
        southPanelPart1.add(joinSystem);
        southPanelPart1.add(leaveSystem);

        JPanel southPanelPart2 = new JPanel();
        southPanelPart2.add(selfElection);
        southPanelPart2.add(initializeSnapShot);

        southPanel.setLayout(new BorderLayout());
        southPanel.add(BorderLayout.CENTER, southPanelPart1);
        southPanel.add(BorderLayout.SOUTH, southPanelPart2);

        distributeSystems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        distributeSystems.setSelectedIndex(0);

        JScrollPane listScrollPane = new JScrollPane(distributeSystems);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                messageScrollPane,
                listScrollPane);
        splitPane.setDividerLocation(700);
        splitPane.setOneTouchExpandable(true);

        getContentPane().add(BorderLayout.SOUTH, southPanel);
        getContentPane().add(BorderLayout.CENTER, splitPane);

        try {
            initRegistry();
            registerCreatingDistributedSystemAction();
            registerJoinDistributedSystemAction();
            registerLeaveDistributedSystemAction();
            registerTriggerGlobalSnapshotAction();
            registerTriggerElectionAction();
            registerDistributedNamesSelectionAction();
            registerLogs();
        } catch (RemoteException | NotBoundException e) {
            throw new IllegalStateException(e);
        }
    }

    private void registerDistributedNamesSelectionAction() {
        distributeSystems.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                try {
                    displayLogs(distributeSystems.getSelectedValue());
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void displayLogs(String systemName) throws RemoteException {
        if (systemName != null && !systemName.isEmpty()) {
            IPeer peer = peerInstances.get(distributeSystems.getSelectedValue());
            messageArea.setText("");
            if (peer != null) {
                addLogs(systemName, peer.getLogs());
                for (String log : logGroupBySystem.getOrDefault(systemName, Collections.emptyList())) {
                    messageArea.append(log);
                }
            }
        }
    }

    private void registerLogs() {
        Timer timer = new Timer(3000, e -> {
            try {
                String selectedDistributedSystemName = distributeSystems.getSelectedValue();
                displayLogs(selectedDistributedSystemName);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        });

        timer.start();
    }

    private void addLogs(String system, List<String> logs) {
        if (logGroupBySystem.containsKey(system)) {
            logGroupBySystem.get(system).addAll(logs);
        } else {
            logGroupBySystem.putIfAbsent(system, logs);
        }
    }

    private boolean cannotDoAction() {

        String selectedDistributedSystemName = distributeSystems.getSelectedValue();

        if (!peerInstances.containsKey(selectedDistributedSystemName)) {
            JOptionPane.showMessageDialog(null,
                    String.format("Peer %s did not join system %s yet", getTitle(), selectedDistributedSystemName),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return true;
        }
        return false;
    }

    private void registerTriggerElectionAction() {
        selfElection.addActionListener(e -> {

            if (cannotDoAction()) {
                return;
            }

            IPeer peer = peerInstances.get(distributeSystems.getSelectedValue());

            try {
                peer.selfNominate();
            } catch (RemoteException | NotBoundException ex) {
                throw new RuntimeException("Cannot join system");
            }
        });
    }

    private void registerTriggerGlobalSnapshotAction() {
        initializeSnapShot.addActionListener(e -> {

            if (cannotDoAction()) {
                return;
            }

            IPeer peer = peerInstances.get(distributeSystems.getSelectedValue());

            try {
                peer.initializeSnapShot();
            } catch (RemoteException | NotBoundException ex) {
                throw new RuntimeException("Cannot join system");
            }
        });
    }

    private void registerLeaveDistributedSystemAction() {
        leaveSystem.addActionListener(e -> {

            if (cannotDoAction()) {
                return;
            }

            IPeer peer = peerInstances.get(distributeSystems.getSelectedValue());

            try {
                peer.leave();
                peerInstances.remove(distributeSystems.getSelectedValue());
            } catch (RemoteException | NotBoundException ex) {
                throw new RuntimeException("Cannot join system");
            }
        });
    }

    private void registerJoinDistributedSystemAction() throws NotBoundException, RemoteException {

        joinSystem.addActionListener(e -> {
            IPeer peer = Utils.createPeer(registry, getTitle(), distributeSystems.getSelectedValue());
            peerInstances.put(distributeSystems.getSelectedValue(), peer);

            try {
                peer.join();
            } catch (RemoteException | NotBoundException ex) {
                throw new RuntimeException("Cannot join system");
            }
        });
    }

    private void initRegistry() throws RemoteException {
        try {
            registry = LocateRegistry.createRegistry(PORT);
        } catch (RemoteException e) {
            registry = LocateRegistry.getRegistry(PORT);
        }

        Timer timer = new Timer(1000, e -> {
            try {
                Arrays.stream(registry.list())
                        .filter(name -> name.endsWith("System"))
                        .forEach(name -> {
                            if (!distributedSystemListModel.contains(name)) {
                                distributedSystemListModel.addElement(name);
                            }
                        });
                if(distributedSystemListModel.size() == 1) {
                    distributeSystems.setSelectedIndex(0);
                }
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        });

        timer.start();
    }

    private void registerCreatingDistributedSystemAction() {

        distributedSystemName = String.format("%sSystem", getTitle());

        createDistributedSystem.addActionListener(e -> {
            try {
                showOptionPane();
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void showOptionPane() throws RemoteException {
        JTextField name = new JTextField(10);
        name.setText(distributedSystemName);

        JTextField port = new JTextField(4);
        port.setEditable(false);
        port.setText(String.valueOf(DISTRIBUTED_SYSTEM_PORT));

        JPanel distributedSystemInputPanel = new JPanel();
        distributedSystemInputPanel.add(new JLabel("Name:"));
        distributedSystemInputPanel.add(name);
        distributedSystemInputPanel.add(Box.createHorizontalStrut(15)); // a spacer
        distributedSystemInputPanel.add(new JLabel("Port:"));
        distributedSystemInputPanel.add(port);

        int result = JOptionPane.showConfirmDialog(
                null,
                distributedSystemInputPanel,
                String.format("The new system will be created as name %s and port %s", getTitle(), port),
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            createDistributedSystem();
        }
    }

    private void createDistributedSystem() throws RemoteException {

        PeerSystem system = new PeerSystem();
        IPeerSystem stub = (IPeerSystem) UnicastRemoteObject.exportObject(system, 0);

        try {
            registry.bind(distributedSystemName, stub);
        } catch (AlreadyBoundException e) {
            JOptionPane.showMessageDialog(null,
                    String.format("Distributed system with name %s already existed", distributedSystemName),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        distributedSystemListModel.addElement(distributedSystemName);
        createDistributedSystem.setEnabled(false);
    }

    public static void main(String[] args) {
        PeerUI ui = new PeerUI(args[0]);
        ui.setVisible(true);
    }
}
