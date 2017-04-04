package se.kth.causalbroadcast;

import se.kth.reliablebroadcast.RBPort;
import se.kth.reliablebroadcast.RBroadcast;
import se.kth.reliablebroadcast.RDeliver;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
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
    private HashMap<KAddress, Object> past;
    private ArrayList<Object> delivered;

    public CRB(Init init) {
        this.self = init.self;
        this.delivered = new ArrayList<>();
        this.past = new HashMap<>();

        //subscriptions
        subscribe(crbroadcastHandler, crb);
        subscribe(rDeliverHandler, rb);
    }

    private Handler<CRBroadcast> crbroadcastHandler = new Handler<CRBroadcast>() {
        @Override
        public void handle(CRBroadcast crBroadcast) {
            Object msg = crBroadcast.getMessage();
            trigger(new RBroadcast(msg, past), rb);
            past.put(self, msg);
        }
    };

    private Handler<RDeliver> rDeliverHandler = new Handler<RDeliver>() {
        @Override
        public void handle(RDeliver rDeliver) {
            Object msg = rDeliver.getPayload();
            if(!delivered.contains(msg)) {
                for(Map.Entry<KAddress, Object> entry : rDeliver.getPast().entrySet()) {
                    if(!delivered.contains(entry.getValue())) {
                        trigger(new CRBDeliver(entry.getKey(), entry.getValue()), crb);
                        delivered.add(entry.getValue());
                        if(past.containsKey(entry.getKey())) {
                            if(!past.get(entry.getKey()).equals(entry.getValue())) {
                                past.put(entry.getKey(), entry.getValue());
                            }
                        }
                    }
                }
                trigger(new CRBDeliver(rDeliver.getSource(), msg), crb);
                delivered.add(msg);
                if(past.containsKey(rDeliver.getSource())) {
                    if(!past.get(rDeliver.getSource()).equals(msg)) {
                        past.put(rDeliver.getSource(), msg);
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
