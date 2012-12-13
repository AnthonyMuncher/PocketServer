package pocketserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

public class NetworkManager implements Runnable {

    public static final Logger logger = Logger.getLogger("PocketServer");
    private PocketServer pocket;
    private boolean isListening;
    private DatagramSocket socket;
    private DatagramPacket packet;
    private DatagramPacket responsePacket;
    public ArrayList<Player> players = new ArrayList();
    
    
    public NetworkManager(PocketServer aThis, int i) throws SocketException {
	isListening = false;
	pocket = aThis;
	socket = new DatagramSocket(i);
	socket.getBroadcast();
	isListening = true;
	
    }

    public void run() {
	logger.info("Listening");
	while(isListening) {
	    byte[] buffer = new byte[1536];
	    packet = new DatagramPacket(buffer,1536);
	    int size = 0;
	    try {
		socket.setSoTimeout(5000);
		socket.receive(packet);
		socket.setSoTimeout(0);
		size = packet.getData().length;
	    } catch (Exception e) {
		logger.info("Nobody wants to play? :(");
	    }

	    if (size > 0) {
		new Thread(new PacketHandler(pocket,this,packet)).start();
	    }
	}
    }



    void addPlayer(InetAddress address, int port, long cid) {
	logger.info("addPlayer!");
	players.add(new Player(address,port,cid));
    }
    
    public void removePlayer(Player player) {
	for (Iterator it = players.iterator(); it.hasNext();) {
	    Player p = (Player)it.next();
	    if (p.getPort() == player.getPort() && p.getAddress().equals(player.getAddress())){
		logger.info("removePlayer");
		it.remove();
		break;
	    }
	}
    }

        
    public Player getCurrentPlayer(InetAddress ip, int port) {
	Player p = null;
	for (Player player : players) {
	    if (player.getAddress().equals(ip) && player.getPort() == port) {
		p = player;
	    }
	}
	return p;
    }

    public void sendPacket(ByteBuffer response, InetAddress ip, int port) {
	responsePacket = null;
	if (response != null) {
	    try {
		responsePacket = new DatagramPacket(response.array(),response.capacity());
		responsePacket.setAddress(ip);
		responsePacket.setPort(port);
		socket.send(responsePacket);
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }
    
    public void sendPacket(ByteBuffer response, Player player) {
	responsePacket = null;
	if (response != null) {
	    try {
		responsePacket = new DatagramPacket(response.array(),response.capacity());
		responsePacket.setAddress(player.getAddress());
		responsePacket.setPort(player.getPort());
		socket.send(responsePacket);
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }

    public void sendToAll(ByteBuffer response) {
	if (response != null) {
	    for (Player player : players) {
		try {
		    responsePacket = new DatagramPacket(response.array(),response.capacity());
		    responsePacket.setAddress(player.getAddress());
		    responsePacket.setPort(player.getPort());
		    socket.send(responsePacket);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}
    }
}
