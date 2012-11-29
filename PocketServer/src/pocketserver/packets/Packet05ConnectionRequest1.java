package pocketserver.packets;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import pocketserver.*;

public class Packet05ConnectionRequest1 extends Packet{

    private byte[] magic;
    private short mtuSize;
    private byte packetType;
    private Player player;

    public Packet05ConnectionRequest1(DatagramPacket packet) {
        ByteBuffer bb = ByteBuffer.wrap(packet.getData());
        packetType = bb.get();
        if (packetType != 0x05) { return; }
        magic = Hex.getMagicFromBuffer(bb);
        bb.get();

        mtuSize = (short)packet.getLength();
    }

    @Override
    public DatagramPacket getPacket() {
        ByteBuffer rData = ByteBuffer.allocate(19);
        rData.put((byte)0x06);
        rData.put(magic);
        rData.putShort(mtuSize);
        return new DatagramPacket(rData.array(),19);
    }

    @Override
    public void process(PacketHandler handler) {
        handler.process(getPacket());
    }
}
