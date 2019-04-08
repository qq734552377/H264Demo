package com.xair.h264demo.socket;

import com.xair.h264demo.tools.MyTools;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class IdleClientHandler extends SimpleChannelInboundHandler<Object> {

	private NioTcpClient nettyClient;

	/**
	 * @param nettyClient
	 */
	public IdleClientHandler(NioTcpClient nettyClient) {
		this.nettyClient = nettyClient;
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			String type = "";
			if (event.state() == IdleState.READER_IDLE) {
				type = "read idle";
			} else if (event.state() == IdleState.WRITER_IDLE) {
				type = "write idle";
			} else if (event.state() == IdleState.ALL_IDLE) {
				type = "all idle";
			}
			String id = ctx.channel().id().toString();
			MyTools.writeSimpleLogWithTime("IdleClientHandler.userEventTriggered() 的ID-->" + id  + "  -->" + type);
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}



	/**
	 * 处理断开重连
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		final EventLoop eventLoop = ctx.channel().eventLoop();
		eventLoop.schedule(new Runnable() {
			@Override
			public void run() {
				MyTools.writeSimpleLogWithTime("客户端尝试重新连接");
				nettyClient.connect();
			}
		} ,10L, TimeUnit.SECONDS);
		MyTools.writeSimpleLogWithTime("客户端的连接被关闭-->" + ctx.channel().id().toString());
		super.channelInactive(ctx);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

	}
}
