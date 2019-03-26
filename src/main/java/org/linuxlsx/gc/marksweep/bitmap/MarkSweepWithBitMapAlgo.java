package org.linuxlsx.gc.marksweep.bitmap;

import org.linuxlsx.gc.common.Obj;
import org.linuxlsx.gc.common.Slot;
import org.linuxlsx.gc.marksweep.MarkSweepAlgo;
import org.linuxlsx.gc.marksweep.MarkSweepObj;

import java.util.Iterator;

/**
 *
 * 使用位图标记来标记对象，兼容COW(copy-on-write)
 * 将对象的标记从对象头移动到位图表中。位图表中的一位代表了内存中的一个字。
 *
 * @author linuxlsx
 * @date 2017/12/28
 */
public class MarkSweepWithBitMapAlgo extends MarkSweepAlgo {
    /**
     * 定义一个字的长度
     */
    public static final int WORD_LENGTH = 32;

    private int[] bitmapTable;
    private int bitmapTableSize;

    public MarkSweepWithBitMapAlgo(int size, int fitStrategy) {
        super(size, fitStrategy);
        this.bitmapTableSize = size / WORD_LENGTH + 1;
        //初始化位图标记
        bitmapTable = new int[bitmapTableSize];
    }

    @Override
    public void markSweep() {
        markPhase();
        sweepPhase();
    }

    private void markPhase() {
        for (MarkSweepObj obj : roots) {
            mark(obj);
        }
    }

    private void mark(MarkSweepObj obj) {

        int index = obj.start / WORD_LENGTH;
        int offset = obj.start % WORD_LENGTH;

        if ((bitmapTable[index] & (1 << offset)) == 0) {
            //将对象对应的标志位置为 1
            bitmapTable[index] |= (1 << offset);
            for (Obj child : obj.children) {
                mark((MarkSweepObj) child);
            }
        }
    }

    private void sweepPhase() {
        int index, offset;

        Iterator<MarkSweepObj> iterator = allocatedObjList.iterator();
        while (iterator.hasNext()) {

            MarkSweepObj obj = iterator.next();
            index = obj.start / WORD_LENGTH;
            offset = obj.start % WORD_LENGTH;

            //标记完成之后标志位为0，说明该对象是需要回收的对象
            if ((bitmapTable[index] & (1 << offset)) == 0) {
                heap.release(new Slot(obj.start, obj.size));
                iterator.remove();
            }
        }

        //重置标志位
        for (int i = 0; i < bitmapTableSize; i++) {
            bitmapTable[i] = 0;
        }
    }


}
