package q2p.filestorter.sorts.mergenearest;

import java.util.LinkedList;
import q2p.filestorter.sorts.Sorter;

public final class MergeNearestSorter extends Sorter {
	private static Fork currentFork = null;

	protected final void setFiles(final LinkedList<String> files) {
		currentFork = new Fork(null, files.toArray(new String[files.size()]));
		currentFork = currentFork.getDeepest();
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