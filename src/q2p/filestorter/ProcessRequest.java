package q2p.filestorter;

import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public final class ProcessRequest implements Runnable{
	private static Socket socket;
	public static final int MAX_BUFF = 256*1024*1024;
	
	public final void run() {
		try {
			final InputStream in = socket.getInputStream();
			Response.out = socket.getOutputStream();

			String httpHeader = "";
			// чтение заголовка HTTP
			byte[] buffer = new byte[8*1024*1024];
			int pointer = 0;
			int left = buffer.length;
			while(in.available() > 0 && left > 0) {
				final int o = in.read(buffer, pointer, Math.min(left, in.available()));
				pointer += o;
				left -= o;
			}
			
			if(pointer <= 0) {
				socket.close();
				return;
			}
			
			// преобразование полученных байт в строку
			httpHeader = new String(buffer, 0, pointer, StandardCharsets.UTF_8);
			buffer = null;
			
			final String path = parsePath(httpHeader);
			
			if(path == null) {
				Response.badRequest();
				socket.close();
				return;
			}

			if(path.equals("/")) {
				Response.page();
			} else if(path.equals("/file0")) {
				Response.file(false);
			} else if(path.equals("/file1")) {
				Response.file(true);
			} else if(path.equals("/answer0")) {
				Response.answer(false);
			} else if(path.equals("/answer1")) {
				Response.answer(true);
			} else if(path.equals("/path0")) {
				Response.path(false);
			} else if(path.equals("/path1")) {
				Response.path(true);
			} else {
				Response.badRequest();
			}
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			try { socket.close(); } catch(final Exception e) {}
		}
	}
	
	private final String parsePath(final String str) {
		final int bidx = str.indexOf(' ')+1;
		
		if(bidx == 0)
			return null;
		
		final int ridx = str.indexOf('\r');
		final int nidx = str.indexOf('\n');
		
		int eidx;
		if(ridx == -1)
			eidx = nidx;
		else if(nidx == -1)
			eidx = nidx;
		else
			eidx = Math.min(ridx, nidx);

		if(eidx == -1)
			return null;
		
		final int sidx = str.substring(bidx, eidx).lastIndexOf(' ');
		
		if(sidx == -1)
			return null;
		
		try {
			return URLDecoder.decode(str.substring(bidx, bidx+sidx).replaceAll("\\+", "%2B"), "UTF-8");
		} catch (final Exception e) {
			return null;
		}
	}

	public static void socket(final Socket socket) throws SocketException {
		ProcessRequest.socket = socket;
		socket.setSendBufferSize(2*MAX_BUFF);
	}
}