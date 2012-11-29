package pocketserver.packets;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import pocketserver.Hex;
import pocketserver.PacketHandler;

class Packet84LoginPacket {

    private short nameLength;
    private String name;
    private int unknown1;
    private int unknown2;
    
    Packet84LoginPacket(byte[] data) {
	ByteBuffer bb = ByteBuffer.wrap(data);
	nameLength = bb.getShort();
	name = Hex.bytesToString(bb,nameLength);
	bb.get(unknown1);
	bb.get(unknown2);
    }

    public DatagramPacket getPacket() {
	ByteBuffer b = ByteBuffer.allocate(54);
	// LoginStatusPacket
	b.put((byte)0x60);
	b.put((byte)0x00);
	b.put((byte)0x28);
	b.put(Hex.intToBytes(1, 3));
	b.put(Hex.intToBytes(1, 4));
	b.put((byte)0x83);
	b.putInt(0);
	
	// StartGamePacket
	b.put((byte)0x60);
	b.put((byte)0x00);b.put((byte)0xe8);
	b.put(Hex.intToBytes(2, 3));
	b.put(Hex.intToBytes(2, 4));
	b.put((byte)0x87);
	b.putInt(1353526199);	// seed
	b.putInt(0);
	b.putInt(0);
	b.putInt(948624);
	b.putFloat(128.5f);	// X
	b.putFloat(72.0f);	// Y
	b.putFloat(128.5f);	// Z
        return new DatagramPacket(b.array(),54);
    }
    
    byte[] response(PacketHandler handler) {
	handler.player.setUsername(name);
	return getPacket().getData();
    }
    
}
