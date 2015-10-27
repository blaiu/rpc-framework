package com.rpc.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 编码类 继承于 io.netty.buffer.ByteBuf.MessageToByteEncoder
 * @author bailu-ds
 *
 */
@SuppressWarnings("rawtypes")
public class RpcEncoder extends MessageToByteEncoder {

	/** 需要编码的Class */
	private Class<?> genericClass;
	
	public RpcEncoder(Class<?> generiClass) {
		this.genericClass = generiClass;
	}
	
	/**
	 * 将Class 转换成字节码<br/>
	 * ctx: 通信频道<br/>
	 * in:  对象输入<br/>
	 * out: 字节输出
	 */
	@Override
	protected void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
		if (genericClass.isInstance(in)) {
			//序列化对象
			byte[] data = SerializationUtil.serialize(in);
			out.writeInt(data.length);
			out.writeBytes(data);
		}
	}

}
