package org.linuxlsx.gc.copy;

import org.linuxlsx.gc.common.Obj;

import java.util.LinkedList;

/**
 * GC 复制算法
 *
 * @author linuxlsx
 * @date 2019-04-07
 */
public class CopyAlgo {

    private CopyHeap heap;

    /**
     * 表示GC 的根
     */
    private LinkedList<CopyObj> roots = new LinkedList<>();


    public CopyAlgo(int size) {
        heap = new CopyHeap(size);
    }

    /**
     * 创建对象
     * @param size
     * @return
     */
    public CopyObj newObj(int size) {
        if (!heap.hasEnoughMemory(size)) {
            copying();

            if (!heap.hasEnoughMemory(size)) {

                System.out.println(String.format("Copy GC Fail Heap total(%d) used(%d)", heap.size, heap.free));
                throw new OutOfMemoryError("------ oh, out of memory! ------");
            }
        }

        CopyObj copyObj = new CopyObj(size, heap.free);
        heap.free += size;

        return copyObj;
    }

    /**
     * 开始进行复制
     */
    private void copying() {

        int oldSize = heap.free - heap.fromStart;
        //将当前空闲内存的起始位置置为to区域的起始位置
        heap.free = heap.toStart;

        //从根节点开始递归的进行复制
        for (CopyObj root : roots) {
            copy(root);
        }

        //交互from 和 to
        heap.swap();

        System.out.println(String.format("Copy GC End. Heap before(%d) now(%d)", oldSize, heap.free - heap.fromStart));
    }

    private void copy(CopyObj copyObj) {

        //复制对象，在目标区域中申请同样大小的内存
        copyData(copyObj);
        //将复制标记为置为true
        copyObj.copied = true;

        //开始复制引用的对象
        for (Obj child : copyObj.children) {
            CopyObj obj = (CopyObj) child;
            copy(obj);

        }
    }

    private void copyData(CopyObj copyObj) {
        //因为Java的参数传递均为值传递，所以为了不影响上层的使用
        //这里均实现为修改对象的起始位置。
        copyObj.start = heap.copyData(copyObj.size);
    }

    /**
     * 把对象置为根节点
     *
     * @param obj 根对象
     */
    public void makeItToRoot(CopyObj obj) {
        roots.add(obj);
    }

    public void removeFromRoot(CopyObj obj){
        roots.remove(obj);
    }
}
