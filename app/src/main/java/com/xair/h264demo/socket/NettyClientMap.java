package com.xair.h264demo.socket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2016/2/17.
 */
public class NettyClientMap {
    private static Map<String, NioTcpClient> mapkey = new ConcurrentHashMap<String, NioTcpClient>();

    public static void Add(NioTcpClient client) {
        mapkey.put(client.Name, client);
    }

    public static NioTcpClient GetChannel(String key) {
        return  mapkey.get(key);
    }

    public static void Remove(String key) {
        mapkey.remove(key);
    }
}
