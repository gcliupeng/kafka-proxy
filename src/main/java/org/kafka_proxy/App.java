package org.kafka_proxy;

import static spark.Spark.*;

import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.kafka_proxy.config.GlobalConfig;
// import org.apache.kafka.clients.producer.KafkaProducer;
// import org.apache.kafka.clients.producer.ProducerRecord;
import org.kafka_proxy.kafka.KafkaApi;
import org.kafka_proxy.util.CommonUtil;
import org.kafka_proxy.vo.Ret;

import net.sf.json.JSONObject;

/**
 * @author kafka_proxy@rong360.com
 *
 */
public class App {
	private static final Logger log = LoggerFactory.getLogger(App.class);
	private static boolean isStopping = false;
	private static KafkaApi kafkaApi;
	public static void main(String[] args) {
		
		//平滑停止，sleep足够长时间，尽量保证消息送达
		Runtime.getRuntime().addShutdownHook(new Thread(){
			
			public void run(){
				log.info("receive signal to shutdown!");
				isStopping =true;
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
				}
			}
		});
		
		if(args.length != 1){
			log.error("invald params");
			return;
		}
		GlobalConfig.loadConfig(args[0]);
		threadPool(GlobalConfig.thread_num);
		port(GlobalConfig.listen_port);
		initExceptionHandler((e) -> {
			log.error("init failed", e);
			System.exit(100);
		});
		kafkaApi = new KafkaApi();

		String value = "hello world";
        for(int i=0;i<=2048;i++)
            value=value+"hello world";
        String value_2 = value;
        for(int i=0;i<=10;i++)
        	value_2=value_2+value;
        final String value2= value_2;
		/*
		before("/", (req, res) ->{
			if(isStopping){
				halt(504, "the server is stoping!");
			}
			req.attribute("startTime", System.currentTimeMillis());
			Ret resRet = new Ret();
			String topic = req.queryParams("topic");
			String message = req.queryParams("message");
			Integer partition = new Interger(req.queryParams("partition");
			String deliveryMode = req.queryParams("delivery_mode");
			if (deliveryMode == null || "".equals(deliveryMode)) {
				deliveryMode = "1";
			}
			if (!deliveryMode.equals("1") && !deliveryMode.equals("2")) {
				resRet.setFailCode();
				resRet.setMsg("invalid param delivery_mode,must be 1 or 2!");
				JSONObject obj = JSONObject.fromObject(resRet);
				halt(200, obj.toString());
			}
			if (topic == null || partition == null || message ==null) {
				resRet.setFailCode();
				resRet.setMsg("invalid param topic or partition!");
				JSONObject obj = JSONObject.fromObject(resRet);
				halt(200, obj.toString());
			}
			HashMap<String, String> reqMap = new HashMap<String, String>();
			for (String str : req.queryParams()) {
				if (!str.equals("token")) {
					reqMap.put(str, req.queryParams(str));
				}
			}
			String token = CommonUtil.stringMD5(CommonUtil.getSortParams(reqMap) + GlobalConfig.token);
			String reqToken = req.queryParams("token");
			if (reqToken == null || !reqToken.equals(token)) {
				resRet.setFailCode();
				resRet.setMsg("the wrong token,req=" + reqToken);
				log.warn("发送失败,before md5:{},req={},expect={}",
						CommonUtil.getSortParams(reqMap),reqToken,token);
				JSONObject obj = JSONObject.fromObject(resRet);
				halt(200, obj.toString());
			}
		});
		*/

		get("/send",(req,res)->{

			if(isStopping){
				log.warn("发送失败,server is stopping");
				halt(504, "the server is stoping!");
			}
			long startTime = System.currentTimeMillis();
			Ret resRet = new Ret();
			String topic = req.queryParams("topic");
			String message = req.queryParams("message");
			Integer partition = new Interger(req.queryParams("partition");   
			String deliveryMode = req.queryParams("delivery_mode");
			if (deliveryMode == null || "".equals(deliveryMode)) {
				deliveryMode = "1";
			}
			boolean ret;
			if(deliveryMode == "1"){
				ret = kafkaApi.send(topic,partition,message);
			}else{
				ret = kafkaApi.sendAcks(topic,partition,message);
			}
			if (ret) {
				resRet.setSucCode();
			} else {
				resRet.setFailCode();
				resRet.setMsg("发送失败");
				log.warn("发送失败,topic:{},partition:{}",topic,partition);
			}
			long spend = System.currentTimeMillis() - startTime;
			log.info("amqp_queue_pub:[queue]:{}[payload]:{}[delivery_mode]:{}[spend]:{}ms[ret]:{}", queue, payload,
					deliveryMode, spend, resRet.getCode());
			JSONObject obj = JSONObject.fromObject(resRet);
			return obj.toString();
		});

		get("/testSmall", (req, res) -> {
			if(isStopping){
				halt(504, "the server is stoping!");
			}
			Ret resRet = new Ret();
			kafkaApi.send("test",1,"hello world");
			resRet.setSucCode();
			resRet.setMsg("send ok");
			JSONObject obj = JSONObject.fromObject(resRet);
			return obj.toString();
		});

		get("/testLarge", (req, res) -> {
			if(isStopping){
				halt(504, "the server is stoping!");
			}
			Ret resRet = new Ret();
			kafkaApi.send("test",1,value2);
			resRet.setSucCode();
			resRet.setMsg("send ok");
			JSONObject obj = JSONObject.fromObject(resRet);
			return obj.toString();
		});
		get("/index", (req, res) -> {
			return "under construction!";
		});
	}

}
