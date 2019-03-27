package org.linuxlsx.gc.reference;

import org.linuxlsx.gc.common.Obj;
import org.linuxlsx.gc.common.Slot;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author rongruo.lsx
 * @date 2019-03-27
 */
public class DeferredReferenceCountAlgo extends ReferenceCountAlgo{

    public static final int CAPACITY = 4;
    /**
     * Zero Count Table. 用来记录计数器变为0的对象
     * 这里通过一个有界队列来实现
     */
    Queue<Obj> zct = new ArrayBlockingQueue<>(CAPACITY);

    public DeferredReferenceCountAlgo(int size, int fitStrategy) {
        super(size, fitStrategy);
    }

    @Override
    public ReferenceCountObj newObj(int size) {

        Slot slot = heap.applySlot(size);

        //对于引用计数法来说，如果slot 为空
        //不直接抛出OOM，而是释放掉ZCT中可以释放的对象
        if (slot == null) {

            scanZctToReleaseMemory();

            slot = heap.applySlot(size);

            if (slot == null) {

                System.out.println(String.format("ReferenceCount GC Fail Heap total(%d) used(%d) usableSlot: %s", heap.getSize(), heap.getAllocatedSize(), heap.getEmptyListStr()));
                throw new OutOfMemoryError("------ oh, out of memory! ------");
            }
        }

        ReferenceCountObj obj = initObj(slot);
        allocatedObjList.add(obj);
        System.out.println(String.format("Allocate obj(%d) start(%d) size(%d) total(%d) used(%d)", obj.hashCode(), obj.start, obj.size, heap.getSize(), heap.getAllocatedSize()));

        return obj;
    }

    /**
     * 与原始实现不同，当计数器变成0时，将对象放到ZCT中而不是直接释放
     * @param obj
     */
    @Override
    protected void defRefCount(ReferenceCountObj obj) {
        obj.count--;

        //如果对象的计数变为0，则优先把对象放到zct中
        if (obj.count == 0) {

            //如果队列满了，则释放掉队列中的对象
            if(isFull()){
                scanZctToReleaseMemory();
            }

            zct.add(obj);
        }
    }

    private boolean isFull(){
        if(zct.size() == CAPACITY){
            return true;
        }
        return false;
    }

    private void scanZctToReleaseMemory(){

        Iterator<Obj> iterator = zct.iterator();
        while (iterator.hasNext()){

            ReferenceCountObj countObj = (ReferenceCountObj) iterator.next();

            //对于计数器为0的对象，从ZCT中移除，并删除该对象
            if(countObj.count == 0){
                iterator.remove();
                delete(countObj);
            }

        }
    }

    private void delete(ReferenceCountObj countObj){

        //释放内存
        releaseMemory(countObj);

        //减少引用对象的计数器值，如果计数器值为0，回收之
        for (Obj child : countObj.children) {
            ReferenceCountObj childCountObj = (ReferenceCountObj) child;
            childCountObj.count--;
            if(childCountObj.count == 0){
                delete(countObj);
            }
        }
    }
}
