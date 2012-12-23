package pocketserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;
import pocketserver.packets.data.DisconnectPacket;
import pocketserver.packets.data.MessagePacket;
import pocketserver.packets.data.SetHealthPacket;

public class ServerCommands extends Thread {

    final PocketServer server;
    private static final Logger logger = Logger.getLogger("PocketServer");

    public ServerCommands(PocketServer pocketserver) {
	server = pocketserver;
    }

    public void run() {
	BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(System.in));
	String s = null;
	try {
	    while (!server.serverStopped && (s = bufferedreader.readLine()) != null) {
		handleCommand(s);
	    }
	} catch (IOException i) {
	    i.printStackTrace();
	}
    }

    private void handleCommand(String s) throws IOException {
	if (s.toLowerCase().startsWith("help") || s.toLowerCase().startsWith("?")) {
	    printHelp();
	} else if (s.toLowerCase().startsWith("stat")) {
	    printStats();
	} else if (s.toLowerCase().startsWith("stop")) {
	    shutdownServer();
	} else if (s.toLowerCase().startsWith("list")) {
	    playerList();
	} else if (s.toLowerCase().startsWith("say ")) {
	    String say = s.substring(s.indexOf(" ")).trim();
	    sendMessagePacket(say);
	} else if (s.toLowerCase().startsWith("suicide ")) {
	    String name = s.substring(s.indexOf(" ")).trim();
	    sendSuicidePacket(name);
	} else {
	    logger.info("Unknown command!");
	}
    }

    private void printHelp() {
	logger.info("Console commands: PocketServer v0.0.1");
	logger.info("	help or ?	    shows this message");
	logger.info("	list		    list with connected players");
	logger.info("	stat		    shows server status");
	logger.info("	say		    message to all players");
    }

    private void printStats() {
	logger.info((new StringBuilder()).append("Active threads: ").append(Thread.activeCount()).toString());
    }

    private void sendMessagePacket(String m) throws IOException {
	for (Player player : server.players) {
	    server.networkManager.sendPacket(new MessagePacket(m).sendPacket(player), player);
	}
    }

    //PlaceBlockPacket
    private void playerList() throws IOException {
	for (Player player : server.players) {
	    String pos = player.x + ", " + player.y + ", " + player.z;
	    logger.info((new StringBuilder()).append("[").append(player.entityID).append("] ").append(player.name).append(" ").append("Location: ").append(pos).toString());
	}
    }

    private void shutdownServer() {
	for (Player player : server.players) {
	    server.networkManager.sendPacket(new MessagePacket("Server Shutdown!").sendPacket(player), player);
	    server.networkManager.sendPacket(new DisconnectPacket().sendPacket(player), player);
	}
	server.initiateShutdown();
    }

    private void sendSuicidePacket(String name) {
        for (Player p : server.players) {
            if (p.name.equals(name)) {
                server.networkManager.sendPacket(new MessagePacket("Silence! I Kill You!").sendPacket(p), p);
                server.networkManager.sendPacket(new SetHealthPacket((byte)0).sendPacket(p), p);
            }
        }
    }
}
