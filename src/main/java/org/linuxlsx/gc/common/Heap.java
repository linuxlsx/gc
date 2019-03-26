package org.linuxlsx.gc.common;


import java.util.Iterator;
import java.util.LinkedList;

/**
 * 简化的堆，可以指定堆大小和对象的分配策略。
 * 这个堆只有两个功能
 * <ul>
 * <li>返回一个指定大小的区块</li>
 * <li>回收一个区块，并将其加入到空闲列表</li>
 * </ul>
 *
 * @author linuxlsx
 * @date 2017/12/27
 */
public class Heap {

    /**
     * 整个堆得大小
     */
    protected int size;

    /**
     * 整个堆已经分配的大小
     */
    protected int allocatedSize;

    /**
     * 堆得分配策略
     */
    protected int fitStrategy;

    public static final int FIRST_FIT_STRATEGY = 1;
    public static final int BEST_FIT_STRATEGY = 2;
    public static final int WORST_FIT_STRATEGY = 3;

    /**
     * 空闲Slot的列表
     */
    protected LinkedList<Slot> emptyList = new LinkedList<Slot>();

    public Heap() {
    }

    public Heap(int size, int fitStrategy) {
        this.size = size;
        this.fitStrategy = fitStrategy;
        //初始化的时候将整个堆当做一个Slot
        emptyList.add(new Slot(0, size));
    }

    /**
     * 从堆中申请一个指定大小的可用区块
     *
     * @param size 对象的大小
     * @return
     */
    public Slot applySlot(int size) {

        switch (fitStrategy) {
            case FIRST_FIT_STRATEGY:
                return firstFit(size);
            case BEST_FIT_STRATEGY:
                return bestFit(size);
            case WORST_FIT_STRATEGY:
                return worstFit(size);
            default:
                return firstFit(size);
        }

    }

    /**
     * 释放内存
     *
     * @param releaseSlot
     */
    public void release(Slot releaseSlot) {

        Slot last = emptyList.peekLast();

        if (last != null && last.start + size == (releaseSlot.start)) {
            last.size += releaseSlot.size;
        } else if (last != null && releaseSlot.start + size == last.start) {
            last.start = releaseSlot.start;
            last.size += releaseSlot.size;
        } else {
            Slot slot = new Slot(releaseSlot.start, releaseSlot.size);

            emptyList.add(slot);
        }

        allocatedSize -= releaseSlot.size;
    }


    /**
     * 找到第一个能够满足分配的Slot
     *
     * @param size
     * @return
     */
    private Slot firstFit(int size) {
        Iterator<Slot> iterator = emptyList.iterator();

        while (iterator.hasNext()) {

            Slot slot = iterator.next();

            if (slot.size >= size) {
                return alloc(size, slot);
            }
        }

        return null;
    }

    /**
     * 找到满足分配的最小Slot
     *
     * @param size
     * @return
     */
    private Slot bestFit(int size) {

        Iterator<Slot> iterator = emptyList.iterator();

        Slot fit = null;

        while (iterator.hasNext()) {

            Slot slot = iterator.next();
            if (slot.size > size) {
                if (fit == null || slot.size < fit.size) {
                    fit = slot;
                }
            }
        }

        if (fit != null) {
            return alloc(size, fit);
        }

        return null;
    }

    /**
     * 找到满足分配的最大Slot，使得余下的Slot 最大
     *
     * @param size
     * @return
     */
    private Slot worstFit(int size) {

        Iterator<Slot> iterator = emptyList.iterator();

        Slot fit = null;
        while (iterator.hasNext()) {

            Slot slot = iterator.next();

            if (slot.size > size) {
                if (fit == null || slot.size > fit.size) {
                    fit = slot;
                }
            }
        }

        if (fit != null) {
            return alloc(size, fit);
        }

        return null;
    }

    /**
     * 返回指定大小的区块，如果原区块剩余空间为零，将其中空闲列表中删除
     *
     * @param size
     * @param slot
     * @return
     */
    private Slot alloc(int size, Slot slot) {
        Slot obj = new Slot(slot.start, size);

        slot.start += size;
        slot.size -= size;

        //如果Slot 已经全部被分配了，将其从空闲列表中删除
        if (slot.size == 0) {
            emptyList.remove(slot);
        }

        allocatedSize += obj.size;

        return obj;
    }

    public int getAllocatedSize() {
        return allocatedSize;
    }

    public int getSize() {
        return size;
    }

    public String getEmptyListStr(){

        StringBuilder sb = new StringBuilder();

        if(!emptyList.isEmpty()){

            for (Slot slot : emptyList) {
                sb.append(slot.toString()).append(",");
            }

            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("Heap totalSize = %d, usedSize = %d ", size, allocatedSize);
    }
}
