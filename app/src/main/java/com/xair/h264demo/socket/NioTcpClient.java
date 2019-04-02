package com.xair.h264demo.socket;


import com.xair.h264demo.tools.MyTools;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * Created by Administrator on 2016/2/4.
 */
public class NioTcpClient implements Runnable {

    public String Ip;

    private int Port;

    public String SSid;

    public String Password;

    private EventLoopGroup group;

    public ChannelFuture f;

    public static String Name = "client";

    private boolean mDispose;

    public int WaitChannel;

    public boolean Old;

    public boolean NoResult;

    public NioTcpClient() {

    }

    public NioTcpClient(String ip, int port, boolean old) {
        Ip = ip;
        Port = port;
        Old = old;
        group = new NioEventLoopGroup();
    }

    @Override
    public void run() {
        connect();
    }

    public void connect() {
        try {
//            synchronized (this) {
//                if (mDispose)
//                    return;

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            //TODO 可能需要扩容
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024*1024);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(new DataClientInitializer());
            f = bootstrap.connect(Ip, Port).sync();
            //等待链接关闭
            f.addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    ChannelFuture futureListener = (ChannelFuture) future;
                    final EventLoop eventLoop = futureListener.channel().eventLoop();
                    if (!futureListener.isSuccess()) {
                        MyTools.writeSimpleLogWithTime("Failed to connect to server, try connect after 10s");
                        futureListener.channel().eventLoop().schedule(new Runnable() {
                            @Override
                            public void run() {
                                connect();
                            }
                        }, 10, TimeUnit.SECONDS);
                    }
                }
            });
            try {
                f.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
                MyTools.writeSimpleLogWithTime("sync 抛出" + e.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            MyTools.writeSimpleLogWithTime("connect 抛出" + e.toString());
        } finally {

            Close();
        }
    }

    public void Close() {
        synchronized (this) {
            if (mDispose)
                return;
            group.shutdownGracefully();
            WaitChannel = 2;
        }
    }

    public void Dispose() {
        synchronized (this) {
            if (mDispose)
                return;
            mDispose = true;
            if (group == null)
                return;
            group.shutdownGracefully();
        }
    }

    public boolean Send(byte[] Data) {
        try {
            if (f == null)
                return false;
            if (!f.isSuccess())
                return false;
            Channel channel = f.channel();
            if (channel == null)
                return false;
            ByteBuf resp = Unpooled.copiedBuffer(Data);
            channel.writeAndFlush(resp);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public boolean ClietnStatus() {
        try {
            if (f == null)
                return false;
            if (!f.isSuccess())
                return false;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
