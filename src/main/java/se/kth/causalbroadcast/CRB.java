package se.kth.causalbroadcast;

import se.kth.reliablebroadcast.RBPort;
import se.kth.reliablebroadcast.RBroadcast;
import se.kth.reliablebroadcast.RDeliver;
import se.sics.kompics.*;
import se.sics.ktoolbox.util.network.KAddress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mikael on 2017-04-04.
 */
public class CRB extends ComponentDefinition {

    private Positive<RBPort> rb = requires(RBPort.class);
    private Negative<CRBPort> crb = provides(CRBPort.class);

    private KAddress self;
    private HashMap<Object, KAddress> past;
    private ArrayList<Object> delivered;

    public CRB(Init init) {
        this.self = init.self;
        this.delivered = new ArrayList<>();
        this.past = new HashMap<>();

        //subscriptions
        subscribe(crbroadcastHandler, crb);
        subscribe(rDeliverHandler, rb);
    }

    /** handle causal order reliable broadcast events, forward it using reliable broadcast **/
    private Handler<CRBroadcast> crbroadcastHandler = new Handler<CRBroadcast>() {
        @Override
        public void handle(CRBroadcast crBroadcast) {
            Object msg = crBroadcast.payload;           // extract payload, don't want to send cBroadcast event
            trigger(new RBroadcast(msg, past), rb);     // in RB
            past.put(msg, self);                        // add to own past
        }
    };

    /** handle reliable broadcast deliver events, check if not delivered before, if not, check event past and deliver all not delivered **/
    private Handler<RDeliver> rDeliverHandler = new Handler<RDeliver>() {
        @Override
        public void handle(RDeliver rDeliver) {
            RBroadcast msg = (RBroadcast) rDeliver.payload;     // extract payload, don't want to handle rDeliver event

            // check if not delivered before
            if(!delivered.contains(msg)) {
                // go through list of past received by event handler
                for(Map.Entry<Object, KAddress> entry : msg.past.entrySet()) {
                    // check if not delivered before
                    if(!delivered.contains(entry.getValue())) {
                        trigger(new CRBDeliver(entry.getValue(), entry.getKey()), crb);         // in AppComp
                        delivered.add(entry.getValue());    // has now been delivered
                        // check if past does not contain the current key-value pair
                        if(past.containsKey(entry.getKey())) {
                            if(!past.get(entry.getKey()).equals(entry.getValue())) {
                                past.put(entry.getKey(), entry.getValue());     // add to past
                            }
                        }
                        return;
                    }
                }
                trigger(new CRBDeliver(rDeliver.source, msg.payload), crb);        // in AppComp
                delivered.add(msg);     // has been delivered
                // check if past does not contain the event key-value pair
                if(past.containsKey(rDeliver.payload)) {
                    if(!past.get(rDeliver.payload).equals(msg)) {
                        past.put(msg, rDeliver.source);
                    }
                }
            }
        }
    };

    public static class Init extends se.sics.kompics.Init<CRB> {
        public KAddress self;

        public Init(KAddress self) {
            this.self = self;
        }
    }
}
