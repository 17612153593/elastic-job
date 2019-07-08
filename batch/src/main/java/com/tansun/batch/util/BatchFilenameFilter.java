package com.tansun.batch.util;

import java.io.File;
import java.io.FilenameFilter;



public class BatchFilenameFilter implements FilenameFilter {

	String regex;

	public BatchFilenameFilter(String regex) {
		this.regex = regex;
	}

	@Override
	public boolean accept(File dir, String name) {
		if (null != regex && !regex.trim().equals("")) {
			return name.matches(regex);
		} else {
			return true;
		}
	}
	
}