package q2p.filestorter.sorts.mergedeepest;

import java.util.ArrayList;
import java.util.LinkedList;
import q2p.filestorter.sorts.Sorter;

public final class MergeDeepestSorter extends Sorter {
	static final ArrayList<Fork> deepestForks = new ArrayList<Fork>();
	private static Fork currentFork = null;

	protected final void setFiles(final LinkedList<String> files) {
		new Fork(null, files.toArray(new String[files.size()]));
		
		if(!deepestForks.isEmpty())
			currentFork = deepestForks.remove(0);
	}
	
	protected final void answer(final boolean isSecound) {
		currentFork = currentFork.answer(isSecound?(byte)1:0);
	}
	
	protected final String file(final boolean isSecound) {
		return currentFork.file(isSecound?(byte)1:0);
	}

	protected final boolean isDone() {
		return currentFork == null;
	}

	protected final boolean tryToLoad() {
		// TODO Auto-generated method stub
		return false;
	}
}