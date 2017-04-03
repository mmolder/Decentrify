package se.kth.gossipingbroadcast;

import se.sics.kompics.KompicsEvent;

/**
 * Created by mikael on 2017-04-03.
 */
public class GBEBroadcast implements KompicsEvent {

    private Object message;

    public GBEBroadcast(Object message) {
        this.message = message;
    }

    public Object getMessage() {
        return message;
    }
}
