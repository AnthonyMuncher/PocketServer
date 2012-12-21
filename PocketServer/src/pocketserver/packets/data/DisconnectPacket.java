package pocketserver.packets.data;

import java.nio.ByteBuffer;
import pocketserver.Hex;
import pocketserver.Player;
import pocketserver.packets.Packet;
import pocketserver.packets.PacketHandler;

public class DisconnectPacket extends Packet {

    public DisconnectPacket() {
    }

    @Override
    public ByteBuffer getPacket() {

	return null;
    }

    public ByteBuffer sendPacket(Player player) {

	ByteBuffer response = ByteBuffer.allocate(8);
	response.put((byte) 0x84);
	response.put(Hex.intToBytes(player.getPacketCount(), 3), 0, 3);

	int size = 1 * 8;
	response.put((byte) 0x00);
	response.putShort((short) size);

	response.put((byte) 0x15);
	return response;
    }

    @Override
    public void process(PacketHandler h) {
//		h.addToQueue(getPacket());
    }
}
