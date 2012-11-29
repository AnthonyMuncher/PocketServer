package pocketserver.packets;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import pocketserver.PacketHandler;

public class Packet84FirstDataPacketResponse {
    private long unknown1;
    private byte[] unknown = new byte[85];
    private long unknown2;

    Packet84FirstDataPacketResponse(byte[] data) {
	ByteBuffer bb = ByteBuffer.wrap(data);
	bb.get(unknown);
	unknown1 = bb.getLong();
    }

    public DatagramPacket getPacket() {
    ByteBuffer b = ByteBuffer.allocate(12);
        b.put((byte)0x00);
	b.put((byte)0x00);
	b.put((byte)0x48);	// data size / 8
	b.put((byte)0x00);
	b.putLong(unknown1);
        return new DatagramPacket(b.array(),12);
    }
    
    byte[] response(PacketHandler handler) {
	handler.player.setClientID(unknown1);
	return getPacket().getData();
    }
    
}
