package se.kth.growonlyset;

import se.sics.kompics.KompicsEvent;

/**
 * Created by mikael on 2017-05-21.
 */
public class GSet_Add implements KompicsEvent {
    public Object element;

    public GSet_Add(Object element) {
        this.element = element;
    }

}
