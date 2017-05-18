package se.kth.observedremovedset;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

/**
 * Created by mikael on 2017-05-18.
 */
public class ORSet {
    private HashMap<String, Object> set = new HashMap<>();

    public boolean lookup(Object element) {
        return set.containsKey(element);
    }

    public void add(Object element, String u) {
        String tag;

        // at source
        if(u.equals("")) {
            tag = UUID.randomUUID().toString();
            set.put(tag, element);
        }
        // downstream
        else {
            tag = u;
            set.put(tag, element);
        }
    }

    public void remove(Object element, String r) {
        String replica;

        // at source
        if(r.equals("")) {
            if(lookup(element)) {

            }
        }
        // downstream
        else {

        }
    }
}
