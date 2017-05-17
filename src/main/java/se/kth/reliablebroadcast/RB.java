package se.kth.reliablebroadcast;

import se.kth.gossipingbroadcast.GBEBDeliver;
import se.kth.gossipingbroadcast.GBEBPort;
import se.kth.gossipingbroadcast.GBEBroadcast;
import se.sics.kompics.*;
import se.sics.ktoolbox.util.network.KAddress;

import java.util.ArrayList;

/**
 * Created by mikael on 2017-04-03.
 */
public class RB extends ComponentDefinition {

    private KAddress self;

    private Positive<GBEBPort> gbeb = requires(GBEBPort.class);
    private Negative<RBPort> rb = provides(RBPort.class);

    private ArrayList<Object> delivered;        // list of delivered messages

    public RB(Init init) {
        this.self = init.self;
        delivered = new ArrayList<>();

        //subscriptions
        subscribe(broadcastHandler, rb);
        subscribe(gbebDeliverHandler, gbeb);
    }

    /** handle reliable broadcast events, forward using gossip best effort broadcast **/
    private Handler<RBroadcast> broadcastHandler = new Handler<RBroadcast>() {
        @Override
        public void handle(RBroadcast rBroadcast) {
            // send entire broadcast event in order to not throw away past
            trigger(new GBEBroadcast(self, rBroadcast), gbeb);                           // in GBEB
        }
    };

    /** handle gossip best effort broadcast events, check if delivered before, if not deliver and add to past through gbeb **/
    private Handler<GBEBDeliver> gbebDeliverHandler = new Handler<GBEBDeliver>() {
        @Override
        public void handle(GBEBDeliver gbebDeliver) {
            //RBroadcast rbEvent = (RBroadcast) gbebDeliver.payload;     // cast to RBroadcast
            if(!delivered.contains(gbebDeliver.payload)) {
                delivered.add(gbebDeliver.payload);
                trigger(new RDeliver(gbebDeliver.source, gbebDeliver.payload), rb);         // in CRB
                //trigger(new RDeliver(gbebDeliver.source, rbEvent.payload, rbEvent.past), rb);
                trigger(new GBEBroadcast(gbebDeliver.source, gbebDeliver.payload), gbeb);   // in GBEB
                //trigger(new GBEBroadcast(gbebDeliver.source, msg), gbeb);
            }
        }
    };

    public static class Init extends se.sics.kompics.Init<RB> {
        public KAddress self;

        public Init(KAddress self) {
            this.self = self;
        }
    }
}
