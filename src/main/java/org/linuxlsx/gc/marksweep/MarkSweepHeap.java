package org.linuxlsx.gc.marksweep;

import org.linuxlsx.gc.common.Heap;
import org.linuxlsx.gc.common.Slot;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * 为了支持标记清除算法针对于堆的扩展
 *
 * @author linuxlsx
 * @date 2017/12/28
 */
public class MarkSweepHeap extends Heap{

    public MarkSweepHeap() {
    }

    public MarkSweepHeap(int size, int fitStrategy) {
        super(size, fitStrategy);
    }

    /**
     * 对空闲列表中的区块进行合并，解决碎片化的问题
     */
    public void combine(){

        LinkedList<Slot> copyList = new LinkedList<Slot>();

        //首先按照起始位置给列表排个序
        Collections.sort(emptyList, (first, second) -> {

            if(first.start < second.start){
                return -1;
            }

            if(first.start > second.start){
                return 1;
            }

            return 0;
        });

        Slot slot = emptyList.poll();

        while (true){
            Slot next = emptyList.poll();

            if (next == null) {
                break;
            }

            if(slot.start + slot.size == next.start){
                slot.size += next.size;
            }else {
                copyList.add(slot);
                slot = next;
            }
        }

        copyList.add(slot);

        emptyList.clear();
        emptyList.addAll(copyList);
    }
}
