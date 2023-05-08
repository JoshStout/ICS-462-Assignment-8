import java.util.*;

public class Allocator {
	
	public static int makeRequest(LinkedList<Root> rootList, int request) {
		
		request = request + 2;
		
		// illegal request: illegal block size requested
		if(!checkSize(request)) return -2;
		
		// if the request size is 512, allocate now and not go further into Allocator class
		boolean foundFreeRoot = true;
		if(request == 512) {
			foundFreeRoot = false;
		}
		
		for(int i = 0; i < rootList.size(); i++) {
			
			if(request == 512 && rootList.get(i).getRootFree()) {
				rootList.get(i).allocateRoot();
				Globals.address = rootList.get(i).getAddress();
				return 0;
			}
		}
		
		// if request is for a root buffer and no roots are free
		if(!foundFreeRoot) {
			return -1;
		}
		
		for(int i = 0; i < rootList.size(); i++) {
			
			int code = rootList.get(i).getAvailability(request);
			if(code == 0) {
				rootList.get(i).requestAllocation(request);
				return 0;
			}
		}
		
		// cannot make request due to lack of space
		return -1;
	}
	
	
	// check if the buffer size requested is illegal
	public static boolean checkSize(int request) {
		
		switch(request) {
		case 512:
			return true;
		case 256:
			return true;
		case 128:
			return true;
		case 64:
			return true;
		case 32:
			return true;
		case 16:
			return true;
		case 8:
			return true;
		default:
			return false;
		}
	}
	
	
	public static int checkSizeAvailable(LinkedList<Buffer> bufferList, int request) {
		int answer = -1;
		
		// first buffer is not use, start splitting it
		if(bufferList.size() == 1 && bufferList.get(0).getIsFree()) {
			return 0;
		}
				
		// check if there are full buddies and get their size
		int fullSize = 512;
		for(int i = 0; i < bufferList.size(); i++) {
			if(bufferList.get(i).getData() && bufferList.get(i).getBuddy().getData()){
				fullSize = bufferList.get(i).getSize();
			}
		}
		
		// check if there is a buffer larger than full size that is free
		if(fullSize < request) {
			for(int i = 0; i < bufferList.size(); i++) {
				if(bufferList.get(i).getSize() == request && bufferList.get(i).getIsFree()) {
					return 0;
				}
			}
		}
		
		// check if the smallest allocated memory is larger than request and can be split
		int smallestBuffer = 512;
		Buffer smallest = null;
		for(int i = 0; i < bufferList.size(); i++) {
			if(bufferList.get(i).getSize() < smallestBuffer && !bufferList.get(i).getData() && !bufferList.get(i).getSplit()) {
				smallestBuffer = bufferList.get(i).getSize();
				smallest = bufferList.get(i);
			}
		}
		
		if(smallestBuffer <= request) {
			return 0;
		}
		
		if(smallest != null) {
			return 0;
			
		}
		
		// size is not available
		return answer;
		
	}
	
	
	// a static method for debugging
	public static LinkedList<Buffer> requestBuffer(LinkedList<Buffer> bufferList, Buffer root, int request){		
		return BufferSlicer(bufferList, root, request);
	}
	
	
	public static LinkedList<Buffer> BufferSlicer(LinkedList<Buffer> bufferList, Buffer root, int request) {
		
		Buffer parent = root;
		
		int smallestBuffer = 512;
		
		for(int i = 0; i < bufferList.size(); i++) {
			
			// if free buffer matching request size found, set buffer as not free and return
			if(bufferList.get(i).getIsFree() && bufferList.get(i).getSize() == request) {
				
				bufferList.get(i).setIsFree(false);
				bufferList.get(i).setData(true);
				bufferList.get(i).setParent(parent);
				bufferList.get(i).getParent().setIsFree(false); //this will likely need to be removed
				bufferList.get(i).getParent().setHasChild(true);
				
				// add address to a shared variable
				Globals.address = bufferList.get(i).getAddress();
				
				return bufferList;
			}
			
			// continue since free buffer matching request size not found
			
			// find smallest buffer in list
			if(bufferList.get(i).getIsFree() && bufferList.get(i).getSize() < smallestBuffer) {
				smallestBuffer = bufferList.get(i).getSize();
				parent = bufferList.get(i);
			}
		}
		
		// split the smallest buffer in list in half and make recursive call 
		parent.setHasChild(true);
		parent.setSplit(true);
		Buffer left = createLeftBuffer(smallestBuffer/2, parent, parent.getRoot());
		Buffer right = createRightBuffer(smallestBuffer/2, parent, parent.getRoot());
		left.setBuddy(right);
		right.setBuddy(left);
		bufferList.add(left);
		bufferList.add(right);
		
		// sort the linked list by offset
		Collections.sort(bufferList);
		
		return BufferSlicer(bufferList, parent, request);
	}
	
	// method for creating the left buffer
	private static Buffer createLeftBuffer(int size, Buffer parent, Buffer root) {
		Buffer left = new Buffer();
		left.setSize(size);
		left.setIsFree(true);
		left.setSide(0);
		left.setParent(parent);
		left.setOffset(parent.getOffset());
		left.setAddress();
		left.getParent().setIsFree(false);
		left.setRoot(root);
		return left;
	}
	
	// method for creating the right buffer
	private static Buffer createRightBuffer(int size, Buffer parent, Buffer root) {
		Buffer right = new Buffer();
		right.setSize(size);
		right.setIsFree(true);
		right.setSide(1);
		right.setParent(parent);
		right.setOffset(parent.getOffset() + size);
		right.setAddress();
		right.setRoot(root);
		return right;
	}
}
