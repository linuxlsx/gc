package org.linuxlsx.gc.copy;

/**
 * GC 复制算法使用的堆
 *
 * @author linuxlsx
 * @date 2019-04-06
 */
public class CopyHeap {

    /**
     * from 区域的起始位置。在完成一个copying后，
     * fromStart 和 toStart的值会进行互换
     */
    public int fromStart;

    /**
     * to 区域的起始位置。
     */
    public int toStart;

    /**
     * 表明当前空闲内存的起始位置。
     */
    public int free;

    /**
     * 表明实际可用的大小
     */
    public int size;


    static final int MAXIMUM_CAPACITY = 1 << 30;

    public CopyHeap() {
    }

    /**
     * 因为Copy算法是连续分配的，所以不需要确定分配策略
     * @param size
     */
    public CopyHeap(int size) {
        int realSize = calcPowSize(size);
        this.size = realSize / 2;
        fromStart = 0;
        toStart = this.size;
        free = 0;
    }


    /**
     * 将数据Copy 到 TO 区块。简单的实现为增加空闲内存的起始位置
     * @param size
     */
    public int copyData(int size){

        int start = free;
        free += size;

        return start;
    }

    /**
     * 判断是否有足够的内存来满足对象分配
     * @param size
     * @return
     */
    public boolean hasEnoughMemory(int size){
        return free + size < (this.size + fromStart);
    }


    /**
     * 每次copying完成以后，互换fromStart 和 toStart
     */
    public void swap(){
        int tmp = fromStart;
        fromStart = toStart;
        toStart = tmp;
    }


    private static final int calcPowSize(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }
}
