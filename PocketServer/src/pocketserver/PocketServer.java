package pocketserver;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PocketServer implements Runnable {

    private static Logger logger = Logger.getLogger("PocketServer");
    public NetworkManager networkManager;
    private boolean serverRunning;
    public boolean serverStopped;
    public ArrayList<Player> players;
    public HashMap<Integer, Long> entityIDList = new HashMap<Integer, Long>();
    private int connectedPlayers;

    public PocketServer() {
	serverRunning = true;
	serverStopped = false;
	connectedPlayers = 0;
	players = new ArrayList<Player>();
    }

    private boolean startServer() {
	ServerCommands serverCommands = new ServerCommands(this);
	serverCommands.setDaemon(true);
	serverCommands.start();

	ConsoleLogManager.init();

	try {
	    networkManager = new NetworkManager(this, 19132);
	    networkManager.start();
	} catch (SocketException s) {
	    s.printStackTrace();
	}
	return true;
    }

    public void initiateShutdown() {
	serverRunning = false;
	System.exit(0);
    }

    public void run() {
	if (startServer()) {
	    long ticks = 0;
	    while (serverRunning) {
		try {
		    Thread.sleep(100);
		} catch (InterruptedException ex) {
		    logger.log(Level.SEVERE, null, ex);
		}
		ticks++;
		if (ticks % 100 == 0) {
		    Runtime.getRuntime().gc();
		    logger.info("Connected players: " + players.size());
		}
	    }
	}
    }

    public static void main(String[] args) {
	try {
	    PocketServer server = new PocketServer();
	    server.run();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void addPlayer(InetAddress i, int p, Long cid) {
	if (currentPlayer(i, p) == null) {
	    boolean b = false;
	    int entityID = 1009;
	    while (!b) {
		entityID = 1000 + (int) (Math.random() * 1050);
		if (!entityIDList.containsKey(entityID)) {
		    entityIDList.put(entityID, cid);
		    b = true;
		}
	    }
	    players.add(new Player(i, p, entityID, cid));
	    connectedPlayers++;
	    logger.info("Connected players: " + players.size());
	}
    }

    public void removePlayer(InetAddress i, int p) {
	for (int j = 0; j < players.size(); j++) {
	    Player player = players.get(j);
	    if (player.clientAddress.equals(i) && player.clientPort == p) {
		entityIDList.values().remove(player.clientID);
		players.remove(j);
		connectedPlayers--;
		break;
	    }
	}
	System.out.println("Connected players: " + players.size() + " " + entityIDList.size());
    }

    public Player currentPlayer(InetAddress i, int p) {
	for (Player player : players) {
	    if (player.clientAddress.equals(i) && player.clientPort == p) {
		return player;
	    }
	}
	return null;
    }

    public Integer addEntityID(Long cid) {
	boolean b = false;
	int newID = 0;
	while (!b) {
	    newID = 1000 + (int) (Math.random() * 1050);
	    if (!entityIDList.containsKey(newID)) {
		entityIDList.put(newID, cid);
		b = true;
	    }
	}
	return newID;
    }
}
