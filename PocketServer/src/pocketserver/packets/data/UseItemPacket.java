package pocketserver.packets.data;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import pocketserver.Player;
import pocketserver.packets.Packet;
import pocketserver.packets.PacketHandler;

public class UseItemPacket extends Packet {

    private int entityID;
    private int x, y, z;
    private byte blockData;
    private short blockID;
    private int unknown;
    private float fx, fy, fz;
    private Player player;

    public UseItemPacket(DatagramPacket p, Player player) {
	ByteBuffer b = ByteBuffer.wrap(p.getData());
	b.get();
	x = b.getInt();
	y = b.getInt();
	z = b.getInt();
	unknown = b.getInt();
	blockID = b.getShort();
	blockData = b.get();
	b.getInt();
	fx = b.getFloat();
	fy = b.getFloat();
	fz = b.getFloat();
	this.player = player;
    }

    @Override
    public ByteBuffer getPacket() {
	int size = 12 * 8;
	ByteBuffer response = ByteBuffer.allocate(3 + 12);
	response.put((byte) 0x00);
	response.putShort((short) size);
	response.put((byte) 0x97);
	response.putInt(getX());
	response.putInt(getZ());
	response.put((byte) getY());
	response.put((byte) blockID);
	response.put((byte) blockData);
	return response;
    }

    private int getX() {
	int r = x;
	if (fx == 1.0f) {
	    r += 1;
	} else if (fx == 0.0f) {
	    r -= 1;
	}
	return r;
    }

    private int getY() {
	int r = y;
	if (fy == 1.0f) {
	    r += 1;
	} else if (fy == 0.0f) {
	    r -= 1;
	}
	return r;
    }

    private int getZ() {
	int r = z;
	if (fz == 1.0f) {
	    r += 1;
	} else if (fz == 0.0f) {
	    r -= 1;
	}
	return r;
    }

    @Override
    public void process(PacketHandler h) {
//		if(player.x != getX() && player.y != getY() && player.z != getZ()) {
	h.addToQueueForAll(getPacket());
//		}
    }
}
