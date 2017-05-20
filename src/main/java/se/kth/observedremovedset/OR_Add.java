package se.kth.observedremovedset;

import se.sics.kompics.KompicsEvent;

/**
 * Created by mikael on 2017-05-18.
 */
public class OR_Add implements KompicsEvent {
    public Object element;
    public String tag;

    public OR_Add(Object element, String tag) {
        this.element = element;
        this.tag = tag;
    }
}
