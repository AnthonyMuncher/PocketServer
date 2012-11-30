
package pocketserver.packets;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import pocketserver.Hex;

class Packet84ChunkRequestPacket {
    private int unknown1;
    private int unknown2;
	
    Packet84ChunkRequestPacket(byte[] data) {
	ByteBuffer bb = ByteBuffer.wrap(data);
	unknown1 = bb.getInt();
	unknown2 = bb.getInt();
    }

    public DatagramPacket getPacket() {
	ByteBuffer b = ByteBuffer.allocate(275);
	// LoginStatusPacket
	b.put((byte)0x60);
	b.put((byte)0x08);b.put((byte)0x48);
	b.put(Hex.intToBytes(1, 3));	// TODO: data packet count
	b.put(Hex.intToBytes(0, 4));
	b.put((byte)0x9e);
	b.putInt(unknown1);
	b.putInt(unknown2);
	for (int i = 0; i<256;i++){
	    b.put((byte)0x01);
	}	
        return new DatagramPacket(b.array(),275);
    }
    
    byte[] response() {
	return getPacket().getData();
    }    
}
