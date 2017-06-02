package q2p.filestorter.sorts.bestofall;

import java.util.ArrayList;
import java.util.LinkedList;
import q2p.filestorter.sorts.Sorter;

public final class BestOfAll extends Sorter {
	private static final ArrayList<String> total = new ArrayList<String>();
	private static int cursor = 0;

	protected final void setFiles(final LinkedList<String> files) {
		total.addAll(files);
	}
	
	protected final void answer(final boolean isSecound) {
		int vr = isSecound?1:0;
		
		total.remove(cursor+(1-vr));
		
		cursor++;
		
		if(cursor == total.size()-1) {
			total.add(0, total.remove(total.size()-1));
			cursor = 0;
		} else if(cursor == total.size()) {
			cursor = 0;
		}
		
		if(isDone())
			save(new String[]{total.get(0)});
	}
	
	protected final String file(final boolean isSecound) {
		return total.get(isSecound?cursor+1:cursor);
	}

	protected final boolean isDone() {
		return total.size() == 1;
	}

	protected final boolean tryToLoad() {
		// TODO Auto-generated method stub
		return false;
	}
}