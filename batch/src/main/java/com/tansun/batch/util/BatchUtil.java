package com.tansun.batch.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.executor.ShardingContexts;

public class BatchUtil {

	private static final Logger logger = Logger.getLogger(BatchUtil.class);

	private static final String CONFIG_FILE = "job-config.properties";

	private static final long MAX_FILE_LENGTH = 2000000000l;

	private static Properties props;

	private static ShardingContext shardingContext;

	static {
		loadProperties();
	}

	synchronized private static void loadProperties() {
		logger.info("Loading config file ......");

		props = new Properties();
		InputStream in = null;
		in = BatchUtil.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
		try {
			props.load(in);
		} catch (IOException e) {
			logger.error("IOException in loading config file!");
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e1) {
					logger.error("IOException in closing InputStream!");
				}
			}
		}

		logger.info("Loading config file end!");
		logger.info("Properties: " + props);
	}

	public static String getProperty(String key) {
		if (null == props) {
			loadProperties();
		}

		return props.getProperty(key);
	}

	public static String getProperty(String key, String defaultValue) {
		if (null == props) {
			loadProperties();
		}

		return props.getProperty(key, defaultValue);
	}

	/**
	 * 数字到字符串转换，左侧0补位
	 * @param num 要转换的数字
	 * @param len 字符串的长度
	 * @return
	 */
	public static String int2Str(int num, int len) {
		StringBuffer sb = new StringBuffer();
		for (int i = len - (num / 10 + 1); i > 0; i--) {
			sb.append("0");
		}
		sb.append(num);
		return sb.toString();
	}

	/**
	 * 检查数据文件是否大于2G。大于则分拆文件，小于则直接生成OK文件
	 * @param src
	 */
	public static boolean checkDataFile(File src) {
		if (src.length() > MAX_FILE_LENGTH) {
			return splitDataFile(src);
		} else {
			return createOKFile(src);
		}
	}

	/**
	 * 生成OK文件
	 * @param src 数据文件
	 * @return
	 */
	public static boolean createOKFile(File src) {
		boolean result = false;
		String fileName = src.getName();
		String okName = src.getAbsolutePath() + BatchConstant.OK_FILE_SUFFIX;
		File okFile = new File(okName);
		FileOutputStream fos = null;
		OutputStreamWriter out = null;
		try {
			fos = new FileOutputStream(okFile);
			out = new OutputStreamWriter(fos, "UTF-8");
			out.write(fileName);
			result = true;
		} catch (FileNotFoundException e) {
			logger.error(okFile.getAbsolutePath() + " not found!");
		} catch (UnsupportedEncodingException e) {
			logger.error("UTF-8 is not supported!");
		} catch (IOException e) {
			logger.error("IOException in creating file[" + okName + "]!");
		} finally {
			if (null != out) {
				try {
					out.close();
				} catch (IOException e) {
					logger.error("IOException in closing file!");
				}
			}
			if (null != fos) {
				try {
					out.close();
				} catch (IOException e) {
					logger.error("IOException in closing file!");
				}
			}
		}

		return result;
	}

	public static boolean splitDataFile(File src) {
		boolean result = false;

		//确定分割的数量
		int num = 2;
		while (src.length() / num >= MAX_FILE_LENGTH) {
			num++;
		}
		logger.info("[" + src.getAbsolutePath() + "] should be splitted to " + num + " files!");

		//原文件改名
		File tmpFile = new File(src.getAbsolutePath().replaceAll("001.txt", ""));
		src.renameTo(tmpFile);
		String fileName = tmpFile.getAbsolutePath();

		InputStreamReader isr = null;
		BufferedReader br = null;
		List<OutputStreamWriter> writerList = null;
		try {
			isr = new InputStreamReader(new FileInputStream(tmpFile), "UTF-8");
			br = new BufferedReader(isr);
			String line = null;
			writerList = new ArrayList<OutputStreamWriter>();
			File[] dataFiles = new File[num];
			for (int i = 0; i < num; i++) {
				dataFiles[i] = new File(fileName + int2Str(i + 1, 3) + ".txt");
				writerList.add(new OutputStreamWriter(new FileOutputStream(dataFiles[i]), "UTF-8"));
			}
			int rownum = 0;
			while ((line = br.readLine()) != null) {
				writerList.get(rownum % num).write(line);
				rownum++;
			}

			for (int i = 0; i < num; i++) {
				createOKFile(dataFiles[i]);
			}

			result = true;
		} catch (FileNotFoundException e) {
			logger.error(tmpFile.getAbsolutePath() + " not found!");
			return false;
		} catch (IOException e) {
			logger.error("IOException in splitting data file!");
			return false;
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
					logger.error("IOException in closing BufferedReader!");
				}
			}
			if (null != isr) {
				try {
					isr.close();
				} catch (IOException e) {
					logger.error("IOException in closing FileReader!");
				}
			}
			for (int i = 0; i < num; i++) {
				try {
					writerList.get(i).close();
				} catch (IOException e) {
					logger.error("IOException in closing FileWriter!");
				}
			}
		}

		if (result) {
			if (null != tmpFile && tmpFile.exists()) {
				tmpFile.delete();
			}
		}

		return result;
	}

//	/**
//	 * post请求
//	 * @param url
//	 * @param json
//	 * @return
//	 */
//	public static JSONObject doPost(String url, JSONObject json) {
//		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
//		JSONObject response = null;
//	 	try {
//	        HttpPost post = new HttpPost(url);
//	        post.addHeader(HTTP.CONTENT_TYPE, "application/json");
//
//            StringEntity s = new StringEntity(json.toString());
//            s.setContentEncoding("UTF-8");
//            s.setContentType("text/json");//发送json数据需要设置contentType
//            post.setEntity(s);
//            //httpclient.execute(post);
//            HttpResponse res = httpclient.execute(post);
//            if(res.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
//                String result = EntityUtils.toString(res.getEntity());// 返回json格式
//                response = JSONObject.fromObject(result);
//            }
//        } catch (Exception e) {
//        	logger.error("Exception:" + e.getMessage(), e);
//            //throw new RuntimeException(e);
//        }
//	        return response;
//
//	}

//	/**
//	 * 接口调用发送请求
//	 * @param url
//	 * @param json
//	 * @return
//	 */
//	public static JSONObject doTransfer(Map<String, Object> resultMap) {
//
//		String url = getProperty("job.monitor.url");
//
//		//JSONObject jsonPost = JSONObject.fromObject(jsonRe);
//		JSONObject jsonPost = new JSONObject();
//		jsonPost.put("action", getProperty("job.monitor.action1"));
//		jsonPost.put("user", getProperty("job.monitor.user"));
//		logger.info("Json in the first request is " + jsonPost.toString());
//
//		//获取IP，本机计算机名称
//		InetAddress addr;
//		String ip = null;
//		String[] ips = null;
//		String hostName =null;
//
//		try {
//			addr = InetAddress.getLocalHost();
//			ip = addr.getHostAddress().toString();//获取IP
//			ips = ip.split("\\.");
//			ip = ips[2]+"."+ips[3];
//			hostName = addr.getHostName().toString();//获取本机计算机名称
//		} catch (UnknownHostException e) {
//			logger.error("Exception:" + e.getMessage(), e);
//			//throw new RuntimeException(e);
//		}
//
//		//第一次掉用doPost方法获取token
//		JSONObject response = BatchUtil.doPost(url, jsonPost);
//		JSONObject	response2 =null;
//		if(null != response){
//			//logger.info("Return of the first request is " + response.toString());
//			//responseDoPost.getString("token");
//			//获取token后，拼接json
//			JSONObject jsonPost2 = new JSONObject();
//			jsonPost2.put("action", getProperty("job.monitor.action2"));
//			jsonPost2.put("token", ((JSONObject) response.get("retResult")).getString("token"));
//			JSONObject message = new JSONObject();
//			message.put("PROJECTNAME", BatchConstant.SCC_SYS_CODE.toUpperCase());
//			message.put("ENTRYNAME", getProperty("job.monitor.entryName"));
//			message.put("JOBNAME", resultMap.get("JOBNAME"));
//			message.put("HOSTNAME", hostName);
//			message.put("COMPUTERIP", ip);
//			message.put("BATCHTYPE", resultMap.get("BATCHTYPE"));
//			message.put("DATE", resultMap.get("DATE"));
//			message.put("STARTTIME", resultMap.get("STARTTIME"));
//			message.put("ENDTIME", resultMap.get("ENDTIME"));
//			message.put("STATE", resultMap.get("STATE"));
//			message.put("ERRORNO", resultMap.get("ERRORNO"));
//			message.put("ERRORINFO", resultMap.get("ERRORINFO"));
//			message.put("ERRORTIME", resultMap.get("ERRORTIME"));
//			jsonPost2.put("message", message);
//			logger.info("Json in the second request is:" + jsonPost2);
//
//			//第二次掉用doPost
//			response2 = BatchUtil.doPost(url, jsonPost2);
//			if(null != response2){
//				logger.info("Return of the second request is " + response2.toString());
//			}
//		}
//		return response2;
//	}

//	public static JSONObject sendJobResult2Monitor(String jobName,
//			String batchType, String date, String startTime,
//			String endTime, String state, String errorNo, String errorInfo,
//			String errorTime) {
//		if(state.equals(BatchConstant.BATCH_JOB_STATE_WAITING)){
//			return null;
//		}else{
//			Map<String, Object> resultMap = new HashMap<String, Object>();
//			resultMap.put("JOBNAME", jobName);
//			resultMap.put("BATCHTYPE", batchType);
//			resultMap.put("DATE", date);
//			resultMap.put("STARTTIME", startTime);
//			resultMap.put("ENDTIME", endTime);
//			resultMap.put("STATE", state);
//			resultMap.put("ERRORNO", errorNo);
//			resultMap.put("ERRORINFO", errorInfo);
//			resultMap.put("ERRORTIME", errorTime);
//
//			return doTransfer(resultMap);
//		}
//
//	}
//
//	public static JSONObject sendJobResult2Monitor1(String jobName,
//			String batchType, String date, String startTime,
//			String endTime, String state, String errorNo, String errorInfo,
//			String errorTime) {
//
//			Map<String, Object> resultMap = new HashMap<String, Object>();
//			resultMap.put("JOBNAME", jobName);
//			resultMap.put("BATCHTYPE", batchType);
//			resultMap.put("DATE", date);
//			resultMap.put("STARTTIME", startTime);
//			resultMap.put("ENDTIME", endTime);
//			resultMap.put("STATE", state);
//			resultMap.put("ERRORNO", errorNo);
//			resultMap.put("ERRORINFO", errorInfo);
//			resultMap.put("ERRORTIME", errorTime);
//
//			return doTransfer(resultMap);
//	}
	
	public static Object getBean(String beanId) {
		WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
		return wac.getBean(beanId);
	}

	/**
	 * 获取营业日前一天
	 * @return yyyyMMdd
	 * @throws ParseException
	 */
	public static String getBussDateBefore() throws ParseException {
		return getBussDateBeforeLine().replaceAll("-", "");
	}

	/**
	 * 获取营业日前一天
	 * @return yyyy-MM-dd
	 * @throws ParseException
	 */
	public static String getBussDateBeforeLine() throws ParseException {
		String bussDateStr =DateUtil.convert(new Date(),"yyyy-MM-dd");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date bussDate = sdf.parse(bussDateStr);
		Calendar bussCal = Calendar.getInstance();
		bussCal.setTime(bussDate);
		bussCal.add(Calendar.DATE, -1);
		bussDate = bussCal.getTime();
		return sdf.format(bussDate);
	}

	/**
	 * 检查需要的数据文件是否存在。
	 * @param dir 数据文件所在目录
	 * @param regex OK文件名前缀，形如“XXXXXXXyyyyMMdd”
	 * @return 如果文件存在，则返回txt文件；不存在，则返回空指针。
	 * @throws InterruptedException 
	 * @throws ParseException 
	 * @throws Exception
	 */
	public static File[] checkImportDataFile(File dir, String regex) throws InterruptedException, ParseException {
		int times = Integer.valueOf(getProperty("dataFile.scan.times"));
		long interval = Long.valueOf(getProperty("dataFile.scan.interval"));
		String latestTimeStr = getProperty("dataFile.scan.latestTime");
		Date latestTime = transferLatestTime(latestTimeStr);
		File[] okFiles = null;
		File[] txtFiles = null;
		int count = 0;

		while(count <= times){
			okFiles = dir.listFiles(new BatchFilenameFilter(regex));
			if (null == okFiles || okFiles.length == 0) {
				if (count <= times && new Date().before(latestTime)) {
					Thread.sleep(interval);
				} else {
					return okFiles;
				}
			} else {
				txtFiles = new File[okFiles.length];
				String fileName = "";
				for (int i = 0; i < okFiles.length; i++) {
					fileName = okFiles[i].getAbsolutePath().replaceAll(
							BatchConstant.OK_FILE_SUFFIX, "");
					txtFiles[i] = new File(fileName);
				}
				return txtFiles;
			}
			count++;
		}

		return txtFiles;
	}

	private static Date transferLatestTime(String latestTimeStr) throws ParseException {
		// TODO Auto-generated method stub
		Date latestTime = null;
		Date now = new Date();
		String nowStr = new SimpleDateFormat("HHmmss").format(now);
		if (nowStr.compareTo(latestTimeStr) >= 0) {
			// 第二天
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, 1);
			nowStr = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
			latestTime = new SimpleDateFormat("yyyyMMddHHmmss").parse(
					nowStr + latestTimeStr);
		} else {
			nowStr = new SimpleDateFormat("yyyyMMdd").format(now);
			latestTime = new SimpleDateFormat("yyyyMMddHHmmss").parse(
					nowStr + latestTimeStr);
		}

		return latestTime;
	}

	public static ShardingContext getShardingContext() {
		if (null == shardingContext) {
			ShardingContexts shardingContexts = new ShardingContexts(
					null, null, 1, null, new HashMap<Integer, String>());
			shardingContext = new ShardingContext(shardingContexts, 0);
		}

		return shardingContext;
	}

	/**
	 * 当前时间是否在配置文件的区间里面
	 * @return
	 */
	public static boolean isLatestTime() {
		Date now = new Date();
		String nowStr = new SimpleDateFormat("HHmmss").format(now);

		String latestTime = getProperty("dataFile.scan.latestTime");
		String latestEnd = getProperty("dataFile.scan.latestEnd");
		if (nowStr.compareTo(latestTime) >= 0
				&& nowStr.compareTo(latestEnd) <= 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 生成end文件
	 * @param src 数据文件
	 * @return
	 */
	public static boolean createEndFile() {
		boolean result = false;

		File dir = new File(
				getProperty(BatchConstant.SEND_FILE_DIR_KEY)
				+ getProperty("endFile.fileDir"));
		if (!dir.exists()) {
			dir.mkdirs();
		}

		String timeStr = null;
		try {
			timeStr = getBussDateBefore();
		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
		}
		String fileNamePrefix = getProperty("endFile.fileName") + timeStr + "_";
		// file name xxxxxx_001.txt
		File endFile = new File(dir, fileNamePrefix
						+ BatchUtil.int2Str(1, 3) + BatchConstant.END_FILE_SUFFIX);
		File okFile = new File(
				endFile.getAbsolutePath() + BatchConstant.OK_FILE_SUFFIX);
		FileOutputStream fos = null;
		OutputStreamWriter out = null;
		FileOutputStream fos1 = null;
		OutputStreamWriter out1 = null;
		try {
			fos = new FileOutputStream(endFile);
			out = new OutputStreamWriter(fos, "UTF-8");
			fos1 = new FileOutputStream(okFile);
			out1 = new OutputStreamWriter(fos1, "UTF-8");
			result = true;
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
		}/* catch (IOException e) {
			logger.error(e.getMessage(), e);
		}*/ finally {
			if (null != out) {
				try {
					out.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
			if (null != fos) {
				try {
					fos.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
			if (null != out1) {
				try {
					out1.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
			if (null != fos1) {
				try {
					fos1.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}

		return result;
	}
}
