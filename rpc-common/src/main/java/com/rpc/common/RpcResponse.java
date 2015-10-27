package com.rpc.common;

/**
 * Rpc 请求响应类
 * @author bailu-ds
 *
 */
public class RpcResponse {
	/** 请求ID */
	private String requestId;
	
	/** 处理异常 */
	private Throwable error;
	
	/** 返回结果 */
	private Object result;
	
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public Throwable getError() {
		return error;
	}
	public void setError(Throwable error) {
		this.error = error;
	}
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
	
	public boolean isError() {
		if (null != error) {
			return true;
		} else {
			return false;
		}
	}
}
