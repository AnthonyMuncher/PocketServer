package pocketserver;

import java.util.logging.Logger;

public class PocketServer implements Runnable {

    public static final Logger logger = Logger.getLogger("PocketServer");
    
    private boolean isRunning;
    
    private PESocket peSocket = new PESocket(this);
    
    private void startServer() {
        isRunning = true;
        ConsoleLogManager.init();
        
        peSocket.start();
    }
        
    @Override
    public void run() {	
	try { 
	    startServer();
	}catch(Exception e) {
	    isRunning = false;
	}
	logger.info("Starting PockerServer v0.0.1");
	long ticks = 0;
	while (isRunning) {
	    sleep(100);
	    ticks++;
	    if (ticks % 100 == 0) {
		Runtime.getRuntime().gc();
	    }
	}
    }
    
    public static void main(String[] args){
        try {
            PocketServer server = new PocketServer();
            server.run();
        } catch (Exception e) {
            logger.info("Failed to start PocketServer");
        }
    }   

    public static void sleep(int i) {
        try { Thread.sleep(i); } catch (InterruptedException e) { }
    }
    
    static boolean isServerRunning(PocketServer server) {
        return server.isRunning;
    }
}
