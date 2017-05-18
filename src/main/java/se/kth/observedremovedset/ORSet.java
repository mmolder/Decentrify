package se.kth.observedremovedset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

/**
 * Created by mikael on 2017-05-18.
 */
public class ORSet {
    private HashMap<String, Object> set = new HashMap<>();

    public boolean lookup(Object element) {
        return set.containsValue(element);
    }

    public String add(Object element, String u) {
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
        return tag;
    }

    public ArrayList<String> remove(Object element, ArrayList<String> replicas) {
        ArrayList<String> tags = new ArrayList<>();

        // at source
        if(replicas.isEmpty()) {
            if(lookup(element)) {
                // extract all duplicate values with different tags
                for(String key : set.keySet()) {
                    if(set.get(key).equals(element)) {
                        tags.add(key);
                        set.remove(key);    // remove from source
                    }
                }
            }
            return tags;
        }
        // downstream
        else {
            for(String entry : replicas) {
                // if key exists, no need to check value since unique
                if(set.containsKey(entry)) {
                    continue;
                }
                else {
                    return tags;
                }
            }
            // success, all pairs have previously been delivered, remove from set
            for(String entry : replicas) {
                set.remove(entry);      // remove from downstream
            }
            return tags;
        }
    }
}
