package org.kafka_proxy.config;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class GlobalConfig {

	public static Integer thread_num = null;
	public static Integer listen_port = null;
	public static String kafka_host = null;
	public static Integer kafka_port = null;
	//public static String rabbit_vhost = null;
	//public static String rabbit_username = null;
	//public static String rabbit_password = null;
	//public static String rabbit_exchangename = null;
	public static String monitor_queue = null;
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
			kafka_host = prop.getProperty("kafka.host");
			kafka_port = Integer.valueOf(prop.getProperty("kafka.port"));
			//rabbit_vhost = prop.getProperty("rabbitmq.vhost");
			//rabbit_username = prop.getProperty("rabbitmq.username");
			//rabbit_password = prop.getProperty("rabbitmq.password");
			monitor_queue = prop.getProperty("messagebus.monitor_queue");
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
	}
	
}
