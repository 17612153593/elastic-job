package com.tansun.batch.util;

public class BatchConstant {

	//字段分隔符
	public static final char COLUMN_SEPARATOR = 0x01;
	public static final char COLUMN_SEPARATOR_1 = '|';

	//行分隔符
	public static final char LINE_SEPARATOR = 0x0A;


	public static final String BATCH_JOB_STATUS_SUCCESS = "0";
	public static final String BATCH_JOB_STATUS_RUNNING = "1";
	public static final String BATCH_JOB_STATUS_WAITING = "2";
	public static final String BATCH_JOB_STATUS_EXCEPTION = "3";
	public static final String BATCH_JOB_STATUS_fail = "4";

	//OK文件后缀
	public static final String OK_FILE_SUFFIX = ".ok";
	// end文件后缀
	public static final String END_FILE_SUFFIX = ".end";

	public static final String SEND_FILE_DIR_KEY = "job.sendFile.dir";

	public static final String RECV_FILE_DIR_KEY = "job.receiveFile.dir";


}
