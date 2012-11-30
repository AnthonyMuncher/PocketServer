package pocketserver.packets;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import pocketserver.Hex;
import pocketserver.PacketHandler;

public class Packet1cPingConnections extends Packet {
    private long pingID;
    private int packetType;
    private byte[] magic;
    private long serverID = 1L;
    
    String identifier = "MCCPP;Demo;";
    String serverName = "PocketServer for Mac/PC";

    public Packet1cPingConnections(DatagramPacket p) {
        ByteBuffer bb = ByteBuffer.wrap(p.getData());
        packetType = bb.get();
        if (packetType != 0x1c) { return; }
        pingID = bb.getLong();
        magic = Hex.getMagicFromBuffer(bb);
    }

    @Override
    public DatagramPacket getPacket() {
        String motd =  identifier + serverName;
        byte[] motdBytes = motd.getBytes();
        ByteBuffer rData;
        rData = ByteBuffer.allocate(35+motd.length());
        rData.put((byte)0x1c);
        rData.putLong(pingID);
        rData.putLong(serverID);
        rData.put(magic);
        rData.putShort((short)motd.length());
        rData.put(motdBytes);
        return new DatagramPacket(rData.array(),35+motd.length());
    }
    
    @Override
    public void process(PacketHandler handler) {
        handler.write(getPacket());
    }
}
