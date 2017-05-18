package se.kth.growonlyset;

import java.util.Iterator;

/**
 * Created by mikael on 2017-05-17.
 */
public class TwoPhaseSet {
    private GSet set = new GSet();
    private GSet tombstone = new GSet();

    public void add(Object element) {
        if(!set.contains(element)) {
            set.add(element);
        }
    }

    public void remove(Object element) {
        if(set.contains(element) && !tombstone.contains(element)) {
            tombstone.add(element);
        }
    }

    public boolean contains(Object element) {
        if(set.contains(element) && !tombstone.contains(element)) {
            return true;
        }
        return false;
    }

    public String print() {
        StringBuilder sb = new StringBuilder();
        Object element;
        for(int i = 0; i < set.size(); i++) {
            element = set.elementAt(i);
            if(!tombstone.contains(element)) {
                sb.append(element.toString());
            }
        }
        return sb.toString();
    }
}
