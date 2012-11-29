package pocketserver.packets;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;

class Packet84UnknownPacket {
    
    private long unknown;

    Packet84UnknownPacket(byte[] data) {
	ByteBuffer bb = ByteBuffer.wrap(data);
	unknown = bb.getLong();
    }

    public DatagramPacket getPacket() {
	ByteBuffer b = ByteBuffer.allocate(12);
        b.put((byte)0x00);
	b.put((byte)0x00);
	b.put((byte)0x48);
	b.put((byte)0x00);
	b.putLong(unknown);
        return new DatagramPacket(b.array(),12);
    }
    
    byte[] response() {
	return getPacket().getData();
    }  
    
}
