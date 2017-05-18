package se.kth.growonlyset;

import se.sics.kompics.KompicsEvent;

/**
 * Created by mikael on 2017-05-18.
 */
public class Remove implements KompicsEvent {
    public Object element;

    public Remove(Object element) {
        this.element = element;
    }
}
