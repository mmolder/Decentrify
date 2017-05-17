package se.kth.reliablebroadcast;

import se.sics.kompics.KompicsEvent;
import se.sics.ktoolbox.util.network.KAddress;

import java.util.HashMap;

/**
 * Created by mikael on 2017-04-03.
 */
public class RBroadcast implements KompicsEvent {

    public Object payload;
    public HashMap<KAddress, Object> past;

    public RBroadcast(Object payload, HashMap<KAddress, Object> past) {
        this.payload = payload;
        this.past = past;
    }
}
