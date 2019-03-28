package org.linuxlsx.gc.reference;

import org.linuxlsx.gc.common.Obj;
import org.linuxlsx.gc.common.Slot;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 部分标记清除引用计数法的实现。
 * 用来解决无法回收循环引用的问题
 *
 * @author linuxlsx
 * @date 2019-03-28
 */
public class PartialMarkAndSweepReferenceCountAlgo extends ReferenceCountAlgo {

    /**
     * 用来保存颜色为 Color.HATCH 对象的队列
     */
    Queue<ReferenceCountObj> hatchQueue = new LinkedList<>();

    public PartialMarkAndSweepReferenceCountAlgo(int size, int fitStrategy) {
        super(size, fitStrategy);
    }

    @Override
    public ReferenceCountObj newObj(int size) {
        Slot slot = heap.applySlot(size);

        if (slot != null) {

            ReferenceCountObj obj = initObj(slot);
            obj.color = Color.BLACK;
            allocatedObjList.add(obj);
            System.out.println(String.format("Allocate obj(%d) start(%d) size(%d) total(%d) used(%d)", obj.hashCode(), obj.start, obj.size, heap.getSize(), heap.getAllocatedSize()));

            return obj;
        } else if (!hatchQueue.isEmpty()) {
            //如果第一次分配失败，就尝试清理疑似垃圾
            scanHatchQueue();
            return newObj(size);
        }

        System.out.println(String.format("PartialMarkAndSweepReferenceCount GC Fail Heap total(%d) used(%d) usableSlot: %s", heap.getSize(), heap.getAllocatedSize(), heap.getEmptyListStr()));
        throw new OutOfMemoryError("------ oh, out of memory! ------");
    }

    @Override
    protected void defRefCount(ReferenceCountObj obj) {
        obj.count--;

        //如果对象的计数变为0，则直接进行回收操作
        if (obj.count == 0) {

            //循环遍历该对象引用的对象，对其进行引用减一操作
            for (Obj child : obj.children) {
                ReferenceCountObj countObj = (ReferenceCountObj) child;
                defRefCount(countObj);
            }
            releaseMemory(obj);
        } else if (obj.color != Color.HATCH) {
            //对象的颜色不为 HATCH的时候，将其颜色标记为 HATCH，然后加入到队列中
            obj.color = Color.HATCH;
            hatchQueue.add(obj);
        }
    }

    /**
     * 对于Color==HATCH的对象进行检测
     */
    public void scanHatchQueue(){
        ReferenceCountObj obj = hatchQueue.poll();
        if(obj.color == Color.HATCH){
            paintGray(obj);
            scanGray(obj);
            collectWhite(obj);
        }else if(!hatchQueue.isEmpty()){
            scanHatchQueue();
        }

    }

    /**
     * 把对象的颜色置为 GRAY，对子对象计数器减1，递归调用 paintGray。
     * @param obj
     */
    public void paintGray(ReferenceCountObj obj){
        if(obj.color == Color.BLACK || obj.color == Color.HATCH){
            obj.color = Color.GRAY;

            for (Obj child : obj.children) {
                ReferenceCountObj c = (ReferenceCountObj) child;
                // 这个地方非常重要，是子对象的计数器减1，而不是父对象。
                // 否则会造成错误回收的情况。
                c.count--;
                paintGray(c);
            }
        }
    }

    public void scanGray(ReferenceCountObj obj){

        if(obj.color == Color.GRAY){
            if(obj.count > 0){
                paintBlack(obj);
            }else {
                obj.color = Color.WHITE;
                for (Obj child : obj.children) {
                    ReferenceCountObj c = (ReferenceCountObj) child;
                    scanGray(c);
                }
            }
        }
    }

    public void paintBlack(ReferenceCountObj obj){
        obj.color = Color.BLACK;
        for (Obj child : obj.children) {
            ReferenceCountObj c = (ReferenceCountObj) child;
            c.count++;

            if(c.color != Color.BLACK){
                paintBlack(c);
            }
        }
    }

    public void collectWhite(ReferenceCountObj obj){
        if(obj.color == Color.WHITE){
            obj.color = Color.BLACK;

            for (Obj child : obj.children) {
                ReferenceCountObj c = (ReferenceCountObj) child;
                collectWhite(c);
            }

            releaseMemory(obj);
        }
    }
}
