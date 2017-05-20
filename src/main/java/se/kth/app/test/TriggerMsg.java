package se.kth.app.test;

import se.sics.kompics.KompicsEvent;

/**
 * Created by mikael on 2017-05-16.
 */
public class TriggerMsg implements KompicsEvent {
    public Object payload;

    public TriggerMsg(Object payload) {
        this.payload = payload;
    }
}
