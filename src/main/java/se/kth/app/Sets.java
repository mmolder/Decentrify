package se.kth.app;

import java.util.ArrayList;

/**
 * Created by mikael on 2017-05-17.
 */
public class Sets {

    private ArrayList<Object> set = new ArrayList<>();

    public void add(Object element) {
        if(!set.contains(element)) {
            set.add(element);
        }
    }

    public void remove(Object element) {
        if(set.contains(element)) {
            set.remove(set.indexOf(element));
        }
    }
}
