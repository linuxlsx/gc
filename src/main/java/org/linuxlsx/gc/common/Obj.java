package org.linuxlsx.gc.common;

import org.linuxlsx.gc.marksweep.MarkSweepObj;

import java.util.LinkedList;

/**
 * 一个基本的对象，使用不同的GC算法可能会需要不同的对象
 * @author linuxlsx
 * @date 2017/12/28
 */
public class Obj {

    /**
     * 表示对象的大小
     */
    public int size;

    /**
     * 表示对象在中的起始位置
     */
    public int start;

    /**
     * 用来表示对象之间的引用关系
     */
    public LinkedList<Obj> children = new LinkedList<Obj>();
}
