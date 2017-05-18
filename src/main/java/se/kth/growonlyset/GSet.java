package se.kth.growonlyset;

import java.util.ArrayList;

/**
 * Created by mikael on 2017-05-17.
 */
public class GSet {

    private ArrayList<Object> set = new ArrayList<>();

    public boolean add(Object element) {
        if(!set.contains(element)) {
            set.add(element);
            return true;
        }
        return false;
    }

    public String print() {
        StringBuilder res = new StringBuilder();
        for(Object entry : set) {
            res.append(entry.toString() + " ");
        }
        return res.toString();
    }

    public boolean contains(Object element) {
        return set.contains(element);
    }

    public int size() {
        return set.size();
    }

    public Object elementAt(int n) {
        return set.get(n);
    }
}
