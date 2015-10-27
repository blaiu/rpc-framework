package com.rpc.common;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 解码类 继承于  io.netty.handler.codec.ByteToMessageDecoder
 * @author bailu-ds
 *
 */
public class RpcDecoder extends ByteToMessageDecoder {

	/** 用于转换对象的 Class */
	private Class<?> genericClass;
	
	public RpcDecoder(Class<?> genericClass) {
		this.genericClass = genericClass;
	}
	
	/**
	 * 将字节转换成对象<br/>
	 * ctx: 通信频道<br/>
	 * in:  通信字节码输入<br/>
	 * out: 对象输出
	 */
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() < 4) {
			return;
		}
		in.markReaderIndex();
		
		int dataLength = in.readInt();
		if (dataLength < 0) {
			ctx.close();
		}
		
		if(in.readableBytes() < dataLength) {
			in.resetReaderIndex();
			return;
		}
		
		byte[] data = new byte[dataLength];
		in.readBytes(data);
		
		//烦序列化对象
		Object obj = SerializationUtil.deserialize(data, genericClass);
		out.add(obj);
	}

}
