package org.linuxlsx.gc.reference;

/**
 * @author linuxlsx
 * @date 2019-03-28
 */
public enum Color {

    /**
     * 绝对不是垃圾的对象
     */
    BLACK,
    /**
     * 绝对是垃圾的对象
     */
    WHITE,
    /**
     * 搜索完毕的对象
     */
    GRAY,
    /**
     * 可能是循环垃圾的对象
     */
    HATCH;
}
