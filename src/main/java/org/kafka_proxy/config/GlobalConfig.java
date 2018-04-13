package org.kafka_proxy.config;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class GlobalConfig {

	public static Integer thread_num = null;
	public static Integer listen_port = null;
	public static String kafka_server = null;
	public static String token = null;
	private static final Logger log = Logger.getLogger(GlobalConfig.class);
	public static void loadConfig(String args){
		
		String path =  System.getProperty("user.dir") + "/src/main/resources/" 
					+ args + "/message_bus.properties";         
		Properties prop = new Properties();   
		InputStream in;
		try {
			in = new BufferedInputStream(new FileInputStream(path));
			prop.load(in);
			thread_num = Integer.valueOf(prop.getProperty("messagebus.thread_num"));
			listen_port = Integer.valueOf(prop.getProperty("messagebus.listen_port"));
			token = prop.getProperty("messagebus.token");
			kafka_server = prop.getProperty("kafka.server");
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
	}
	
}
