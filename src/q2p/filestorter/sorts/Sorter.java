package q2p.filestorter.sorts;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import q2p.filestorter.FileSorter;

public abstract class Sorter {
	private static Sorter sorter = null;
	public static final boolean setSorter(final Sorter sorter) {
		Sorter.sorter = sorter;
		return sorter.tryToLoad();
	}

	public static void initilize(final LinkedList<String> files) {
		sorter.setFiles(files);
	}

	public static final void save(final String[] sorted) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File("sorted.txt"));
			boolean needNL = false;
			for(final String path : sorted) {
				fos.write(((needNL?"\n":"")+path).getBytes(StandardCharsets.UTF_8));
				needNL = true;
			}
			fos.flush();
			fos.close();
		} catch(final Exception e) {
			try { fos.close(); }
			catch(final Exception e1) {}
			FileSorter.abort(e);
		}
	}
	
	public static void pushAnswer(final boolean isSecound) {
		sorter.answer(isSecound);
	}
	public static String getFile(final boolean isSecound) {
		return sorter.file(isSecound);
	}
	public static boolean checkIfDone() {
		return sorter.isDone();
	}

	protected abstract void setFiles(final LinkedList<String> files);
	protected abstract void answer(final boolean isSecound);
	protected abstract String file(final boolean isSecound);
	protected abstract boolean isDone();
	protected abstract boolean tryToLoad();
}
