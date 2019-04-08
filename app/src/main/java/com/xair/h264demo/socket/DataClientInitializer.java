package com.xair.h264demo.socket;


import java.util.concurrent.TimeUnit;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Created by Administrator on 2016/2/4.
 */
public class DataClientInitializer extends ChannelInitializer {
    NioTcpClient client;

    public DataClientInitializer(NioTcpClient client) {
        this.client = client;
    }

    public void initChannel(Channel channel) {
        TcpClientHandle handle = new TcpClientHandle();
        channel.pipeline().addLast("idleStateHandler", new IdleStateHandler(15, 15,15, TimeUnit.SECONDS));
//        channel.pipeline().addLast("idleTimeoutHandler", new IdleClientHandler(this.client));
        channel.pipeline().addLast("handler", handle);
    }

}
