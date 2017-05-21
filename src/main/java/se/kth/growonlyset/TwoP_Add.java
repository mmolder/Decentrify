package se.kth.growonlyset;

import se.sics.kompics.KompicsEvent;

/**
 * Created by mikael on 2017-05-17.
 */
public class TwoP_Add implements KompicsEvent {
    public Object element;

    public TwoP_Add(Object element) {
        this.element = element;
    }

}