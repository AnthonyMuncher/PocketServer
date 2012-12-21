package pocketserver.packets;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;
import pocketserver.Hex;
import pocketserver.NetworkManager;
import pocketserver.Player;
import pocketserver.PocketServer;
import pocketserver.packets.data.AnimatePacket;
import pocketserver.packets.data.CurrentPlayersPacket;
import pocketserver.packets.data.Data00;
import pocketserver.packets.data.Data09;
import pocketserver.packets.data.Data82;
import pocketserver.packets.data.MessagePacket;
import pocketserver.packets.data.MovePlayerPacket;
import pocketserver.packets.data.PlayerEquipmentPacket;
import pocketserver.packets.data.RemoveBlockPacket;
import pocketserver.packets.data.RemovePlayerPacket;
import pocketserver.packets.data.RequestChunkPacket;
import pocketserver.packets.data.StartGamePacket;
import pocketserver.packets.data.UseItemPacket;
import pocketserver.packets.login.Login02;
import pocketserver.packets.login.Login05;
import pocketserver.packets.login.Login07;

public class PacketHandler implements Runnable {

    public PocketServer server;
    private NetworkManager network;
    private DatagramPacket packet;
    private InetAddress clientAddress;
    private int clientPort;
    private Queue<DatagramPacket> splitPackets = new LinkedList();
    private Queue<ByteBuffer> queuePackets = new LinkedList<>();
    private Queue<ByteBuffer> queuePacketsToAll = new LinkedList<>();
    private int queueDataSize;
    private int queueDataSizeToAll;

    public PacketHandler(PocketServer pocket, NetworkManager networkManager, DatagramPacket packet) {
	server = pocket;
	network = networkManager;
	this.packet = packet;
	clientAddress = packet.getAddress();
	clientPort = packet.getPort();
    }

    public void run() {
	if (packet != null) {
	    int packetType = (packet.getData()[0] & 0xFF);
	    int packetSize = packet.getData().length;
//			System.out.println("Packet from: " +clientAddress + ":" +clientPort + " PacketType: " + Integer.toHexString(packetType));

	    switch (packetType) {
		case 0x02:
		case 0x05:
		case 0x07:
//					System.out.println("LoginPacket from: " +clientAddress + ":" +clientPort + " PacketType: " + Integer.toHexString(packetType) + " Size-> " + packetSize);
		    LoginHandle();
		    break;
		case 0xa0:
		    System.out.println("NACK from: " + clientAddress + ":" + clientPort + " PacketType: " + Integer.toHexString(packetType) + " Size-> " + packetSize);
		    break;
		case 0xc0:
//					System.out.println("ACK from: " +clientAddress + ":" +clientPort + " PacketType: " + Integer.toHexString(packetType) + " Size-> " + packetSize);
		    break;
		case 0x84:
		case 0x85:
		case 0x86:
		case 0x87:
		case 0x88:
		case 0x89:
		case 0x8a:
		case 0x8b:
		case 0x8c:
		case 0x8d:
		case 0x8e:
		case 0x8f:
//					System.out.println("DataPacket from: " +clientAddress + ":" +clientPort + " PacketType: " + Integer.toHexString(packetType) + " Size-> " + packetSize);
		    DataHandle();
		    break;
		default:
		    System.out.println("Unknown packet from: " + clientAddress + ":" + clientPort + " PacketType: " + Integer.toHexString(packetType) + " Size-> " + packetSize);
		    break;
	    }
	}
    }

    private void LoginHandle() {
	int packetType = (packet.getData()[0] & 0xFF);

	Packet pkt = null;

	switch (packetType) {
	    case 0x02:
		pkt = new Login02(packet, network.serverID);
		break;
	    case 0x05:
		pkt = new Login05(packet, network.serverID);
		break;
	    case 0x07:
		pkt = new Login07(packet, network.serverID, server);
		break;
	    default:
		break;
	}

	if (pkt != null) {
	    pkt.process(this);
	}
    }

    private void DataHandle() {

	splitDataPacket();
	Player player = currentPlayer();
	while (splitPackets.size() > 0) {
	    DatagramPacket p = splitPackets.poll();

	    int mcpeID = (p.getData()[0] & 0xFF);
	    Packet pkt = null;

	    switch (mcpeID) {
		case 0x00:
		    pkt = new Data00(network.start, player);
		    break;
		case 0x03:
		    break;
		case 0x09:
		    pkt = new Data09(p, player, network.serverID);
		    break;
		case 0x13:
		    pkt = new StartGamePacket(player);
		    break;
		case 0x15:
		    pkt = new RemovePlayerPacket(player);
		    server.removePlayer(packet.getAddress(), packet.getPort());
		    break;
		case 0x82: // LoginPacket
		    pkt = new Data82(p, player);
		    break;
		case 0x84:
		    if (!player.isConnected) {
			pkt = new MessagePacket("Hello, world!");
			player.isConnected = true;
			if (server.players.size() > 0) {
			    new CurrentPlayersPacket(server.players, player).process(this);
			}
		    }
		    break;
		case 0x94:
		    pkt = new MovePlayerPacket(p, player);
		    break;
		case 0x96:
		    pkt = new RemoveBlockPacket(p);
		    break;
		case 0x9d:
		    pkt = new RequestChunkPacket(p);
		    break;
		case 0x9f:
		    pkt = new PlayerEquipmentPacket(p, player);
		    break;
		case 0xa0:
		    pkt = new MessagePacket("Die DIe DIE!!!!");
		    break;
		case 0xa1:
		    System.out.println((new StringBuilder()).append("UseItemPacket: ").append(Integer.toHexString(mcpeID)).append(" -> ").append(Hex.getHexString(p.getData(), true)).append(" Size: ").append(p.getLength()).toString());
		    pkt = new UseItemPacket(p, player);
		    break;
		case 0xa7:
		    pkt = new AnimatePacket(p);
		    break;
		default:
		    System.out.println((new StringBuilder()).append("unknown: ").append(Integer.toHexString(mcpeID)).append(" -> ").append(Hex.getHexString(p.getData(), true)).append(" Size: ").append(p.getLength()).toString());
		    break;
	    }

	    if (pkt != null) {
		pkt.process(this);
	    }
	}

	if (queuePackets.size() > 0) {
	    ByteBuffer b1 = ByteBuffer.allocate(queueDataSize + 4);
	    b1.put((byte) 0x84);
	    b1.put(Hex.intToBytes(player.getPacketCount(), 3), 0, 3);
	    while (queuePackets.size() > 0) {
		b1.put(queuePackets.poll().array());
	    }
//			System.out.println("Send: " + Hex.getHexString(b1.array(), true));
	    sendPacket(b1);
	}

	if (queuePacketsToAll.size() > 0) {
//			System.out.println("Send: " + Hex.getHexString(b1.array(), true));
	    for (Player p : server.players) {
		if (!p.clientAddress.equals(clientAddress) && p.clientPort != clientPort) {
		    sendPacketToAll(queuePacketsToAll, p);
		}
	    }
	    queuePacketsToAll.clear();
	}

    }

    private Player currentPlayer() {
	for (Player p : server.players) {
	    if (p.clientAddress.equals(clientAddress) && p.clientPort == clientPort) {
		return p;
	    }
	}
	return null;
    }

    private void splitDataPacket() {
	ByteBuffer b = ByteBuffer.wrap(packet.getData());
	b.get();
	byte[] count = new byte[3];
	b.get(count); // TODO: Send ack 
	sendACK(count);

	int len = packet.getLength() - 4;
	byte[] buffer = new byte[len];
	b.get(buffer);

	ByteBuffer data = ByteBuffer.wrap(buffer);
	int i = 0;
	int length = 0;
	while (i < buffer.length) {
	    if (buffer[i] == 0x00) {
		length = (data.getShort(i + 1) / 8);// + 3;
		i += 3;
	    } else if (buffer[i] == 0x40) {
		length = (data.getShort(i + 1) / 8);// + 6;
		i += 6;
	    } else if (buffer[i] == 0x60) {
		length = (data.getShort(i + 1) / 8);// + 10;
		i += 10;
	    }
	    data.position(i);
	    byte[] c = new byte[length];
	    data.get(c);
//			System.out.println((new StringBuilder()).append("split: ").append(Hex.getHexString(buffer[i])).append(" -> ").append(Hex.getHexString(c, true)).append(" Size: ").append(length).toString());
	    DatagramPacket split = new DatagramPacket(c, c.length);
	    splitPackets.add(split);
	    i += length;
	}
    }

    public void sendPacket(ByteBuffer d) {
//		System.out.println("Send: " + Hex.getHexString(d.array(), true));
	DatagramPacket p = new DatagramPacket(d.array(), d.capacity());
	p.setAddress(clientAddress);
	p.setPort(clientPort);
	try {
	    network.socket.send(p);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public void sendPacketToAll(Queue<ByteBuffer> q, Player player) {
	ByteBuffer b1 = ByteBuffer.allocate(queueDataSizeToAll + 4);
	b1.put((byte) 0x84);
	b1.put(Hex.intToBytes(player.getPacketCount(), 3), 0, 3);

	java.util.Iterator<ByteBuffer> it = q.iterator();
	while (it.hasNext()) {
	    b1.put(it.next().array());
	}

//		System.out.println("PacketToAll: " + Hex.getHexString(b1.array(), true));

	DatagramPacket p = new DatagramPacket(b1.array(), b1.capacity());
	p.setAddress(player.clientAddress);
	p.setPort(player.clientPort);
	try {
	    network.socket.send(p);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    private void sendACK(byte[] count) {
	ByteBuffer r = ByteBuffer.allocate(7);
	r.put((byte) 0xc0);
	r.putShort((short) 1);
	r.put((byte) 0x01);
	r.put(count);

	DatagramPacket p = new DatagramPacket(r.array(), r.capacity());
	p.setAddress(clientAddress);
	p.setPort(clientPort);
	try {
	    network.socket.send(p);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public void addToQueue(ByteBuffer b) {
	queuePackets.add(b);
	queueDataSize += b.capacity();
    }

    public void addToQueueForAll(ByteBuffer b) {
	queuePacketsToAll.add(b);
	queueDataSizeToAll += b.capacity();
    }
}
