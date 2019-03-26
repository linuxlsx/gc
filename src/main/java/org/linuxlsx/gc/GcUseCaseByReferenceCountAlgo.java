package org.linuxlsx.gc;

import org.linuxlsx.gc.common.Heap;
import org.linuxlsx.gc.marksweep.MarkSweepAlgo;
import org.linuxlsx.gc.marksweep.MarkSweepObj;
import org.linuxlsx.gc.reference.ReferenceCountAlgo;
import org.linuxlsx.gc.reference.ReferenceCountObj;

/**
 * @author linuxlsx
 * @date 2017/12/28
 */
public class GcUseCaseByReferenceCountAlgo {

    public static void main(String[] args) {

        ReferenceCountAlgo algo = new ReferenceCountAlgo(20, Heap.FIRST_FIT_STRATEGY);


        try {
            ReferenceCountObj obj1 = algo.newObj(4);
            ReferenceCountObj obj2 = algo.newObj(3);
            algo.reference(obj1, obj2);
            algo.makeItToRoot(obj1);

            ReferenceCountObj obj3 = algo.newObj(3);
            algo.makeItToRoot(obj3);

            algo.deReference(obj1, obj2);

            ReferenceCountObj obj4 = algo.newObj(4);
            algo.makeItToRoot(obj4);
            ReferenceCountObj obj5 = algo.newObj(2);
            ReferenceCountObj obj6 = algo.newObj(3);

            algo.reference(obj4, obj5);
            algo.reference(obj4, obj6);

            algo.deReference( null, obj1);

            ReferenceCountObj obj7 = algo.newObj(2);
            algo.reference(obj6, obj7);

            ReferenceCountObj obj8 = algo.newObj(7);
            algo.reference(obj3, obj8);
            ReferenceCountObj obj9 = algo.newObj(4);

            algo.changeReference(obj3, obj8, obj9);

        }catch (Throwable e){
            e.printStackTrace();
        }



    }
}
