package com.xair.h264demo.socket;


import java.util.concurrent.TimeUnit;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Created by Administrator on 2016/2/4.
 */
public class DataClientInitializer extends ChannelInitializer {


    public DataClientInitializer() {
    }

    public void initChannel(Channel channel) {
        TcpClientHandle handle = new TcpClientHandle();
        channel.pipeline().addLast("idleStateHandler", new IdleStateHandler(300000, 0,0, TimeUnit.MILLISECONDS));
        channel.pipeline().addLast("handler", handle);
    }

}
