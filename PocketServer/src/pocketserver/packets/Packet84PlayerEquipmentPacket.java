package pocketserver.packets;

import java.nio.ByteBuffer;
import pocketserver.PacketHandler;

class Packet84PlayerEquipmentPacket {
    private int entityID;
    private short itemID;
    private short itemData;

    Packet84PlayerEquipmentPacket(byte[] data) {
	ByteBuffer b = ByteBuffer.wrap(data);
	entityID = b.getInt();
	itemID = b.getShort();
	itemData = b.getShort();
    }

    byte[] response(PacketHandler handler) {
	//handler.player.setLocation(x,y,z,yaw,pitch);
	System.out.println(handler.player.getUsername() + " Changed item: ID: " + itemID + " Data: " + itemData);
	return null;
    }
    
}
