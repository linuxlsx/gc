package org.linuxlsx.gc.reference;

import org.linuxlsx.gc.common.Obj;

/**
 * 引用计数使用的对象
 * @author linuxlsx
 * @date 2018/8/28
 */
public class ReferenceCountObj extends Obj {

    /**
     * 表示该对象的引用计数
     */
    public int count;

}
