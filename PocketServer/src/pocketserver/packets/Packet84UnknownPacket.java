package pocketserver.packets;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;

class Packet84UnknownPacket {
    
    private long unknown1;

    Packet84UnknownPacket(byte[] data) {
	ByteBuffer bb = ByteBuffer.wrap(data);
	unknown1 = bb.getLong();
    }

    public DatagramPacket getPacket() {
	int size = 12;
	ByteBuffer b = ByteBuffer.allocate(size);
        b.put((byte)0x00);
	b.put((byte)0x01);
	b.put((byte)0x48);
	b.put((byte)0x00);
	b.putLong(unknown1);  
        return new DatagramPacket(b.array(),size);
    }
    
    byte[] response() {
	return getPacket().getData();
    }  
    
}
