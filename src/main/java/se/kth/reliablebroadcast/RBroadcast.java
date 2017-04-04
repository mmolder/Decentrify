package se.kth.reliablebroadcast;

import se.sics.kompics.KompicsEvent;
import se.sics.ktoolbox.util.network.KAddress;

import java.util.HashMap;

/**
 * Created by mikael on 2017-04-03.
 */
public class RBroadcast implements KompicsEvent {

    private Object payload;
    private HashMap<KAddress, Object> past;

    public RBroadcast(Object payload, HashMap<KAddress, Object> past) {
        this.payload = payload;
        this.past = past;
    }

    public Object getPayload() {
        return payload;
    }

    public HashMap<KAddress, Object> getPast() {
        return past;
    }
}
