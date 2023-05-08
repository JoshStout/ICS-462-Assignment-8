// this creates the linked list representing the root buffer (510 size buffer)
// and all of its allocated buffers within that single root buffer 

import java.util.LinkedList;

public class Root {
	LinkedList<Buffer> bufferList = new LinkedList<>();
	
	Buffer buffer = new Buffer();
	int rootNum;
	int offset;
	int address;
	
	public Root(int offset, int rootNum) {
		buffer.setSize(512);
		buffer.setOffset(offset);
		buffer.setIsFree(true);
		buffer.setBuddy(this.buffer);
		bufferList.add(buffer);
		this.rootNum = rootNum;
		this.offset = offset;
		this.address = offset + 2;
	}
	
	public LinkedList<Buffer> requestRootList(){
		return bufferList;
	}

	public void setRootNum(int num) {
		rootNum = num;
	}
	
	public int getRootNum() {
		return rootNum;
	}
	
	public boolean getRootFree() {
		if(buffer.getIsFree() && !buffer.getData()) {
			return true;
		}
		return false;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public int getAddress() {
		return address;
	}
	
	public String getControlWord() {
		return String.valueOf(address) + String.valueOf(address + 512);
	}
	
	public void allocateRoot() {
		buffer.setIsFree(false);
		buffer.setData(true);
	}
	
	// request allocation within root's buffers
	public LinkedList<Buffer> requestAllocation(int request){
		bufferList = Allocator.requestBuffer(bufferList, buffer, request);
		return bufferList;
	}
	
	// check if any buffers are available within root's buffers
	public int getAvailability(int request) {
		return Allocator.checkSizeAvailable(bufferList, request);
	}

}
