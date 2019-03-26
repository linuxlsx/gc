package org.linuxlsx.gc.marksweep;

import org.linuxlsx.gc.common.Obj;

/**
 * 标记清除算法使用到的对象
 * @author linuxlsx
 * @date 2017/12/27
 */
public class MarkSweepObj extends Obj{

    /**
     * 表示对象的标记状态  true : 表示不需要清理，false : 表示需要清理
     */
    public boolean marked;

}
