package pocketserver;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 *
 * @author Intyre
 */
public class Player {

    private InetAddress clientAddress;
    private int clientPort;
    private String clientName;
    private long clientID;
    private int packetCount;
    private int dataCount;
    private ArrayList<Float> position;
    
    public Player(InetAddress address, int port,long cid) {
	clientAddress = address;
	clientPort = port;
	clientID = cid;
	clientName = "";
	packetCount = 0;
	dataCount = 0;
	position = new ArrayList<Float>();
	position.add(128.0f);
	position.add(65.0f);
	position.add(128.0f);
    }
    
    public void setName(String s) {
	clientName = s;
    }
    
    public String getName() {
	return clientName;
    }

    public InetAddress getAddress() {
	return clientAddress;
    }

    public int getPort() {
	return clientPort;
    }

    public int getPacketCount() {
	return packetCount++;
    }
    
    public int getDataCount() {
	return packetCount++;
    }
    
    public long getClientID() {
	return clientID;
    }
    
    public void setPosition(float x, float y, float z) {
	position.set(0,x);
	position.set(1,y);
	position.set(2,z);
    }
    
    public ArrayList getPosition() {
	return position;
    }
    
    
}
