package q2p.filestorter.sorts.insertionsort;

import java.util.LinkedList;
import q2p.filestorter.sorts.Sorter;

public final class InsertionSort extends Sorter {
	private static String[] total;
	private static int cursor;
	private static int toPick;
	private static boolean isDone = false;
	
	protected final void setFiles(final LinkedList<String> files) {
		total = new String[files.size()];
		files.toArray(total);
		toPick = 1;
		pickNext();
	}
		
	protected final void answer(final boolean isSecound) {
		if(isSecound) {
			pickNext();
		} else {
			final String t = total[cursor];
			total[cursor] = total[cursor-1];
			total[cursor-1] = t;
			cursor = cursor-1;
			if(cursor-1 < 0)
				pickNext();
		}
		if(isDone())
			save(total);
	}
	
	private final void pickNext() {
		cursor = toPick;
		if(cursor >= total.length)
			isDone = true;
		toPick++;
	}
	
	protected final String file(final boolean isSecound) {
		return total[isSecound?cursor-1:cursor];
	}

	protected final boolean isDone() {
		return isDone;
	}

	protected final boolean tryToLoad() {
		// TODO Auto-generated method stub
		return false;
	}
}