package pocketserver.packets.data;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;

import pocketserver.packets.Packet;
import pocketserver.packets.PacketHandler;

public class AnimatePacket extends Packet {

    private int entityID;
    private byte animate;

    public AnimatePacket(DatagramPacket p) {
	ByteBuffer b = ByteBuffer.wrap(p.getData());
	b.get();
	animate = b.get();
	entityID = b.getInt();

    }

    @Override
    public ByteBuffer getPacket() {
	int size = 6 * 8;
	ByteBuffer response = ByteBuffer.allocate(3 + 6);
	response.put((byte) 0x00);
	response.putShort((short) size);

	response.put((byte) 0xa7);
	response.put(animate);
	response.putInt(entityID);
	return response;
    }

    @Override
    public void process(PacketHandler h) {
	h.addToQueueForAll(getPacket());
    }
}
