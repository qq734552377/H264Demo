package com.xair.h264demo.queue;

import com.xair.h264demo.MainActivity;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by pj on 2019/3/27.
 */
public class H264Queue {
    public int queuesize = 50;
    public static final byte[] cut_paper_byte = {0x1D,0x56};
    public ArrayBlockingQueue<h264data> h264Queue = new ArrayBlockingQueue<h264data>(queuesize);
    public MainActivity mainActivity;
    public static boolean IsRun = true;


    public H264Queue(MainActivity mainActivity, int queuesize) {
        this.queuesize = queuesize;
        this.mainActivity = mainActivity;
        new Thread(new Runnable() {
            @Override
            public void run() {
                continueRun();
            }
        }).start();

    }

    private void continueRun() {
        while (IsRun) {
            myRun();
        }
    }

    private void myRun() {
        try {
            h264data one = getItem();
            if (one != null) {
                mainActivity.onFrame(one.data, 0, one.data.length);
//                    Thread.sleep(2);
            } else {
                Thread.sleep(2);
            }
        } catch (Exception e) {

        }

    }

    public void addItem(byte[] data) {
        synchronized (this) {
            putData(data);
        }
    }

    public h264data getItem(){
        synchronized (this) {
            h264data one = h264Queue.poll();
            return one;
        }
    }

    public void putData(byte[] buffer) {
        if (h264Queue.size() >= queuesize) {
            h264Queue.poll();
        }
        h264data data = new h264data();
        data.data = buffer;
        h264Queue.add(data);
    }


}
