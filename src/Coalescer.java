import java.util.LinkedList;

public class Coalescer {
	
	// loop thru linked list and find buffer matching the returned buffer
	public static boolean releaseBuffer(LinkedList<Root> rootList, int request) {
		
		request = request + 2;
		
		for(Root root: rootList) {
			for(Buffer buffer: root.requestRootList()) {
				if(buffer.getSize() == request && buffer.getData() == true) {
					buffer.setIsFree(true);
					buffer.setData(false);
					coalesce(rootList);
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean coalesce(LinkedList<Root> rootList) {
		
		// loop thru and remove buffers no longer being used 
		for(Root root: rootList) {
			for(Buffer buffer: root.requestRootList()) {
				LinkedList<Buffer> bufferList = root.requestRootList();
				if(!buffer.getSplit() && buffer.getIsFree() && buffer.getBuddy().getIsFree() && buffer.getSize() != 512) {
					buffer.setIsFree(true);
					buffer.setData(false);
					buffer.getParent().setIsFree(true);
					buffer.getParent().setSplit(false);
					Buffer buddy = buffer.getBuddy();
					bufferList.remove(buffer);
					
					// garbage collection
					buddy = null;
					buffer = null;
					
					// coalesced: make recursive call
					coalesce(rootList);
				}
			}
		}
		
		// base case: no coalescing occurred
		return false;
	}
}
