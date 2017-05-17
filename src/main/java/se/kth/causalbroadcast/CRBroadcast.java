package se.kth.causalbroadcast;

import se.sics.kompics.KompicsEvent;

/**
 * Created by mikael on 2017-04-04.
 */
public class CRBroadcast implements KompicsEvent {
    public Object payload;

    public CRBroadcast(Object payload) {
        this.payload = payload;
    }
}
