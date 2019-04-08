package org.linuxlsx.gc.copy;

import org.linuxlsx.gc.common.Obj;

/**
 * 复制算法使用的对象
 * @author linuxlsx
 * @date 2019-04-07
 */
public class CopyObj extends Obj {

    /**
     * 标识一个对象是否应被复制，防止重复复制
     */
    public boolean copied = false;

    public CopyObj() {
    }

    public CopyObj(int size, int start) {
        super(size, start);
    }
}
