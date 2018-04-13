package org.kafka_proxy.vo;

public class Ret {

	private Integer code ;
	private String msg;
	static final Integer RESPONSE_SUC = 0;
	static final Integer RESPONSE_FAIL = 1;
	
	public void setSucCode(){
		this.code = RESPONSE_SUC;
	}
	public void setFailCode(){
		this.code = RESPONSE_FAIL;
	}
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
