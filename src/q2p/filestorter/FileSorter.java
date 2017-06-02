package q2p.filestorter;

import java.net.InetAddress;
import java.net.ServerSocket;
import q2p.filestorter.sorts.Sorter;
import q2p.filestorter.sorts.bestofall.BestOfAll;

public final class FileSorter {
	private static ServerSocket serverSocket;
	private static final ProcessRequest PROCESS_REQUEST = new ProcessRequest();
	private static Thread thread;
	
	public static final void main(final String[] args) {
		if(!Sorter.setSorter(new BestOfAll()))
			FilePicker.pickFromAllAndNWsAmount(false, false, false, false, true, false, false, 128);
//		if(!Sorter.setSorter(new BinarySearchSort()))
//			FilePicker.pickFromDirrectory("f/");
//		FilePicker.pickFromAllAndNWs(false, true, false, false, false, false, false);
//		FilePicker.pickFromLastNW(false, true, false, false, false, false, false);
		
		try { serverSocket = new ServerSocket(3434, 0, InetAddress.getLoopbackAddress()); }
		catch (Exception e) { abort(e); }
		
		System.out.println("Image merge sorting server started. You can access it by \""+serverSocket.getInetAddress().getHostAddress()+":"+serverSocket.getLocalPort()+"\".");
		
		while(true) {
			System.gc();
			try {
				ProcessRequest.socket(serverSocket.accept());
			} catch (final Exception e) { continue; }
			thread = new Thread(PROCESS_REQUEST);
			thread.start();
			try { thread.join(); }
			catch (final InterruptedException e) {}
		}
	}

	public static final void abort(final Exception e) {
		e.printStackTrace();
		System.exit(1);
	}
}