package se.kth.observedremovedset;

import se.sics.kompics.KompicsEvent;

import java.util.ArrayList;

/**
 * Created by mikael on 2017-05-18.
 */
public class OR_Remove implements KompicsEvent {
    public Object element;
    public ArrayList<String> tags;

    public OR_Remove(Object element, ArrayList<String> tags) {
        this.element = element;
        this.tags = tags;
    }
}
