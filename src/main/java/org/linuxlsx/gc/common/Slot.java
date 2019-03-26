package org.linuxlsx.gc.common;

/**
 * 表示一个内存的区块
 *
 * @author linuxlsx
 * @date 2017/12/28
 */
public class Slot {

    public Slot(int start, int size) {
        this.start = start;
        this.size = size;
    }

    public int start;
    public int size;

    @Override
    public String toString() {
        return "Slot{" +
                "start=" + start +
                ", size=" + size +
                '}';
    }
}
