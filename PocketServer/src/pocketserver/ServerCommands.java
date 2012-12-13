package pocketserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

/**
 *
 * @author Intyre
 */
public class ServerCommands extends Thread {
    final PocketServer server;
    
    private static final Logger logger = Logger.getLogger("PocketServer");
    private CustomDataPacket customDataPacket = new CustomDataPacket();
    
    public ServerCommands(PocketServer pocketserver) {
	server = pocketserver;
    }
    
    public void run() {
	BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(System.in));
	String s = null;
	try {
	    while(!server.serverStopped && PocketServer.isServerRunning(server) && (s = bufferedreader.readLine()) != null) {
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
	} else if (s.toLowerCase().startsWith("list")) {
	    server.getConnectedList();
	} else if (s.toLowerCase().startsWith("say ")) {
	    String say = s.substring(s.indexOf(" ")).trim();
	    sendMessagePacket(say,null);
	} else if (s.toLowerCase().startsWith("tell ")) {
	    String tell[] = s.split(" ");
	    if (tell.length >= 3) {
		s = s.substring(s.indexOf(" ")).trim();
		s = s.substring(s.indexOf(" ")).trim();
		sendMessagePacket(s,tell[1]);
	    }
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
	logger.info("	tell <name> <msg>   message to players");
    }

    private void printStats() {
	logger.info((new StringBuilder()).append("Active threads: ").append(Thread.activeCount()).toString());
    }
    
    private void sendMessagePacket(String m, String n) throws IOException {
	for (Player player : server.networkManager.players) {
	    if (n == null) {
		server.networkManager.sendPacket(customDataPacket.messagePacket(m, player),player);
	    } else {
		if (player.getName().equalsIgnoreCase(n)) {
		    server.networkManager.sendPacket(customDataPacket.chatPacket(m, player),player);
		}
	    }
	}
    }
}
