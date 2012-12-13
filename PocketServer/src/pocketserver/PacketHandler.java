package pocketserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

class PacketHandler implements Runnable {

    private PocketServer server;
    public NetworkManager networkManager;
    private DatagramPacket packet;
    private Queue customPackets = new LinkedList();
    private static final Logger logger = Logger.getLogger("PocketServer");
    
    public PacketHandler(PocketServer pocket, NetworkManager aThis, DatagramPacket p) {
	server = pocket;
	networkManager = aThis;
	packet = p;
    }

    public void run() {
	if (packet != null) {
	    ByteBuffer b = ByteBuffer.wrap(packet.getData());
	    int packetType = (b.get() & 0xFF);
	    
	    byte[] magic = new byte[16];
	    byte[] cookie = new byte[4];
	    
	    ByteBuffer response = null;
	    switch(packetType) {
		case 0x02:
		    long clientID = b.getLong();
		    b.get(magic);
		    
		    // response
		    String motd = server.getMotd();
		    response = ByteBuffer.allocate(35+motd.length());
		    response.put((byte)0x1c);
		    response.putLong(clientID);
		    response.putLong(server.getServerID());
		    response.put(magic);
		    response.putShort((short)motd.length());
		    response.put(motd.getBytes());
		    break;
		case 0x05:
		    b.get(magic);
		    short mtuSize = (short)packet.getLength();
		    
		    //response
		    response = ByteBuffer.allocate(28);
		    response.put((byte)0x06);
		    response.put(magic);
		    response.putLong(server.getServerID());
		    response.put((byte)0x00);
		    response.putShort(mtuSize);
		    break;
		case 0x07:
		    b.get(magic);
		    b.get(cookie);
		    b.get();
		    mtuSize = b.getShort();
		    long cID = b.getLong();
		    short clientPort = (short)packet.getPort();
		    
		    //response
		    response = ByteBuffer.allocate(35);
		    response.put((byte)0x08);
		    response.put(magic);
		    response.putLong(server.getServerID());
		    response.put(cookie);
		    response.put((byte)0xcd);
		    response.putShort(clientPort);
		    response.putShort((short)mtuSize);
		    response.put((byte)0x00);
		    
		    networkManager.addPlayer(packet.getAddress(), packet.getPort(),cID);
		    break;
		case 0x84:
		    byte[] count = new byte[3];
		    b.get(count);
		    sendACK(count);
		    
		    int len = packet.getLength()-4;
		    byte[] buffer = new byte[len];
		    b.get(buffer);
		    
		    ByteBuffer data = ByteBuffer.wrap(buffer);
		    int i = 0;
		    int length = 0;
		    while (i < buffer.length) {
			if (buffer[i] == 0x00) {
			    length = (data.getShort(i+1) / 8) + 3;
			} else if (buffer[i] == 0x40) {
			    length = (data.getShort(i+1) / 8) + 6;
			} else if (buffer[i] == 0x60) {
			    length = (data.getShort(i+1) / 8) + 10;
			} 
			data.position(i);
			byte[] c = new byte[length];
			data.get(c);
			logger.fine((new StringBuilder()).append("split: ").append(Hex.getHexString(c, true)).append(" Size: ").append(length).toString());
			DatagramPacket split = new DatagramPacket(c, c.length);
			customPackets.add(split);
			i += length;
		    }
		    
		    DataPacket res;
		    while(!customPackets.isEmpty()){
			try {
			    res = new DataPacket(networkManager,(DatagramPacket)customPackets.poll(),networkManager.getCurrentPlayer(packet.getAddress(), packet.getPort()));
			    res.response();
			} catch (IOException ex) {
			    Logger.getLogger(PacketHandler.class.getName()).log(Level.SEVERE, null, ex);
			}
		    }
		    
		    break;
		case 0xc0:
		    break;
		default:
		    logger.info((new StringBuilder()).append("Unknown packet: ").append(packetType).toString());
		    break;
	    }
	    
	    if (response != null) {
		networkManager.sendPacket(response,packet.getAddress(),packet.getPort());
	    }
	    
	}
    }

    private void sendACK(byte[] count) {
	ByteBuffer response = ByteBuffer.allocate(7);
	response.put((byte)0xc0);
	response.putShort((short)1);
	response.put((byte)0x01);
	response.put(count);
	networkManager.sendPacket(response,packet.getAddress(),packet.getPort());
    }

}
