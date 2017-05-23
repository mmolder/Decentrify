package se.kth.observedremovedset;

import java.util.*;

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
                    }
                }
                set.keySet().removeAll(tags);   // remove from source
            }
            return tags;
        }
        // downstream
        else {
            ArrayList<Object> tmp = new ArrayList<>();
            for(String entry : replicas) {
                // if key exists, no need to check value since unique
                if(set.containsKey(entry)) {
                    tmp.add(entry);
                    continue;
                }
                else {
                    System.out.println("Not all add(e,u) has been delivered causal order does not suffice, wont remove");
                    return tags;
                }
            }
            // success, all pairs have previously been delivered, remove from set
            /*
            for(String entry : replicas) {
                set.remove(entry);      // remove from downstream
            }*/
            set.keySet().removeAll(tmp);
            return tags;
        }
    }

    public String print() {
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, Object> entry : set.entrySet()) {
            sb.append(entry.getValue().toString() + " ");
        }
        return sb.toString();
    }
}
