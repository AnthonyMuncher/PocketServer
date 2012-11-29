package pocketserver.packets;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import pocketserver.Hex;

public class Packet84StartGamePacket {

    byte[] getPacket() {
	
    ByteBuffer b = ByteBuffer.allocate(54);
	// ReadyPacket
	b.put((byte)0x60);
	b.put((byte)0x00);
	b.put((byte)0x28);
	b.put(Hex.intToBytes(1, 3));
	b.put(Hex.intToBytes(1, 4));
	b.put((byte)0x84);
	b.putInt(0);
    
	// StartGamePacket
        b.put((byte)0x60);
	b.put((byte)0x00);b.put((byte)0x28);
	b.put(Hex.intToBytes(2, 3));
	b.put(Hex.intToBytes(2, 4));
	b.put((byte)0x87);
	b.putInt(1353526199);	// seed
	b.putInt(0);
	b.putInt(1);
	b.putInt(2);
	b.putFloat(1.0f);
	b.putFloat(1.0f);
	b.putFloat(1.0f);
        return b.array();
    }
    
}
