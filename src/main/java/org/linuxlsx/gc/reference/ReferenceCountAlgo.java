package org.linuxlsx.gc.reference;

import org.linuxlsx.gc.common.Heap;
import org.linuxlsx.gc.common.Obj;
import org.linuxlsx.gc.common.Slot;

import java.util.LinkedList;

/**
 * @author rongruo.lsx
 * @date 2018/8/28
 */
public class ReferenceCountAlgo {

    protected Heap heap;

    public ReferenceCountAlgo(int size, int fitStrategy) {
        heap = new Heap(size, fitStrategy);
    }

    /**
     * 表示GC 的根
     */
    ReferenceCountObj root = new ReferenceCountObj();

    /**
     * 表示已经分配过的对象列表
     */
    protected LinkedList<ReferenceCountObj> allocatedObjList = new LinkedList<>();


    /**
     * 创建一个新的对象
     *
     * @param size 对象的大小
     * @return 创建好的对象
     * @throws OutOfMemoryError 当内存无法满足分配要求时抛出
     */
    public ReferenceCountObj newObj(int size) {

        Slot slot = heap.applySlot(size);

        //对于引用计数法来说，如果slot 为空，说明内存已经耗尽了
        if (slot == null) {
            System.out.println(String.format("ReferenceCount GC Fail Heap total(%d) used(%d) usableSlot: %s", heap.getSize(), heap.getAllocatedSize(), heap.getEmptyListStr()));
            throw new OutOfMemoryError("------ oh, out of memory! ------");
        }

        ReferenceCountObj obj = initObj(slot);
        allocatedObjList.add(obj);
        System.out.println(String.format("Allocate obj(%d) start(%d) size(%d) total(%d) used(%d)", obj.hashCode(), obj.start, obj.size, heap.getSize(), heap.getAllocatedSize()));

        return obj;
    }

    /**
     * 建立对象之间的引用关系。 相当于 from = to
     * @param from      引用的起始对象。
     * @param to        应用的目标对象。该对象的引用计数需要加一
     */
    public void reference(ReferenceCountObj from, ReferenceCountObj to) {
        incrRefCount(to);
        from.children.add(to);
    }

    /**
     * 删除对象之间的引用关系。相当于 from = null
     * @param from    引用 '需要解除引用对象' 的对象。 如果 from == null, 说明是要解除和根的引用
     * @param to      需要解除引用的对象。该对象的引用计数会减一
     */
    public void deReference(ReferenceCountObj from, ReferenceCountObj to){

        if(from == null){
            root.children.remove(to);
        }else {
            from.children.remove(to);
        }

        defRefCount(to);
    }

    /**
     * 修改对象的引用关系。相当于从 from = oldTo 变化为 from = newTo
     * @param from
     * @param oldTo
     * @param newTo
     */
    public void changeReference(ReferenceCountObj from, ReferenceCountObj oldTo, ReferenceCountObj newTo){

        from.children.remove(oldTo);
        defRefCount(oldTo);
        from.children.add(newTo);
        incrRefCount(newTo);

    }

    private void incrRefCount(ReferenceCountObj obj) {
        obj.count++;
    }

    private void defRefCount(ReferenceCountObj obj) {
        obj.count--;

        //如果对象的计数变为0，则直接进行回收操作
        if (obj.count == 0) {

            //循环遍历该对象引用的对象，对其进行引用减一操作
            for (Obj child : obj.children) {
                ReferenceCountObj countObj = (ReferenceCountObj) child;
                defRefCount(countObj);
            }

            //释放掉内存
            heap.release(new Slot(obj.start, obj.size));
            System.out.println(String.format("ReferenceCount GC Release Heap total(%d) used(%d) usableSlot: %s", heap.getSize(), heap.getAllocatedSize(), heap.getEmptyListStr()));
        }
    }

    private ReferenceCountObj initObj(Slot slot) {
        ReferenceCountObj obj = new ReferenceCountObj();

        obj.start = slot.start;
        obj.size = slot.size;
        //对象的初始化引用计数为 0
        obj.count = 0;

        return obj;
    }

    /**
     * 把对象置为根节点
     *
     * @param obj 根对象
     */
    public void makeItToRoot(ReferenceCountObj obj) {
        root.children.add(obj);
        obj.count++;
    }
}
