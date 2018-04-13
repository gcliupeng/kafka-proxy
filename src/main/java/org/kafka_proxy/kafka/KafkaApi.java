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
public class KafkaApi {

	private static final Logger log = LoggerFactory.getLogger(KafkaApi.class);
	private static KafkaProducer<String, String> producer;
	private static KafkaProducer<String, String> ackProducer;

	public KafkaApi(){
		Properties props = new Properties();
        props.put("acks", "1");
		props.put("request.timeout.ms",2000);
		props.put("max.block.ms",2000);
		props.put("retries",3);
		props.put("bootstrap.servers", GlobalConfig.kafka_host + ":" + GlobalConfig.kafka_port);
        props.put("client.id", "DemoProducer");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
 		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(props);
        props.put("acks", "all");
        ackProducer = new KafkaProducer<>(props);
	}

	//没有重试
	public void send(String topic, String value){
		try {
			long startTime = System.currentTimeMillis();
			producer.send(new ProducerRecord<>(topic,value)).get();
		}catch( InterruptedException| ExecutionException e){
			e.printStackTrace();
			// need reopen the producer
		}
	}

	public void sendAcks(String topic, String value){
		try {
			ackProducer.send(new ProducerRecord<>(topic,value)).get();
		}catch(InterruptedException | ExecutionException e){
			e.printStackTrace();
		}
	}
}

class DemoCallBack implements Callback {

    private final long startTime;
    private final String topic;
    private final String message;

    public DemoCallBack(long startTime, String topic, String message) {
        this.startTime = startTime;
        this.topic = topic;
        this.message = message;
    }

    /**
     * A callback method the user can implement to provide asynchronous handling of request completion. This method will
     * be called when the record sent to the server has been acknowledged. Exactly one of the arguments will be
     * non-null.
     *
     * @param metadata  The metadata for the record that was sent (i.e. the partition and offset). Null if an error
     *                  occurred.
     * @param exception The exception thrown during processing of this record. Null if no error occurred.
     */
    public void onCompletion(RecordMetadata metadata, Exception exception) {
        long elapsedTime = System.currentTimeMillis() - startTime;
        if (metadata != null) {
            // System.out.println(
            //     "message(" + topic + ", " + message + ") sent to partition(" + metadata.partition() +
            //         "), " +
            //         "offset(" + metadata.offset() + ") in " + elapsedTime + " ms");
        } else {
            exception.printStackTrace();
            System.out.println("topic :"+topic+", message:"+message);
        }
    }
}
