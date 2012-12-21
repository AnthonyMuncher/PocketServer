package pocketserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Random;
import pocketserver.packets.PacketHandler;

public class NetworkManager extends Thread {

    private boolean isListening;
    private PocketServer pocket;
    public DatagramSocket socket;
    private DatagramPacket packet;
    public long serverID;
    private Random rnd = new Random();
    public long start;

    public NetworkManager(PocketServer pocketServer, int i) throws SocketException {
	isListening = false;
	pocket = pocketServer;
	socket = new DatagramSocket(i);
	socket.getBroadcast();
	serverID = rnd.nextLong();
	isListening = true;
    }

    public void run() {
	start = System.currentTimeMillis();
	while (isListening) {
	    byte[] buffer = new byte[1536];
	    packet = new DatagramPacket(buffer, 1536);
	    int packetSize = 0;
	    try {
		socket.setSoTimeout(5000);
		socket.receive(packet);
		socket.setSoTimeout(0);
		packetSize = packet.getLength();
	    } catch (Exception e) {
		System.out.println("Nobody wants to play? :(");
	    }


	    if (packetSize > 0) {
//				System.out.println("Packet received! " + packet.getLength());
		ByteBuffer b = ByteBuffer.wrap(packet.getData());
		byte[] data = new byte[packet.getLength()];
		b.get(data);


		DatagramPacket pkt = new DatagramPacket(data, packetSize);
		pkt.setAddress(packet.getAddress());
		pkt.setPort(packet.getPort());
		new Thread(new PacketHandler(pocket, this, pkt)).start();
	    }
	}

    }

    public void sendPacket(ByteBuffer response, Player player) {
	DatagramPacket responsePacket = null;
	if (response != null) {
	    try {
		responsePacket = new DatagramPacket(response.array(), response.capacity());
		responsePacket.setAddress(player.clientAddress);
		responsePacket.setPort(player.clientPort);
		socket.send(responsePacket);
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }
}
