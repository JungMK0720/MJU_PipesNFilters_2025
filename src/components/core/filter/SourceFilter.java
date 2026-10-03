/**
 * Copyright(c) 2021 All rights reserved by Jungho Kim in Myungji University.
 */
package components.core.filter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import annotation.source.SourceFile;
import components.core.filter.impl.CommonFilterImpl;

@SourceFile("Students.txt")
public class SourceFilter extends CommonFilterImpl {

	@Override
	public boolean specificComputationForFilter() throws IOException {
		String fileName = "Students.txt";
		System.out.println("[Source] Reading from " + fileName);

		try (BufferedInputStream br = new BufferedInputStream(new FileInputStream(new File(fileName)))) {
			int data;
			while ((data = br.read()) != -1) {
				out.write(data);
			}
			out.flush();
		}
		return true;
	}
}