package q2p.filestorter.sorts.binarysearchsort;

import java.util.LinkedList;
import q2p.filestorter.sorts.Sorter;

public final class BinarySearchSort extends Sorter {
	private static String[] total;
	private static int cursor;
	private static int toPick;
	private static boolean isDone = false;
	
	private static int left, right, mid;
	
	protected final void setFiles(final LinkedList<String> files) {
		total = new String[files.size()];
		files.toArray(total);
		toPick = 1;
		pickNext();
	}
		
	protected final void answer(final boolean isSecound) {
		if(isSecound)
			right = mid;
		else
			left = mid + 1;
		
		if(left < right)
			mid = (left + right) / 2;
		else
			swapThem();
				
		if(isDone())
			save(total);
	}
	
	private final void swapThem() {
    final String temp = total[cursor];
    
		for(int i = cursor; i != left; i--)
      total[i] = total[i - 1];
		
    total[left] = temp;
    
		pickNext();
	}

	private final void pickNext() {
		cursor = toPick;
		toPick++;
		
		if(cursor >= total.length)
			isDone = true;
		else {
			left = 0;
			right = cursor;
			mid = (left + right) / 2;
		}
	}
	
	protected final String file(final boolean isSecound) {
		return total[isSecound?mid:cursor];
	}

	protected final boolean isDone() {
		return isDone;
	}

	protected final boolean tryToLoad() {
		// TODO Auto-generated method stub
		return false;
	}
}