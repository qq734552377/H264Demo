package com.xair.h264demo.tools;

import android.util.Base64;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/2/16.
 */
public class Common {

    public static Object CheckFirstStatus = new Object();
    private static boolean  FirstStatus=false;

    public static String encode(byte[] bstr) {
        return Base64.encodeToString(bstr, Base64.NO_WRAP);
    }

    /**
     * 解码
     *
     * @param str
     * @return string
     */
    public static byte[] decode(String str) {
        byte[] bt = new byte[100];
        try {

            bt = Base64.decode(str, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bt;
    }


    public static String getPackageString(String cmd, String type, String number, int total, int serial, int len, String data) {

        return "@" + cmd + "," + type + "," + number + "," + total + "," + serial + "," + len + "," + data + "$";
    }


    //获取校验位
    public static byte getCheckByte(byte[] bArr) {
        byte b =  bArr[0];
        for (int i = 1; i < bArr.length; i++) {
            b = (byte) (b ^ bArr[i]);
        }
        return b;
    }

    //将数据封装成一条完整的协议
    public static byte[] pakageOneProtocol(byte[] res) {
        return addEndToPackageData(changeByte(addCheckByteToArray(res)));
    }

    //给数据添加上校验位
    public static byte[] addCheckByteToArray(byte[] res) {
        byte [] des = addByteLength(res, 1);
        des[des.length - 1] = getCheckByte(res);
        return des;
    }

    //将数据封装上尾部
    public static byte[] addEndToPackageData(byte[] send) {
        byte[] des = addByteLength(send, 1);
        des[des.length - 1] = (byte) 0x03;
        return des;
    }

    //将数据转义
    public static byte[] changeByte(byte[] send) {
        byte[] des = new byte[send.length];
        int desPoint = 0;
        for (int i = 0; i < send.length; i++) {
            if (i == 0 && send[i] == (byte) 0x02) {
                des[desPoint] = send[i];
                desPoint += 1;
            } else if (send[i] == (byte) 0x02) {
                des = addByteLength(des, 1);
                des[desPoint] = (byte) 0x1B;
                des[desPoint + 1] = (byte) 0xE7;
                desPoint += 2;
            } else if (send[i] == (byte) 0x03) {
                des = addByteLength(des, 1);
                des[desPoint] = (byte) 0x1B;
                des[desPoint + 1] = (byte) 0xE8;
                desPoint += 2;
            } else if (send[i] == (byte) 0x1B) {
                des = addByteLength(des, 1);
                des[desPoint] = (byte) 0x1B;
                des[desPoint + 1] = (byte) 0x00;
                desPoint += 2;
            } else {
                des[desPoint] = send[i];
                desPoint += 1;
            }
        }

        return des;
    }

    //将数据转义回来
    public static byte[] changeByteBack(byte[] res) {
        List<Byte> desList = new ArrayList();
        int i = 0;
        while (i < res.length) {
            if (res[i] == (byte) 0x1B) {
                if (i + 1 < res.length) {
                    switch (res[i + 1]) {
                        case (byte) 0xE7:
                            desList.add( new Byte((byte) 0x02));
                            break;
                        case (byte) 0xE8:
                            desList.add( new Byte((byte) 0x03));
                            break;
                        case (byte) 0x00:
                            desList.add( new Byte((byte) 0x1B));
                            break;
                    }
                    i += 2;
                }else{
                    desList.add(new Byte(res[i]));
                    i += 1;
                }
            } else {
                desList.add(new Byte(res[i]));
                i += 1;
            }
        }
        byte [] des = new byte[desList.size()];
        for (int j = 0; j < desList.size(); j++) {
            des[j] = desList.get(j).byteValue();
        }

        return des;
    }

    //扩容数组
    public static byte[] addByteLength(byte[] bArr, int length) {
        int newLength = bArr.length + length;
        byte[] newBArr = new byte[newLength];
        System.arraycopy(bArr, 0, newBArr, 0, bArr.length);
        return newBArr;
    }

    //发送App
    public static byte[] GetFormat(String cmd, int total, int current, String[] data) {
        StringBuffer sb = new StringBuffer();
        sb.append("@");
        sb.append(cmd);
        sb.append("," + total);
        sb.append("," + current);
        for (int i = 0; i < data.length; i++) {
            sb.append("," + data[i]);
            if (i + 1 >= data.length) {
                sb.append("$");
            }
        }
        return sb.toString().getBytes();
    }
}
