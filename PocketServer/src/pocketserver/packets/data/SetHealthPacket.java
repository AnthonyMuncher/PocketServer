package pocketserver.packets.data;

import java.nio.ByteBuffer;
import pocketserver.Hex;
import pocketserver.Player;
import pocketserver.packets.Packet;
import pocketserver.packets.PacketHandler;

public class SetHealthPacket extends Packet {

    private byte health;

    public SetHealthPacket(byte i) {
	health = i;
    }

    @Override
    public ByteBuffer getPacket() {
	return null;
    }

    public ByteBuffer sendPacket(Player player) {

	ByteBuffer response = ByteBuffer.allocate(4+5);
	response.put((byte) 0x84);
	response.put(Hex.intToBytes(player.getPacketCount(), 3), 0, 3);

	int size = 2 * 8;
	response.put((byte) 0x00);
	response.putShort((short) size);

	response.put((byte) 0xa5);
	response.put(health);
	return response;
    }

    @Override
    public void process(PacketHandler h) {
	h.addToQueue(getPacket());
    }
}
