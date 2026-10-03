/**
 * Copyright(c) 2021 All rights reserved by Jungho Kim in Myungji University.
 */
package components.core.filter.impl;

import java.io.EOFException;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import components.core.filter.CommonFilter;

public abstract class CommonFilterImpl implements CommonFilter {
	protected PipedInputStream in = new PipedInputStream();
	protected PipedOutputStream out = new PipedOutputStream();

	public void connectOutputTo(CommonFilter nextFilter) throws IOException {
		out.connect(nextFilter.getPipedInputStream());
	}
	public void connectInputTo(CommonFilter previousFilter) throws IOException {
		in.connect(previousFilter.getPipedOutputStream());
	}
	public PipedInputStream getPipedInputStream() {
		return in;
	}
	public PipedOutputStream getPipedOutputStream() {
		return out;
	}
	
	abstract public boolean specificComputationForFilter() throws IOException;
	// Implementation defined in Runnable interface for thread
	public void run() {
		try {
			specificComputationForFilter();
		} catch (IOException e) {
			if (e instanceof EOFException) return;
			else System.out.println(e);
		} finally {
			closePorts();
		}
	}
	
	 /** 추가 구현(필터 통과시키기) */
    public void passThrough() throws IOException {
        int data;
        while ((data = in.read()) != -1) {
            out.write(data);
        }
        out.flush();
    }
    
	private void closePorts() {
		try {
			out.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
