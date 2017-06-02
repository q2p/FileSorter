package q2p.filestorter.sorts.mergenearest;

import q2p.filestorter.sorts.Sorter;

final class Fork {
	private final Fork parent;
	
	private Fork[] slaves = null;
	String result[] = null;
	
	private int[] slavesPointer = new int[]{0,0};
	private int resultPointer = 0;
	
	Fork(final Fork parent, final String[] paths) {
		this.parent = parent;
		if(paths.length == 0) {
			result = null;
		} else if(paths.length == 1) {
			result = new String[] {paths[0]};
		} else {
			final int mid = paths.length/2;
			slaves = new Fork[2];
			String[] buff = new String[mid];
			for(int i = mid-1; i != -1; i--)
				buff[i] = paths[i];
			
			slaves[0] = new Fork(this, buff);
			
			buff = new String[paths.length-mid];
			for(int i = mid; i != paths.length; i++)
				buff[i-mid] = paths[i];
			
			slaves[1] = new Fork(this, buff);
		}
	}

	final Fork answer(final byte id) {
		result[resultPointer++] = slaves[id].result[slavesPointer[id]++];
		
		for(byte i = 0; i != 2; i++) {
			if(slavesPointer[i] == slaves[i].result.length) {
				while(slavesPointer[1-i] != slaves[1-i].result.length)
					result[resultPointer++] = slaves[1-i].result[slavesPointer[1-i]++];
				
				return next();
			}
		}
		System.out.println(slavesPointer[0] + " " + slavesPointer[1]);
		return this;
	}

	private final Fork next() {
		slaves = null;
		
		if(parent == null) {
			Sorter.save(result);
			return null;
		}
		
		return parent.getDeepest();
	}

	final String file(final byte id) {
		return slaves[id].result[slavesPointer[id]];
	}

	final Fork getDeepest() {
		if(slaves == null)
			return null;

		for(byte i = 0; i != 2; i++)
			if(slaves[i].result == null)
				return slaves[i].getDeepest();
		
		result = new String[slaves[0].result.length+slaves[1].result.length];
		
		return this;
	}
}