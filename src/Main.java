import java.io.File;
import java.io.PrintWriter;
import java.util.*;

public class Main {
	
	static String status = "";
	
	public static void main(String[] args) {
		
		//PrinterWriter & I/O File code copied from ICS-340 InitialCodebase 
		//by Metropolitan State University Professor Michael Stein
		File outputFile;
		PrintWriter output = null;
		
		outputFile = new File( "output.txt" );
		if ( outputFile.exists() ) {
			outputFile.delete();
		}
		
		try {
			output = new PrintWriter(outputFile);			
		}
		catch (Exception x ) { 
			System.err.format("Exception: %s%n", x);
			System.exit(0);
		}

		String s = "4.30.23, Assignment 8\n";
		write(output, s);
		
		LinkedList<Root> rootList = new LinkedList<>();
		int request;
		String expected = "";
		
		
		// create the 10 buffers of size 510
		write(output, "Initializing buffers");
		write(output, " Expected values: 10 512 size buffers, Status Ok\n");
		int offset = 0;
		for(int i = 0; i < 10; i++) {
			rootList.add(new Root(offset, i));
			offset += 512;
		}
		write(output, outputFreeBufferCount(rootList));
		status = checkStatus(rootList);
		write(output, status + "\n\n");
		
		// make an illegal request
		request = 700;
		expected = " ";
		write(output, allocationOutput(rootList, expected, request));
		
		// make size 6 buffer request
		request = 6;
		expected = " 9 510 size buffers, 1 254 size buffer, 1 126 size buffer,\n "
				+ "1 62 size buffer, 1 30 size buffer, 1 14 size buffer and 1 6 size buffer, "
				+ "Status OK";
		write(output, allocationOutput(rootList, expected, request));
		
		
		// return the 7-word buffer
		request = 6;
		expected = "10 510 size buffers, Status OK";
		write(output, coalesceOutput(rootList, expected, request));
		
		
		// request all 510 size buffers using a loop
		write(output, "Request 10 510 buffers");
		write(output, " Expected values:");
		write(output, " 10 510 buffers, 0 for all buffers, Status Tight\n");
		int[] assignedAddresses = new int[10];
		int loopCode = 0;
		int code = 0;
		request = 510;
		for(int i = 0; i < 10; i++) {
			loopCode = Allocator.makeRequest(rootList, request);
			assignedAddresses[i] = Globals.address;
			if(loopCode != 0) {
				code = loopCode;
			}
		}
		if(code != 0) {
			write(output, "Assigned address: " + code);
		}
		for(int i = 0; i < 10; i++) {
			s = "Actual = Assigned address: " + assignedAddresses[i] + "\n";
			write(output, s);
		}
		assignedAddresses = null; // garbage collection
		write(output, outputFreeBufferCount(rootList));
		status = checkStatus(rootList);
		write(output, status);
		write(output, "\n");
		
		
		// request another buffer of any size and should receive a -1 status
		write(output, "Request additional buffer");
		request = 254;
		expected = " Assigned address: -1, \n 0 150 buffers, Status Tight\n";
		write(output, allocationOutput(rootList, expected, request));
		status = checkStatus(rootList);
		write(output, status);
		write(output, "\n");
		
		
		// return 10 510 buffers
		request = 510;
		write(output, "Return 10 510 buffers");
		write(output, " Expected values:");
		write(output, " 10 510 size buffers, Status OK");
		boolean release = true;
		boolean loopRelease = release;
		for(int i = 0; i < 10; i++) {
			release = Coalescer.releaseBuffer(rootList, request);
			if(!release) {
				loopRelease = false;
			}
		}
		if(!loopRelease) {
			write(output, "Status FAIL");
		}
		write(output, "\nActual = ");
		write(output, "\n");
		write(output, outputFreeBufferCount(rootList));
		status = checkStatus(rootList);
		write(output, status);
		
		// completed required tests outlined in assignment instructions //
		
		// "Next make up some other tests to verify the proper functioning of your program."
		
		write(output, "\n\nCompleted required tests outlined in the assignment instructions.\n");
		write(output, "Making up some other tests to verify the proper functioning of the program...\n");
		
		// request 19 254 size buffers
		int[] assignedAddresses2 = new int[19];
		request = 254;
		write(output, "\n\nRequest 19 254 size buffers");
		write(output, " Expected values:\n 0 510 buffers, 1 254 size buffers, 0 126 size buffers,\n" +
				" 0 62 size buffers, 0 30 size buffers, 0 14 size buffers,  0 6 size buffers Status Tight\n\n");
		loopCode = 0;
		for(int i = 0; i < 19; i++) {
			code = Allocator.makeRequest(rootList, request);
			assignedAddresses2[i] = Globals.address;
			if(code != 0) {
				loopCode = code;
			}
		}
		if(loopCode != 0) {
			write(output, "Assigned address: " + loopCode);
		}
		for(int i = 0; i < 19; i++) {
			s = "Actual = Assigned address: " + assignedAddresses2[i] + "\n";
			write(output, s);
		}
		assignedAddresses2 = null; // garbage collection
		write(output, outputFreeBufferCount(rootList));
		status = checkStatus(rootList);
		write(output, status);
		write(output, "\n");
		
		
		// request 1 510 buffer after there is only 1 254 left
		write(output, "Request 1 510 buffer after there is only 1 254 left");
		request = 510;
		expected = " -1";
		write(output, allocationOutput(rootList, expected, request));
		status = checkStatus(rootList);
		write(output, status);
		write(output, "\n");
		
		
		// return all used buffers
		write(output, "Return 19 254 size buffers");
		write(output, " Expected values:");
		write(output, " 10 510 buffers, 0 254 size buffers, 0 126 size buffers,\n" + 
				" 0 62 size buffers, 0 30 size buffers, 0 14 size buffers,  0 6 size buffers Status OK");
		release = true;
		loopRelease = release;
		request = 254;
		for(int i = 0; i < 19; i++) {
			release = Coalescer.releaseBuffer(rootList, request);
			if(!release) {
				loopRelease = false;
			}
		}
		if(!loopRelease) {
			write(output, "Status FAIL");
		}
		write(output, "\nActual = ");
		write(output, "\n");
		write(output, outputFreeBufferCount(rootList));
		status = checkStatus(rootList);
		write(output, status);
		
		
		// request 254 size buffers
		request = 254;
		expected = " 9 510 size buffers, 1 254 size buffers, 0 126 size buffers\n "
				+ "0 62 size buffers, 0 30 size buffers, 0 14 size buffers, 0 6 size buffers Status OK";
		write(output, allocationOutput(rootList, expected, request));
		
		// request 510 size buffers twice
		request = 510;
		expected = " 8 510 size buffers, 0 254 size buffers, 0 126 size buffers\n "
				+ "0 62 size buffers, 0 30 size buffers, 0 14 size buffers, 0 6 size buffers Status OK";
		write(output, allocationOutput(rootList, expected, request));
		expected = " 7 510 size buffers, 0 254 size buffers, 0 126 size buffers\n "
				+ "0 62 size buffers, 0 30 size buffers, 0 14 size buffers, 0 6 size buffers Status OK";
		write(output, allocationOutput(rootList, expected, request));
		
		
		// request 6 size buffer twice 
		request = 6;
		expected = " 7 510 size buffers, 0 254 size buffers, 1 126 size buffers\n "
				+ "1 62 size buffers, 1 30 size buffers, 1 14 size buffers, 1 6 size buffers Status OK";
		write(output, allocationOutput(rootList, expected, request));
		expected = " 7 510 size buffers, 0 254 size buffers, 1 126 size buffers\n"
				+ " 1 62 size buffers, 0 30 size buffers, 0 14 size buffers, 0 6 size buffers Status OK";
		write(output, allocationOutput(rootList, expected, request));
		
		
		// return 6 size buffer
		request = 6;
		expected = " 7 510 size buffers, 0 254 size buffers, 1 126 size buffers,\n"
				+ " 1 62 size buffers, 1 30 size buffers, 1 14 size buffers, 1 size 6 buffers Status OK";
		write(output, coalesceOutput(rootList, expected, request));
		
		
		// return size 510 buffer
		request = 510;
		expected = " 8 510 size buffers, 0 254 size buffers, 1 126 size buffers,\n "
				+ " 1 62 size buffers, 1 30 size buffers, 1 14 size buffers, 1 6 size buffers Status OK";
		write(output, coalesceOutput(rootList, expected, request));
		
		
		// return size 254 size buffer
		request = 254;
		expected = " 8 510 size buffers, 1 256 size buffers, 1 126 size buffers, \n"
				+ " 1 62 size buffers, 1 30 size buffers, 1 14 size buffers, 1 6 size buffers Status OK";
		write(output, coalesceOutput(rootList, expected, request));
		
		
		// return size 510 buffer
		request = 510;
		expected = " 9 510 size buffers, 1 256 size buffers, 1 126 size buffers, \n"
				+ " 1 62 size buffers, 1 30 size buffers, 1 14 size buffers, 1 6 size buffers Status OK";
		write(output, coalesceOutput(rootList, expected, request));
		
		
		// return 6 size buffer
		request = 6;
		expected = " 10 510 size buffers, 0 256 size buffers, 0 126 size buffers, \n"
				+ " 0 62 size buffers, 0 30 size buffers, 0 14 size buffers, 0 6 size buffers Status OK";
		write(output, coalesceOutput(rootList, expected, request));
		
		
		// request 30 size buffer twice 
		request = 30;
		expected = " 9 510 size buffers, 1 254 size buffers, 1 126 size buffers\n "
				+ "1 62 size buffers, 1 30 size buffers, 0 14 size buffers, 0 6 size buffers Status OK";
		write(output, allocationOutput(rootList, expected, request));
		expected = " 9 510 size buffers, 1 254 size buffers, 1 126 size buffers\n"
				+ " 1 62 size buffers, 0 30 size buffers, 0 14 size buffers, 0 6 size buffers Status OK";
		write(output, allocationOutput(rootList, expected, request));
		
		
		// return 30 size buffer
		request = 30;
		expected = " 9 510 size buffers, 1 256 size buffers, 1 126 size buffers, \n"
				+ " 1 62 size buffers, 1 30 size buffers, 0 14 size buffers, 0 6 size buffers Status OK";
		write(output, coalesceOutput(rootList, expected, request));
		
		
		// request 14 size buffer twice
		request = 14;
		expected = " 9 510 size buffers, 1 254 size buffers, 1 126 size buffers\n "
				+ "1 62 size buffers, 0 30 size buffers, 1 14 size buffers, 0 6 size buffers Status OK";
		write(output, allocationOutput(rootList, expected, request));
		expected = " 9 510 size buffers, 1 254 size buffers, 1 126 size buffers\n "
				+ "1 62 size buffers, 0 30 size buffers, 0 14 size buffers, 0 6 size buffers Status OK";
		write(output, allocationOutput(rootList, expected, request));
		
		
		// return 30 size buffer
		request = 30;
		expected = " 9 510 size buffers, 1 256 size buffers, 1 126 size buffers, \n"
				+ " 1 62 size buffers, 1 30 size buffers, 0 14 size buffers, 0 6 size buffers Status OK";
		write(output, coalesceOutput(rootList, expected, request));
		
		// return 14 size buffer
		request = 14;
		expected = " 9 510 size buffers, 1 256 size buffers, 1 126 size buffers, \n"
				+ " 1 62 size buffers, 1 30 size buffers, 1 14 size buffers, 0 6 size buffers Status OK";
		write(output, coalesceOutput(rootList, expected, request));

		// return 14 size buffer
		request = 14;
		expected = " 10 510 size buffers, 0 256 size buffers, 0 126 size buffers, \n"
				+ " 0 62 size buffers, 0 30 size buffers, 0 14 size buffers, 0 6 size buffers Status OK";
		write(output, coalesceOutput(rootList, expected, request));

		
		
		output.flush();
	}
	
	// a static method to start allocation and automatically output to both the console and output file
	static String allocationOutput(LinkedList<Root> rootList, String expected, int request) {
		String output = "";
		output += "Requesting buffer size " + request;
		output += "\n Expected values:\n" + expected + "\n";
		int code = Allocator.makeRequest(rootList, request);
		if(code == -1) {
			output += "Assigned address: " + code;
			output += "\n Debug Output:\n\n\n";
			output += outputFreeBufferCount(rootList);
		}
		if(code == -2) {
			output += "Assigned address: " + code;
			output += "\n\nActual = Assigned address: " + code + "\n\n";
		}
		if(code == 0) {
			output += "\n\nActual = Assigned address: " + Globals.address + "\n\n";
			output += outputFreeBufferCount(rootList);
			status = checkStatus(rootList);
			output += status + "\n";
		}
		
		return output;
	}
	
	// a static method to start coalesce and automatically output to both the console and output file
	static String coalesceOutput(LinkedList<Root> rootList, String expected, int request) {
		String output = "";
		output += "\nReturn buffer size " + request;
		output += "\n Expected values:\n";
		output += " " + expected + "\n";
		boolean release = Coalescer.releaseBuffer(rootList, request);
		if(!release) {
			output += "\nCoalescer FAILED\n\n";
		}else {
			output += "\nActual =\n\n";
			output += outputFreeBufferCount(rootList);
			status = checkStatus(rootList);
			output += status + "\n\n";
		}
		
		return output;

	}
	
	// a static method to format an output String
	static String outputFreeBufferCount(LinkedList<Root> rootList) {
		String output = "";

		int count254Free = 0;
		int count126Free = 0;
		int count62Free = 0;
		int count30Free = 0;
		int count14Free = 0;
		int count7Free = 0;
		
		int freeRoots = 0;
		for(Root root : rootList) {
			
			if(root.getRootFree()) {
				freeRoots++;
			}
			
			// get number of each size of buffer
			for(Buffer buffer : root.requestRootList()) {
				
				switch(buffer.getSize()) {
					case 256:
						if(buffer.getIsFree()) count254Free++;
						break;
					case 128:
						if(buffer.getIsFree()) count126Free++;
						break;
					case 64:
						if(buffer.getIsFree()) count62Free++;
						break;
					case 32:
						if(buffer.getIsFree()) count30Free++;
						break;
					case 16:
						if(buffer.getIsFree()) count14Free++;
						break;
					case 8:
						if(buffer.getIsFree()) count7Free++;
						break;
				}
			}
		}
		
		output += "Free Buffer Count:\n";
		output += freeRoots + " 510 size buffers\n";
		output += count254Free + " 254 size buffers\n";
		output += count126Free + " 126 size buffers\n";
		output += count62Free + " 62 size buffers\n";
		output += count30Free + " 30 size buffers\n";
		output += count14Free + " 14 size buffers\n";
		output += count7Free + " 6 size buffers\n\n";
		
		return output;
	}
	
	// a static method to return Tight or OK status of the buffer pool
	public static String checkStatus(LinkedList<Root> rootList) {
		int freeRoots = 0;
		for(Root root : rootList) {
			if(root.getRootFree() == true) {
				freeRoots++;
			}
		}
		if(freeRoots > 1) {
			return "Status:\n\nOK";
		}else {
			return "Status:\n\nTight";
		}
	}
	
	
	// a static method to output to both the console and output file
	public static void write(PrintWriter output, String s) {
		System.out.println(s);
		output.println(s);
	}
	

}
