package com.xair.h264demo.tools;


/**
 * Created by pj on 2018/4/24.
 */
public class ByteArrCache {
    //用于存放打印返回信息
    private byte[] fanhuiBuffer ;
    //用于监控fanBuffer的初始偏移量
    private int offSet = 0;
    //用于反应当前应截取的位置
    private int cutPosition = 0;
    //设置存放消息数组的设定长度
    private int fanhuiBufferLen = 1024 ;

    public ByteArrCache(int fanhuiBufferLen) {
        this.fanhuiBufferLen = fanhuiBufferLen;
        this.fanhuiBuffer = new byte[this.fanhuiBufferLen];
    }

    public int getOffSet() {
        return offSet;
    }

    public void jointBuffer(byte[] buffer) {
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
    public byte[] getOneDataFromBuffer(int start, int len) {
        byte[] printByte = new byte[len];
        int position = start;
        System.arraycopy(fanhuiBuffer,position,printByte,0,printByte.length);
        cutPosition = len;
        return printByte;
    }

    //用于重新截取fanhuiBuffer的数据
    public void cutBuffer() {
        System.arraycopy(fanhuiBuffer,cutPosition,fanhuiBuffer,0,offSet - cutPosition);
        offSet = offSet - cutPosition;
        if(fanhuiBuffer.length > fanhuiBufferLen && offSet < fanhuiBufferLen/2){
            byte[] temp = new byte[offSet];
            System.arraycopy(fanhuiBuffer,0,temp,0,offSet);
            fanhuiBuffer = new byte[fanhuiBufferLen];
            System.arraycopy(temp,0,fanhuiBuffer,0,offSet);
        }
    }

    public int getCutpapperPosition(byte[] b){
        if (b.length < 2){
            return -1;
        }
        for (int i = 0; i < offSet; i++) {
            if (fanhuiBuffer[i] == b[1] && i > 0) {
                if(fanhuiBuffer[i-1] == b[0]){
                    return i - 1;
                }
            }
        }
        return -1;
    }

}
