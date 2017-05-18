package se.kth.observedremovedset;

import se.sics.kompics.KompicsEvent;

/**
 * Created by mikael on 2017-05-18.
 */
public class OR_Lookup implements KompicsEvent {
    public Object element;

    public OR_Lookup(Object element) {
        this.element = element;
    }
}
