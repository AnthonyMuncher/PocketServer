package pocketserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;

/**
 *
 * @author Intyre
 */
public class DataPacket {

    private NetworkManager network;
    private CustomDataPacket customPacket;
    private DatagramPacket packet;
    private int encapsulationID;
    private short length;
    private int mcpeID;
    private byte[] data;
    private byte[] count = new byte[3];
    
    private byte[] unknown = new byte[4];
    private Player player;
    
    public DataPacket(NetworkManager networkManager, DatagramPacket datagramPacket, Player currentPlayer) {
	network = networkManager;
	packet = datagramPacket;
	player = currentPlayer;
	
	ByteBuffer b = ByteBuffer.wrap(packet.getData());
	encapsulationID = (b.get() & 0xFF);
	if (encapsulationID == 0x00) {
	    length = b.getShort();  
	    mcpeID = (b.get()& 0xFF);
	    data = new byte[(length/8)-1];
	} else if (encapsulationID == 0x40) {
	    length = b.getShort();  
	    b.get(count);
	    mcpeID = (b.get()& 0xFF);
	    data = new byte[(length/8)-1];
	    b.get(data);
	} else if (encapsulationID == 0x60) {
	    length = b.getShort();  
	    b.get(count);
	    mcpeID = (b.get()& 0xFF);
	    b.get(unknown);
	    data = new byte[(length/8)-1];
	    b.get(data);
	}
    }

    public void response() throws IOException {
	ByteBuffer b = ByteBuffer.wrap(data);
	ByteBuffer response = null;
	boolean all = false;
	if (encapsulationID == 0x00) {
	    if (mcpeID == 0x00) { 
		long unknown1 = b.getLong();
		
		response = ByteBuffer.allocate(16);
		response.put((byte)0x84);
		response.put(Hex.intToBytes(player.getPacketCount(), 3));
		response.put((byte)0x00);
		response.put((byte)0x00);response.put((byte)0x48);
		response.put((byte)0x00);
		response.putLong(unknown1);
		
	    }
	} else if (encapsulationID == 0x40) {
	    if (mcpeID == 0x09) {
		b.getLong();
		long unknown1 = b.getLong();

		// response
		response = ByteBuffer.allocate(110);
		response.put((byte)0x84);
		response.put(Hex.intToBytes(player.getPacketCount(), 3));
		response.put((byte)0x60);  // Encapsulation ID
		response.put((byte)0x03);response.put((byte)0x00); // size of packet
		response.put(Hex.intToBytes(player.getDataCount(), 3)); 
		response.put((byte)0x00); // MinecrafPE ID
		response.putInt(16);
		response.put((byte)0x04); response.put((byte)0x3f); response.put((byte)0x57); response.put((byte)0xfe);
		response.put((byte)0xcd);
		response.putShort((short)player.getPort());
		for(int i=0;i<10;i++) {
		    response.put(Hex.intToBytes(4, 3));
		    response.putInt(0xffffffff);
		}
		response.putShort((short)0);
		response.putLong(unknown1);
		response.putLong(1L);
	    } else if (mcpeID == 0x82) {
		short nameLength = b.getShort();
		String name = Hex.bytesToString(b,nameLength);
		int unknown1 = b.get();
		int unknown2 = b.get();
		player.setName(name);
		
		// response 
		response = ByteBuffer.allocate(19);
		response.put((byte)0x84);
		response.put(Hex.intToBytes(player.getPacketCount(), 3));
		response.put((byte)0x60);
		response.put((byte)0x00);
		response.put((byte)0x28);
		response.put(Hex.intToBytes(player.getDataCount(), 3));
		response.put(Hex.intToBytes(1, 4));
		response.put((byte)0x83);
		response.putInt(0);
	
	    } else if (mcpeID == 0x84) { //ready packet
		// TODO addplayerpacket + settimepacket
////		customPacket.addPlayerPacket(this,player,network);
//		String txt = "Test";
//		int size = (txt.length() + 3) * 8;
//		response = ByteBuffer.allocate(11);
//		response.put((byte)0x84);
//		response.put(Hex.intToBytes(player.getPacketCount(), 3));
//		response.put((byte)0x60);
//		response.putShort((short)40);
//		response.putShort((short)0);
//		response.putShort((short)0);
	    } else if (mcpeID == 0x94) {
//		String txt = "Stop moving!";
//		int size = (txt.length() + 3) * 8;
//		response = ByteBuffer.allocate(10+txt.length());
//		response.put((byte)0x84);
//		response.put(Hex.intToBytes(player.getPacketCount(), 3));
//		response.put((byte)0x00);
//		response.putShort((short)size);
//		response.put((byte)0x85);
//		response.putShort((short)txt.length());
//		response.put(txt.getBytes());
	    } else if (mcpeID == 0x96) {
		int entityID = b.getInt();
		int x = b.getInt();
		int z = b.getInt();
		byte y = b.get();
		System.out.println("RemoveBlockPacket: EntityID->"+entityID+" X->"+x+" Z->"+z+" Y->"+y);
	    } else if (mcpeID == 0x9f) {
		int entityID = b.getInt();
		short blockID = b.getShort();
		short metaData = b.getShort();
		System.out.println("PlayerEquipmentPacket: entityID->" + entityID + " blockID->" + (short)blockID + " metaData->" +(short)metaData);
	    } else if (mcpeID == 0xa1) {
		int x = b.getInt();
		int y = b.getInt();
		int z = b.getInt();
		int face = b.getInt();
		short blockID = b.getShort();
		byte metaData = b.get();
		int unknown = b.getInt();
		float unknown1 = b.getFloat();
		float unknown2 = b.getFloat();
		float unknown3 = b.getFloat();
		System.out.println("UseItem: blockID->"+blockID);
		//TODO: Doesnt work!
//		String txt = player.getName() + " placed a block!";
//		int size = 12 * 8;
//		response = ByteBuffer.allocate(19+txt.length()+6);
//		response.put((byte)0x84);
//		response.put(Hex.intToBytes(player.getPacketCount(), 3));
//		response.put((byte)0x00);
//		response.putShort((short)size);
//		response.put((byte)0x97);
//		response.putInt(x);
//		response.putInt(z);
//		response.put((byte)y);
//		response.put((byte)blockID);
//		response.put(metaData);
//		size = (txt.length() +3 ) *8;
//		response.put((byte)0x00);
//		response.putShort((short)size);
//		response.put((byte)0x85);
//		response.putShort((short)txt.length());
//		response.put(txt.getBytes());
//		all = true;
	    }
	} else if (encapsulationID == 0x60) {
	    if (mcpeID == 0x00) {
		// response
		response = ByteBuffer.allocate(43);
		response.put((byte)0x84);
		response.put(Hex.intToBytes(player.getPacketCount(), 3));
		response.put((byte)0x60);
		response.put((byte)0x00);response.put((byte)0xe8);
		response.put(Hex.intToBytes(2, 3));	// TODO: data packet count
		response.put(Hex.intToBytes(2, 4));
		response.put((byte)0x87);
		response.putInt(1341140356);	// seed
		response.putInt(0);
		response.putInt(1);		// Survival or Creative
		response.putInt(0);
		response.putFloat(128.0f);	// X
		response.putFloat(65.0f);	// Y
		response.putFloat(128.0f);	// Z
		player.setPosition(128.0f, 65.0f, 128.0f);
	    } else if (mcpeID == 0x01) {
		network.removePlayer(player);
	    } else {
		System.out.println(mcpeID + " dus");
	    }
	    
	}
	
	if (response != null) {
	    if (!all) {
		network.sendPacket(response,player);
	    } else {
		network.sendToAll(response);
	    }
	}
    }
}
