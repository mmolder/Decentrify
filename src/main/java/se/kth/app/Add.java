package se.kth.app;

import se.sics.kompics.KompicsEvent;

/**
 * Created by mikael on 2017-05-17.
 */
public class Add implements KompicsEvent {
    private Object element;

    public Add(Object element) {
        this.element = element;
    }

    public Object getElement() {
        return element;
    }
}
