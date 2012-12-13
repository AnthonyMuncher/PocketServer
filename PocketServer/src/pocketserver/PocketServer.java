package pocketserver;

import java.net.SocketException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PocketServer implements Runnable {

    public static final Logger logger = Logger.getLogger("PocketServer");

    private boolean serverRunning;
    public boolean serverStopped;
    public NetworkManager networkManager;
    private String motd;
    private Long serverID;
    private Random rnd = new Random();
    
    private PocketServer() {
	serverRunning = true;
	serverStopped = false;
    }
    
    private void startServer() {
	motd = "MCCPP;Demo;Pocket";
	serverID = rnd.nextLong();
	
	ServerCommands serverCommands = new ServerCommands(this);
	serverCommands.setDaemon(true);
	serverCommands.start();
	
	ConsoleLogManager.init();
	
	logger.info("Starting PockerServer v0.0.1");
	try {
	    networkManager = new NetworkManager(this,19132);
	    networkManager.run();
	} catch (SocketException i) {
	    i.printStackTrace();
	}
    }
	
    public void run() {
	try { 
	    startServer();
	}catch(Exception e) {
	    serverRunning = false;
	}
	
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
	    }
	}
    }
    
    public static void main(String[] args) {
	try {
            PocketServer server = new PocketServer();
            server.run();
        } catch (Exception e) {
            logger.info("Failed to start PocketServer");
        }
    }

    String getMotd() {
	return motd;
    }
    
    Long getServerID() {
	return serverID;
    }
    
    static boolean isServerRunning(PocketServer server) {
	return server.serverRunning;
    }
    
    public void getConnectedList() {
	logger.info("Connected clients:");
	for (Player player : networkManager.players) {
	    logger.info((new StringBuilder()).append(player.getName().length() != 0 ? player.getName() : "Unknown").append("\t").append(player.getAddress()).append(":").append(player.getPort()).toString());
	}
    }
}
