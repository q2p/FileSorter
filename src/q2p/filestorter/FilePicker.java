package q2p.filestorter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import q2p.filestorter.sorts.Sorter;

final class FilePicker {
	static final String P;
	
	private static final LinkedList<String> files = new LinkedList<String>();
	
	static {
		// TODO: при компиляции ставить нормальный путь
		// final String path = new File("test.file").getAbsolutePath().replace("\\", "/");
		// P = path.substring(0, path.indexOf("/p/"+3));
		P = "E:/@MyFolder/p/";
	}
	
	public static final void pickFromAllAndNWs(final boolean folders, final boolean images, final boolean videos, final boolean flashVideos, final boolean swf, final boolean audios, final boolean otherFiles) {
		final LinkedList<String> nwsList = new LinkedList<String>(Arrays.asList(new File(P+"nws/").list()));

		while(!nwsList.isEmpty()) {
			final String name = nwsList.removeFirst();
			if(!name.startsWith("nw") || !(new File(P+"nws/"+name).isDirectory()))
				continue;
			try { Integer.parseInt(name.substring(2)); }
			catch(final NumberFormatException e) {
				continue;
			}
			getFromDir("nws/"+name+"/");
		}
				
		getFromDir("all/");
		
		filter(folders, images, videos, flashVideos, swf, audios, otherFiles);
		shuffle();
		Sorter.initilize(files);
	}
	
	public static final void pickNumbers(int amount) {
		final ArrayList<String> newf = new ArrayList<String>();
		while(amount != 0)
			newf.add(""+amount--);
		
		final Random r = new Random(amount);
		while(!newf.isEmpty())
			files.addLast(newf.remove(r.nextInt(newf.size())));
		
		Sorter.initilize(files);
	}
		
	private static void shuffle() {
		final ArrayList<String> newf = new ArrayList<String>(files);
		files.clear();
		final Random r = new Random();
		while(!newf.isEmpty())
			files.addLast(newf.remove(r.nextInt(newf.size())));
	}

	public static final void pickFromLastNW(final boolean folders, final boolean images, final boolean videos, final boolean flashVideos, final boolean swf, final boolean audios, final boolean otherFiles) {
		final LinkedList<String> nwsList = new LinkedList<String>(Arrays.asList(new File(P+"nws/").list()));
		
		int max = 0;
		
		while(!nwsList.isEmpty()) {
			final String name = nwsList.removeFirst();
			if(!name.startsWith("nw") || !(new File(P+"nws/"+name).isDirectory()))
				continue;
			try {
				final int n = Integer.parseInt(name.substring(2));
				if(n > max)
					max = n;
			} catch(final NumberFormatException e) {
				continue;
			}
		}
		
		if(max > 0)
			getFromDir("nws/nw"+max+"/");
		
		filter(folders, images, videos, flashVideos, swf, audios, otherFiles);
		shuffle();
		Sorter.initilize(files);
	}
	
	public static final void pickFromDirrectory(final String relativePath) {
		getFromDir(relativePath);
		
		shuffle();
		Sorter.initilize(files);
	}

	public static final void pickFromAllAndNWsAmount(final boolean folders, final boolean images, final boolean videos, final boolean flashVideos, final boolean swf, final boolean audios, final boolean otherFiles, final int maxAmount) {
		final LinkedList<String> nwsList = new LinkedList<String>(Arrays.asList(new File(P+"nws/").list()));

		while(!nwsList.isEmpty()) {
			final String name = nwsList.removeFirst();
			if(!name.startsWith("nw") || !(new File(P+"nws/"+name).isDirectory()))
				continue;
			try { Integer.parseInt(name.substring(2)); }
			catch(final NumberFormatException e) {
				continue;
			}
			getFromDir("nws/"+name+"/");
		}
				
		getFromDir("all/");
		
		filter(folders, images, videos, flashVideos, swf, audios, otherFiles);

		final Random r = new Random();
				
		while(!files.isEmpty() && nwsList.size() != maxAmount)
			nwsList.addLast(files.remove(r.nextInt(files.size())));
		
		files.clear();
		files.addAll(nwsList);
		nwsList.clear();
				
		shuffle();
		Sorter.initilize(files);
	}
	
	private static final void filter(final boolean folders, final boolean images, final boolean videos, final boolean flashVideos, final boolean swf, final boolean audios, final boolean otherFiles) {
		final Iterator<String> i = files.iterator();
		while(i.hasNext()) {
			final String path = i.next();
			if(new File(P+path).isDirectory()) {
				if(!folders)
					i.remove();
			} else {
				if(isImage(path)) {
					if(!images)
						i.remove();
				} else if(isImage(path)) {
					if(!videos)
						i.remove();
				} else if(isSwf(path)) {
					if(!swf)
						i.remove();
				} else if(isVideo(path)) {
					if(!videos)
						i.remove();
				} else if(isFlashVideo(path)) {
					if(!flashVideos)
						i.remove();
				} else if(isAudio(path)) {
					if(!audios)
						i.remove();
				} else {
					if(!otherFiles)
						i.remove();
				}
			}
		}
	}
	
	public static final boolean isImage(final String path) {
		return endsWith(path, ".png", ".jpg", ".jpeg", ".gif");
	}
	
	public static final boolean isVideo(final String path) {
		return endsWith(path, ".webm", ".mp4", ".mpeg");
	}
	
	public static final boolean isSwf(final String path) {
		return endsWith(path, ".swf");
	}
	
	public static final boolean isFlashVideo(final String path) {
		return endsWith(path, ".flv");
	}
	
	public static final boolean isAudio(final String path) {
		return endsWith(path, ".aac", ".mp3", "ogg", ".flac", ".wav");
	}
	
	public static final boolean endsWith(String path, final String ... extentions) {
		path = path.toLowerCase();
		for(final String extention : extentions)
			if(path.endsWith(extention.toLowerCase()))
				return true;
		return false;
	}

	private static final void getFromDir(final String relativePath) {
		final String[] names;
		try {
			names = new File(P+relativePath).list();
		} catch(final Exception e) {
			return;
		}
		for(final String name : names)
			files.add(relativePath+name);
	}
}