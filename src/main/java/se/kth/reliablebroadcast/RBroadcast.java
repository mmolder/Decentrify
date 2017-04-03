package se.kth.reliablebroadcast;

import se.sics.kompics.KompicsEvent;

/**
 * Created by mikael on 2017-04-03.
 */
public class RBroadcast implements KompicsEvent {

    private Object payload;

    public RBroadcast(Object payload) {
        this.payload = payload;
    }

    public Object getPayload() {
        return payload;
    }
}
