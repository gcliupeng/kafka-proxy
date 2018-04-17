package org.kafka_proxy.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.kafka_proxy.config.GlobalConfig;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.clients.producer.Callback;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * @author liupeng@rong360.com
 */
public class KafkaApiBin {

	private static final Logger log = LoggerFactory.getLogger(KafkaApiBin.class);
	private static KafkaProducer<String, byte[]> producer;
	private static KafkaProducer<String, byte[]> ackProducer;

	public KafkaApiBin(){
		Properties props = new Properties();
        //收到leader的回复
        props.put("acks", "1");
        //超过两秒认为失败
		props.put("request.timeout.ms",2000);
		//获取内存和meta信息的最长等待时间
        props.put("max.block.ms",2000);
		props.put("retries",3);
		props.put("bootstrap.servers", GlobalConfig.kafka_server);
        props.put("client.id", "DemoProducer");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
 		props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        producer = new KafkaProducer<>(props);
        //收到replication回复
        props.put("acks", "all");
        ackProducer = new KafkaProducer<>(props);
	}

	
	public boolean send(String topic, Integer partition, byte[] value){
		try {
			long startTime = System.currentTimeMillis();
			producer.send(new ProducerRecord<>(topic,partition,null,value)).get();
            return true;
		}catch( InterruptedException| ExecutionException e){
            e.printStackTrace();
            return false;
            //log
			// need reopen the producer
		}
	}

	public boolean sendAcks(String topic, Integer partition, byte[] value){
		try {
			ackProducer.send(new ProducerRecord<>(topic,partition,null, value)).get();
			return true;
		}catch(InterruptedException | ExecutionException e){
			e.printStackTrace();
			return false;
		}
	}
}

