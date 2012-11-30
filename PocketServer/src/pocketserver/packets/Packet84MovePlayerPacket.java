package pocketserver.packets;

import java.nio.ByteBuffer;
import pocketserver.PacketHandler;

public class Packet84MovePlayerPacket {
    private int entityID;
    private float x;
    private float y;
    private float z;
    private float pitch;
    private float yaw;
    
    Packet84MovePlayerPacket(byte[] data) {
	ByteBuffer b = ByteBuffer.wrap(data);
	entityID = b.getInt();
	x = b.getFloat();
	y = b.getFloat();
	z = b.getFloat();
	yaw = b.getFloat();
	pitch = b.getFloat();
    }

    byte[] response(PacketHandler handler) {
	handler.player.setLocation(x,y,z,yaw,pitch);
	System.out.println(handler.player.getUsername() + " moved: X: " + x + " Y: " + y + " Z: " + z + " Yaw: " + yaw + " Pitch: " + pitch);
	return null;
    }
}
