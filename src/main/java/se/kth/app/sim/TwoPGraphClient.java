package se.kth.app.sim;

import se.kth.graph.*;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.Transport;
import se.sics.kompics.timer.Timer;
import se.sics.ktoolbox.util.network.KAddress;
import se.sics.ktoolbox.util.network.KContentMsg;
import se.sics.ktoolbox.util.network.KHeader;
import se.sics.ktoolbox.util.network.basic.BasicContentMsg;
import se.sics.ktoolbox.util.network.basic.BasicHeader;

/**
 * Created by mikael on 2017-05-21.
 */
public class TwoPGraphClient extends ComponentDefinition {
    //*******************************CONNECTIONS********************************
    Positive<Timer> timerPort = requires(Timer.class);
    Positive<Network> networkPort = requires(Network.class);
    //**************************************************************************
    private KAddress selfAdr;
    private String ip;
    private int id;
    private int type;
    private Object msg;

    /*
    public Vertex v1 = new Vertex(1);
    public Vertex v2 = new Vertex(2);
    public Vertex v3 = new Vertex(3);
    public Edge e1 = new Edge(v1, v2);*/

    public TwoPGraphClient(TwoPGraphClient.Init init) {
        selfAdr = init.selfAdr;
        ip = init.ip;
        id = init.id;
        type = init.type;
        msg = init.msg;

        subscribe(handleStart, control);
    }

    Handler handleStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            switch (type) {
                case 0:
                    Vertex v = (Vertex)msg;
                    addVertex(v);
                    break;
                case 1:
                    Vertex w = (Vertex)msg;
                    removeVertex(w);
                    break;
                case 2:
                    Edge e = (Edge)msg;
                    addEdge(e);
                    break;
                case 3:
                    Edge f = (Edge)msg;
                    removeEdge(f);
                    break;
                default:
                    break;

            }
        }
    };

    public void addVertex(Vertex v) {
        KAddress peer = ScenarioSetup.getNodeAdr(ip, id);
        KHeader header = new BasicHeader(selfAdr, peer, Transport.UDP);
        KContentMsg msg1 = new BasicContentMsg(header, new AddVertex(v));
        //KContentMsg msg1 = createMsg(ip, id, 0, v1, null);
        trigger(msg1, networkPort);
    }

    public void removeVertex(Vertex w) {
        KAddress peer = ScenarioSetup.getNodeAdr(ip, id);
        KHeader header = new BasicHeader(selfAdr, peer, Transport.UDP);
        KContentMsg msg1 = new BasicContentMsg(header, new RemoveVertex(w));
        //KContentMsg msg1 = createMsg(ip, id, 1, v1, null);
        trigger(msg1, networkPort);
    }

    public void addEdge(Edge e) {
        KAddress peer = ScenarioSetup.getNodeAdr(ip, id);
        KHeader header = new BasicHeader(selfAdr, peer, Transport.UDP);
        KContentMsg msg1 = new BasicContentMsg(header, new AddEdge(e));
        //KContentMsg msg1 = createMsg(ip, id, 2, null, e1);
        trigger(msg1, networkPort);
    }

    public void removeEdge(Edge e) {
        KAddress peer = ScenarioSetup.getNodeAdr(ip, id);
        KHeader header = new BasicHeader(selfAdr, peer, Transport.UDP);
        KContentMsg msg1 = new BasicContentMsg(header, new RemoveEdge(e));
        //KContentMsg msg1 = createMsg(ip, id, 3, null, e1);
        trigger(msg1, networkPort);
    }

    public KContentMsg createMsg(String ipaddr, int ide, int type, int vid, Edge e) {
        KAddress peer = ScenarioSetup.getNodeAdr(ipaddr, ide);
        KHeader header = new BasicHeader(selfAdr, peer, Transport.UDP);
        if(type == 0) {
            return new BasicContentMsg(header, new AddVertex(new Vertex(vid)));
        } else if(type == 1) {
            return new BasicContentMsg(header, new RemoveVertex(new Vertex(vid)));
        } else if(type == 2) {
            return new BasicContentMsg(header, new AddEdge(e));
        } else {
            return new BasicContentMsg(header, new RemoveEdge(e));
        }
    }

    public void populate() {

        KContentMsg msg1 = createMsg(ip, id, 0, 1, null); // add vertex
        trigger(msg1, networkPort);
        KContentMsg msg2 = createMsg(ip, id, 0, 2, null); // add vertex
        trigger(msg2, networkPort);
        KContentMsg msg3 = createMsg(ip, id, 0, 3, null); // add vertex
        trigger(msg3, networkPort);
    }
/*
    public void addEdges() {
        KContentMsg msg1 = createMsg(ip, id, 2, null, e1); // add edge
        trigger(msg1, networkPort);
    }*/

    public static class Init extends se.sics.kompics.Init<TwoPGraphClient> {

        public final KAddress selfAdr;
        public final String ip;
        public final int id;
        public final int type;
        public final Object msg;

        public Init(KAddress selfAdr, String ip, int id, int type, Object msg) {
            this.selfAdr = selfAdr;
            this.ip = ip;
            this.id = id;
            this.type = type;
            this.msg = msg;
        }
    }
}
