package se.kth.growonlyset;

import java.util.Iterator;

/**
 * Created by mikael on 2017-05-17.
 */
public class TwoPhaseSet {
    private GSet set = new GSet();
    private GSet tombstone = new GSet();

    public boolean add(Object element) {
        // don't need to check tombstone since it only adds to it if it's present in set
        if(!set.contains(element)) {
            set.add(element);
            return true;
        } else {
            System.out.println("Not allowed to add '" + element + "' again");
            return false;
        }
    }

    public boolean remove(Object element) {
        if(set.contains(element) && !tombstone.contains(element)) {
            tombstone.add(element);
            return true;
        }
        System.out.println("Element " + element + " not in set, cant remove");
        return false;
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
                sb.append(element.toString() + " ");
            }
        }
        return sb.toString();
    }

    public int size() {
        return set.size();
    }

    public Object elementAt(int i) {
        return set.elementAt(i);
    }

}
