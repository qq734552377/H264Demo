package com.xair.h264demo.socket;


import com.xair.h264demo.MainActivity;
import com.xair.h264demo.tools.ByteArrCache;
import com.xair.h264demo.tools.Common;
import com.xair.h264demo.tools.MyTools;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

/**
 * Created by Administrator on 2016/2/4.
 */
public class TcpClientHandle extends ChannelInboundHandlerAdapter {
    ByteArrCache cache ;
    //用于存放打印返回信息
    private byte[] fanhuiBuffer ;
    //用于监控fanBuffer的初始偏移量
    private int offSet = 0;
    //用于反应当前应截取的位置
    private int cutPosition = 0;
    //设置存放消息数组的设定长度
    private int fanhuiBufferLen = 1024 * 1024;
    public TcpClientHandle() {
        fanhuiBuffer = new byte[fanhuiBufferLen];
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接来了:"+ctx.channel().id());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        try {
            ByteBuf buff = (ByteBuf) msg;
            int len = buff.readableBytes();
            byte[] buffer = new byte[len];
            buff.readBytes(buffer);
            // 处理server过来的数据
//            analyzeDataWithString(buffer);
            analyzeDataWithByte(buffer);
            ReferenceCountUtil.release(msg);
        } catch (Exception e) {
            MyTools.writeSimpleLogWithTime("channelRead异常停止--》" + e.toString());
            ctx.close();
        }
    }

    private void analyzeDataWithByte(byte[] buffer) {
        //添加串口数据
        jointBuffer(buffer);
//        MyTools.writeSimpleLogWithTime(MyTools.printHexString(buffer));
        while (offSet > 0) {
            int startIndex = getIndexByByte((byte) 0x02);
            if (startIndex <= -1) {
                break;
            }
            int endIndex = getIndexByByte((byte) 0x03);
            if (endIndex <= -1) {
                break;
            }
            if (endIndex < startIndex) {
                cutPosition = endIndex + 1;
                cutBuffer();
                continue;
            }
            byte[] printBuffer = getPrintbyte(startIndex , endIndex);
            handleProtocol(printBuffer);
            cutBuffer();
        }
    }

    private void handleProtocol(byte[] printBuffer) {
        MainActivity.h264Queue.addItem(Common.changeByteBack(printBuffer));
    }

    private int getIndexByByte( byte b) {
        for (int i = 0; i < offSet; i++) {
            if (fanhuiBuffer[i] == b) {
                return i;
            }
        }
        return -1;
    }

    private void jointBuffer(byte[] buffer) {
        if (offSet + buffer.length  > fanhuiBuffer.length) {
            // 扩容 为原来的两倍
            byte[] temp = new byte[fanhuiBuffer.length];
            System.arraycopy(fanhuiBuffer,0,temp,0,fanhuiBuffer.length);
            fanhuiBuffer = new byte[fanhuiBuffer.length * 2];
            System.arraycopy(temp,0,fanhuiBuffer,0,temp.length);
        }
        System.arraycopy(buffer,0,fanhuiBuffer,offSet,buffer.length);
        offSet = offSet + buffer.length;
    }



    //返回一个byte对象 用于发送消息 该数组不会包含 头和尾 即0x02和0x03
    private byte[] getPrintbyte(int start, int end) {
        byte[] printByte = new byte[end - start - 1];
        int position = start + 1;
        System.arraycopy(fanhuiBuffer,position,printByte,0,printByte.length);
        cutPosition = end + 1;
        return printByte;
    }

    //用于重新截取fanhuiBuffer的数据
    private void cutBuffer() {
        System.arraycopy(fanhuiBuffer,cutPosition,fanhuiBuffer,0,offSet - cutPosition);
        offSet = offSet - cutPosition;
        if(fanhuiBuffer.length > fanhuiBufferLen && offSet < fanhuiBufferLen/2){
            byte[] temp = new byte[offSet];
            System.arraycopy(fanhuiBuffer,0,temp,0,offSet);
            fanhuiBuffer = new byte[fanhuiBufferLen];
            System.arraycopy(temp,0,fanhuiBuffer,0,offSet);
        }
    }


























    private StringBuffer sBuffer = new StringBuffer();

    public void analyzeDataWithString(byte[] buffer) throws Exception {
        sBuffer.append(new String(buffer));
        int offset = 0;
        while (sBuffer.length() > offset) {
            int startIndex = sBuffer.indexOf("@", offset);
            if (startIndex == -1)
                break;

            int endIndex = sBuffer.indexOf("$", startIndex);
            if (endIndex == -1)
                break;
            int len = endIndex + 1;


            String value = sBuffer.substring(startIndex + 1, len - 1);
            handleOneData(value);
            offset = len;
        }
        sBuffer.delete(0, offset);
    }

    private void handleOneData(String value) {
        String[] items = value.split(",");
        switch (items[0]){
            case "h264_data":
                byte[] h264Data = MyTools.decode(items[1]);
                MainActivity.h264Queue.addItem(h264Data);
                break;
            default:
                break;
        }
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (!(evt instanceof IdleStateEvent)) {
            return;
        }
        IdleStateEvent event = (IdleStateEvent) evt;
        if(event.state() == IdleState.READER_IDLE) {
            ctx.close();
        }
    }
}
