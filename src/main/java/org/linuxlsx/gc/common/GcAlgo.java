package org.linuxlsx.gc.common;

/**
 * @author linuxlsx
 * @date 2017/12/28
 */
public interface GcAlgo {

    /**
     * 创建一个指定大小的对象
     * @param size
     * @return
     */
    Obj newObj(int size);
}
