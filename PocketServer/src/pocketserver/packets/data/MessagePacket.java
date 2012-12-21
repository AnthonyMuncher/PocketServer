package pocketserver.packets.data;

import java.nio.ByteBuffer;
import pocketserver.Hex;
import pocketserver.Player;
import pocketserver.packets.Packet;
import pocketserver.packets.PacketHandler;

public class MessagePacket extends Packet {

    private String message;

    public MessagePacket(String s) {
	message = s;
    }

    @Override
    public ByteBuffer getPacket() {
	int size = (message.length() + 3) * 8;
	ByteBuffer response = ByteBuffer.allocate(6 + message.length());
	response.put((byte) 0x00);
	response.putShort((short) size);

	response.put((byte) 0x85);
	response.putShort((short) message.length());
	response.put(message.getBytes());
	return response;
    }

    public ByteBuffer sendPacket(Player player) {

	ByteBuffer response = ByteBuffer.allocate(message.length() + 4 + 6);
	response.put((byte) 0x84);
	response.put(Hex.intToBytes(player.getPacketCount(), 3), 0, 3);

	int size = (message.length() + 3) * 8;
	response.put((byte) 0x00);
	response.putShort((short) size);

	response.put((byte) 0x85);
	response.putShort((short) message.length());
	response.put(message.getBytes());
	return response;
    }

    @Override
    public void process(PacketHandler h) {
	h.addToQueue(getPacket());
    }
}
