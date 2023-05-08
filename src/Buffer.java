
public class Buffer implements Comparable<Buffer> {
	private int size;
	private Buffer buddy;
	private int offset;
	private int address;
	private Buffer parent;
	private boolean free;
	private int side;
	private boolean baseBuffer;
	private boolean isParent;
	private boolean hasChild;
	private boolean data;
	private boolean split;
	private Buffer root;

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Buffer getBuddy() {
		return buddy;
	}

	public void setBuddy(Buffer buddy) {
		this.buddy = buddy;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public void setAddress() {
		this.address = offset + 2;
	}
	
	public int getAddress() {
		return address;
	}

	public Buffer getParent() {
		return parent;
	}

	public void setParent(Buffer parent) {
		this.parent = parent;
	}

	public boolean getIsFree() {
		return free;
	}

	public void setIsFree(boolean free) {
		this.free = free;
	}
	
	// 0 = left, 1 = right
	public int getSide() {
		return side;
	}

	public void setSide(int side) {
		this.side = side;
	}
		
	public void setHasChild(boolean child) {
		this.hasChild = child;
	}
	
	public boolean getHasChild() {
		return this.hasChild;
	}
	
	public void setData(boolean d) {
		this.data = d;
	}
	
	public boolean getData() {
		return data;
	}
	
	public void setSplit(boolean s) {
		this.split = s;
	}
	
	public boolean getSplit() {
		return split;
	}
	
	public void setRoot(Buffer a) {
		this.root = a;
	}
	
	public Buffer getRoot() {
		return root;
	}
	
	public String getControlWord() {
		return String.valueOf(size) + String.valueOf(this.getBuddy().getAddress()); 
	}
	
	@Override
	public int compareTo(Buffer o) {
		
		if(this.getOffset() > o.getOffset()) {
			return 1;
		}else if(this.getOffset() < o.getOffset()) {
			return -1;
		}else {
			return 0;
		}
	}

}
