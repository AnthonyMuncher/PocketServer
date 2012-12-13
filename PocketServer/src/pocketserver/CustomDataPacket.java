package pocketserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 *
 * @author Intyre
 */
public class CustomDataPacket {
    
    private ByteBuffer response;
    
    public ByteBuffer messagePacket(String s, Player player) {
	String message = "[Server] " + s;
	
	int size = (message.length() + 3) * 8;
	int sizeB = message.length() + 10;
	response = ByteBuffer.allocate(sizeB);
	response.put((byte)0x84);
	response.put(Hex.intToBytes(player.getPacketCount(), 3));
	response.put((byte)0x00);
	response.putShort((short)size);
	response.put((byte)0x85);
	response.putShort((short)message.length());
	response.put(message.getBytes());
	
	return response;
    }
    
    public ByteBuffer chatPacket(String s, Player player) throws IOException {
	String message = s;
	byte[] sb = s.getBytes();
	int size = (message.length() + 3) * 8;
	ByteArrayOutputStream bo = new ByteArrayOutputStream();
	bo.write(0x84);
	bo.write(Hex.intToBytes(player.getPacketCount(), 3),0,3);
	bo.write(0x00);
	bo.write(Hex.shortToByte((short)size),0,2);
	bo.write(0x85);
	bo.write(Hex.shortToByte((short)s.length()),0,2);
	bo.write(sb);
	byte res[] = bo.toByteArray();

	response = ByteBuffer.allocate(res.length);
	response.put(res);
	return response;
    }
	
}
