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

    private ArrayList<Object> delivered;

    public RB(Init init) {
        this.self = init.self;
        delivered = new ArrayList<>();
        //subscriptions
        subscribe(broadcastHandler, rb);
        subscribe(gbebDeliverHandler, gbeb);
    }

    private Handler<RBroadcast> broadcastHandler = new Handler<RBroadcast>() {
        @Override
        public void handle(RBroadcast rBroadcast) {
            System.out.println("RB received " + self);
            trigger(new GBEBroadcast(self, rBroadcast.getPayload()), gbeb);                       // in GBEB
            //trigger(new GBEBroadcast(self, rBroadcast), gbeb);
        }
    };

    private Handler<GBEBDeliver> gbebDeliverHandler = new Handler<GBEBDeliver>() {
        @Override
        public void handle(GBEBDeliver gbebDeliver) {
            Object msg = gbebDeliver.getMessage();
            if(!delivered.contains(msg)) {
                delivered.add(msg);
                trigger(new RDeliver(gbebDeliver.getPeer(), msg), rb);      // in CRB
                trigger(new GBEBroadcast(gbebDeliver.getPeer(), msg), gbeb);   // in GBEB
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
