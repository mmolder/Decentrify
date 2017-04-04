package se.kth.causalbroadcast;

import se.sics.kompics.KompicsEvent;

/**
 * Created by mikael on 2017-04-04.
 */
public class CRBroadcast implements KompicsEvent {
    private Object message;

    public CRBroadcast(Object message) {
        this.message = message;
    }

    public Object getMessage() {
        return message;
    }
}
