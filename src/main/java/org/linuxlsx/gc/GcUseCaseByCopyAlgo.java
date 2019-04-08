package org.linuxlsx.gc;

import org.linuxlsx.gc.copy.CopyAlgo;
import org.linuxlsx.gc.copy.CopyObj;

/**
 * @author linuxlsx
 * @date 2019-04-07
 */
public class GcUseCaseByCopyAlgo {

    public static void main(String[] args) {

        CopyAlgo copyAlgo = new CopyAlgo(32);

        CopyObj first = copyAlgo.newObj(4);
        CopyObj second = copyAlgo.newObj(4);

        copyAlgo.makeItToRoot(first);
        copyAlgo.makeItToRoot(second);

        CopyObj firstChildOne = copyAlgo.newObj(5);

        //进行到这里时需要的总内存为 13 + 7， 总内存只有16， 所有会进行一次复制
        //回收掉没有引用来源的 firstChildOne
        CopyObj firstChildTwo = copyAlgo.newObj(7);

        first.children.add(firstChildTwo);

        //到这里需要的内存为 4+4+7+6 = 21, 大于总内存 16，会进行一次copy ,
        //但是仍然是不能满足要求的，所以会抛出OOM的异常
        CopyObj firstChildChildOne = copyAlgo.newObj(6);
        firstChildTwo.children.add(firstChildChildOne);


        CopyObj secondChild = copyAlgo.newObj(6);
        second.children.add(secondChild);

        firstChildOne.children.remove(firstChildChildOne);
        first.children.remove(firstChildTwo);

        CopyObj needCopyingObj = copyAlgo.newObj(4);
    }
}
