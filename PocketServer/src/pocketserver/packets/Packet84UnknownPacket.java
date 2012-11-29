package pocketserver.packets;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;

class Packet84UnknownPacket {
    
    private long unknown1;
    private long unknown2;
    private int i;

    Packet84UnknownPacket(byte[] data) {
	ByteBuffer bb = ByteBuffer.wrap(data);
	i = (bb.get() & 0xFF);
	unknown1 = bb.getLong();
	if (i != 0x00) {
	    unknown2 = bb.getLong();
	}
    }

    public DatagramPacket getPacket() {
	int size = 12;
	if (i == 0x03) { size += 8; }
	ByteBuffer b = ByteBuffer.allocate(size);
        b.put((byte)0x00);
	b.put((byte)0x00);
	if (i == 0x00) {
	    b.put((byte)0x48);
	    b.put((byte)0x00);
	    b.putLong(unknown1);  
	} else {
	    b.put((byte)0x88);
	    b.put((byte)0x00);
	    b.putLong(unknown1);
	    b.putLong(unknown2);
	}
        return new DatagramPacket(b.array(),size);
    }
    
    byte[] response() {
	return getPacket().getData();
    }  
    
}
