package org.linuxlsx.gc.marksweep;

import org.linuxlsx.gc.common.Obj;
import org.linuxlsx.gc.common.Slot;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * 标记-清除算法的实现
 *
 * @author linuxlsx
 * @date 2017/12/27
 */
public class MarkSweepAlgo {

    protected MarkSweepHeap heap;

    /**
     * 表示GC 的根
     */
    protected LinkedList<MarkSweepObj> roots = new LinkedList<MarkSweepObj>();

    /**
     * 表示已经分配过的对象列表
     */
    protected LinkedList<MarkSweepObj> allocatedObjList = new LinkedList<MarkSweepObj>();

    public MarkSweepAlgo(int size, int fitStrategy) {
        heap = new MarkSweepHeap(size, fitStrategy);
    }

    /**
     * 执行标记清除的算法
     */
    public void markSweep() {

        int beforeGC = heap.getAllocatedSize();
        long start = System.nanoTime();

        markPhase();
        sweepPhase();

        long end = System.nanoTime();
        int endGC = heap.getAllocatedSize();

        System.out.println(String.format("MarkSweep GC Result Heap totalSize(%d) usedSize(%d > %d) time %d ns", heap.getSize(), beforeGC, endGC, (end - start)));

    }

    public void combine(){
        //标记清理之后执行合并
        heap.combine();
    }

    private void markPhase() {
        for (MarkSweepObj obj : roots) {
            mark(obj);
        }
    }

    private void mark(MarkSweepObj obj) {
        if (!obj.marked) {
            obj.marked = true;
            if (!obj.children.isEmpty()) {
                for (Obj child : obj.children) {
                    mark((MarkSweepObj) child);
                }
            }
        }
    }

    private void sweepPhase() {

        Iterator<MarkSweepObj> iterator = allocatedObjList.iterator();
        while (iterator.hasNext()) {

            MarkSweepObj obj = iterator.next();
            if (!obj.marked) {
                heap.release(new Slot(obj.start, obj.size));
                iterator.remove();
            } else {
                obj.marked = false;
            }
        }
    }

    /**
     * 创建一个新的对象
     *
     * @param size 对象的大小
     * @return 创建好的对象
     * @throws OutOfMemoryError 当内存无法满足分配要求时抛出
     */
    public MarkSweepObj newObj(int size) {

        Slot slot = heap.applySlot(size);

        //如果申请到的内存空间为null, 则执行一次GC
        if (slot == null) {

            markSweep();

            slot = heap.applySlot(size);

            if (slot == null) {
                System.out.println(String.format("MarkSweep GC Fail Heap total(%d) used(%d) usableSlot: %s", heap.getSize(), heap.getAllocatedSize(), heap.getEmptyListStr()));
                throw new OutOfMemoryError("------ oh, out of memory! ------");
            }
        }


        MarkSweepObj obj = initObj(slot);
        allocatedObjList.add(obj);
        System.out.println(String.format("Allocate obj(%d) start(%d) size(%d) total(%d) used(%d)", obj.hashCode(), obj.start, obj.size, heap.getSize(), heap.getAllocatedSize()));

        return obj;
    }

    private MarkSweepObj initObj(Slot slot) {
        MarkSweepObj obj = new MarkSweepObj();

        obj.start = slot.start;
        obj.size = slot.size;

        return obj;
    }



    /**
     * 把对象置为根节点
     *
     * @param obj 根对象
     */
    public void makeItToRoot(MarkSweepObj obj) {
        roots.add(obj);
    }

}
