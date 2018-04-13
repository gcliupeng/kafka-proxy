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

		get("/test", (req, res) -> {
			if(isStopping){
				halt(504, "the server is stoping!");
			}
			//req.attribute("startTime", System.currentTimeMillis());
			Ret resRet = new Ret();
			kafkaApi.send("test","hello world");
			resRet.setSucCode();
			resRet.setMsg("send ok");
			//resRet.setFailCode();
			//resRet.setMsg("invalid param queue or payload!");
			JSONObject obj = JSONObject.fromObject(resRet);
			return obj.toString();
			//halt(200, obj.toString());
			
			// String queue = req.queryParams("queue");
			// String payload = req.queryParams("payload");
			// String deliveryMode = req.queryParams("delivery_mode");
			// if (deliveryMode == null || "".equals(deliveryMode)) {
			// 	deliveryMode = "2";
			// }
			// if (!deliveryMode.equals("1") && !deliveryMode.equals("2")) {
			// 	resRet.setFailCode();
			// 	resRet.setMsg("invalid param delivery_mode,must be 1 or 2!");
			// 	JSONObject obj = JSONObject.fromObject(resRet);
			// 	halt(200, obj.toString());
			// }
			// if (queue == null || payload == null) {
			// 	resRet.setFailCode();
			// 	resRet.setMsg("invalid param queue or payload!");
			// 	JSONObject obj = JSONObject.fromObject(resRet);
			// 	halt(200, obj.toString());
			// }
			// HashMap<String, String> reqMap = new HashMap<String, String>();
			// for (String str : req.queryParams()) {
			// 	if (!str.equals("token")) {
			// 		reqMap.put(str, req.queryParams(str));
			// 	}
			// }
			// String token = CommonUtil.stringMD5(CommonUtil.getSortParams(reqMap) + GlobalConfig.token);
			// String reqToken = req.queryParams("token");
			// if (reqToken == null || !reqToken.equals(token)) {
			// 	resRet.setFailCode();
			// 	resRet.setMsg("the wrong token,req=" + reqToken);
			// 	log.warn("发送失败,before md5:{},req={},expect={}",
			// 			CommonUtil.getSortParams(reqMap),reqToken,token);
			// 	JSONObject obj = JSONObject.fromObject(resRet);
			// 	halt(200, obj.toString());
			// }
		});

		// get("/"+GlobalConfig.rabbit_vhost +"/monitor", (req, res) -> {
		// 	if(isStopping){
		// 		log.warn("发送失败,server is stopping");
		// 		halt(504, "the server is stoping!");
		// 	}
		// 	long startTime = System.currentTimeMillis();
		// 	Ret resRet = new Ret();
		// 	String queue = GlobalConfig.monitor_queue;
		// 	String payload = "monitor_heartbeat:" + startTime;
		// 	String deliveryMode = "2";
		// 	boolean ret = new RabbitAPI().publishToQueue(queue, payload, Integer.valueOf(deliveryMode));
		// 	if (ret) {
		// 		resRet.setSucCode();
		// 	} else {
		// 		resRet.setFailCode();
		// 		resRet.setMsg("发送失败");
		// 		log.warn("发送失败,queue:{},payload:{}",queue,payload);
		// 	}
		// 	long spend = System.currentTimeMillis() - startTime;
		// 	log.info("amqp_queue_pub:[queue]:{}[payload]:{}[delivery_mode]:{}[spend]:{}ms[ret]:{}", queue, payload,
		// 			deliveryMode, spend, resRet.getCode());
		// 	JSONObject obj = JSONObject.fromObject(resRet);
		// 	return obj.toString();
		// });

		// post("/" + GlobalConfig.rabbit_vhost + "/amqp/queue/pub", (req, res) -> {
		// 	Ret resRet = new Ret();
		// 	String queue = req.queryParams("queue");
		// 	String payload = req.queryParams("payload");
		// 	String deliveryMode = req.queryParams("delivery_mode");
		// 	if (deliveryMode == null || "".equals(deliveryMode)) {
		// 		deliveryMode = "2";
		// 	}
		// 	boolean ret = false;
		// 	int retry = 0;
		// 	for (; retry < 3; retry++) {
		// 		ret = new RabbitAPI().publishToQueue(queue, payload, Integer.valueOf(deliveryMode));
		// 		if (ret) {
		// 			break;
		// 		} else {
		// 			Thread.sleep(100 * (retry+1));
		// 		}
		// 	}

		// 	if (ret) {
		// 		resRet.setSucCode();
		// 		if (retry > 0) {
		// 			resRet.setMsg("发送成功，retry=" + retry);
		// 		}
		// 	} else {
		// 		resRet.setFailCode();
		// 		resRet.setMsg("发送失败,retry=" + retry);
		// 	}
		// 	long spend = System.currentTimeMillis() - (Long) req.attribute("startTime");
		// 	log.info("amqp_queue_pub:[queue]:{}[payload]:{}[delivery_mode]:{}[spend]:{}ms[ret]:{}", queue, payload,
		// 			deliveryMode, spend, resRet.getCode());
		// 	JSONObject obj = JSONObject.fromObject(resRet);
		// 	return obj.toString();
		// });

		// post("/" + GlobalConfig.rabbit_vhost +"/amqp/exchange/pub", (req, res) -> {
		// 	Ret resRet = new Ret();
		// 	String queue = req.queryParams("queue");
		// 	String payload = req.queryParams("payload");
		// 	String deliveryMode = req.queryParams("delivery_mode");
		// 	String routingkey = req.queryParams("routingkey");
		// 	if (deliveryMode == null || "".equals(deliveryMode)) {
		// 		deliveryMode = "2";
		// 	}
		// 	if (routingkey == null) {
		// 		routingkey = "";
		// 	}
		// 	int retry = 0;
		// 	boolean ret = false;
		// 	for (; retry < 3; retry++) {
		// 		ret = new RabbitAPI().publishToExchange(queue, routingkey, payload, Integer.valueOf(deliveryMode));
		// 		if (ret) {
		// 			break;
		// 		} else {
		// 			retry++;
		// 			Thread.sleep(100 * (retry+1));
		// 		}
		// 	}

		// 	if (ret) {
		// 		resRet.setSucCode();
		// 		if (retry > 0) {
		// 			resRet.setMsg("发送成功，retry=" + retry);
		// 		}
		// 	} else {
		// 		resRet.setFailCode();
		// 		resRet.setMsg("发送失败,retry=" + retry);
		// 	}
		// 	long spend = System.currentTimeMillis() - (Long) req.attribute("startTime");
		// 	log.info("amqp_exchange_pub:[queue]:{}[routingkey]:{}[payload]:{}[delivery_mode]:{}[spend]:{}ms[ret]:{}",
		// 			queue, routingkey, payload, deliveryMode, spend, resRet.getCode());
		// 	JSONObject obj = JSONObject.fromObject(resRet);
		// 	return obj.toString();
		// });

		get("/index", (req, res) -> {
			return "under construction!";
		});
	}

}
