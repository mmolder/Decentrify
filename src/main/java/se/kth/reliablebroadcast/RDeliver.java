package se.kth.reliablebroadcast;

import se.sics.kompics.KompicsEvent;
import se.sics.ktoolbox.util.network.KAddress;

import java.util.HashMap;

/**
 * Created by mikael on 2017-04-03.
 */
public class RDeliver implements KompicsEvent {

    public KAddress source;
    public Object payload;

    public RDeliver(KAddress source, Object payload) {
        this.source = source;
        this.payload = payload;
    }
}
