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
    private String msg;
    public Vertex v1 = new Vertex();
    public Vertex v2 = new Vertex();
    public Vertex v3 = new Vertex();
    public Edge e1 = new Edge(v1, v2);

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
                    addVertex();
                    break;
                case 1:
                    removeVertex();
                    break;
                case 2:
                    populate();
                default:
                    break;

            }
        }
    };

    public void addVertex() {
        KAddress peer = ScenarioSetup.getNodeAdr(ip, id);
        KHeader header = new BasicHeader(selfAdr, peer, Transport.UDP);
        KContentMsg msg1 = new BasicContentMsg(header, new AddVertex(v1));
        //KContentMsg msg1 = createMsg(ip, id, 0, v1, null);
        trigger(msg1, networkPort);
    }

    public void removeVertex() {
        KAddress peer = ScenarioSetup.getNodeAdr(ip, id);
        KHeader header = new BasicHeader(selfAdr, peer, Transport.UDP);
        KContentMsg msg1 = new BasicContentMsg(header, new RemoveVertex(v1));
        //KContentMsg msg1 = createMsg(ip, id, 1, v1, null);
        trigger(msg1, networkPort);
    }

    public void addEdge() {
        KAddress peer = ScenarioSetup.getNodeAdr(ip, id);
        KHeader header = new BasicHeader(selfAdr, peer, Transport.UDP);
        KContentMsg msg1 = new BasicContentMsg(header, new AddEdge(e1));
        //KContentMsg msg1 = createMsg(ip, id, 2, null, e1);
        trigger(msg1, networkPort);
    }

    public void removeEdge() {
        KAddress peer = ScenarioSetup.getNodeAdr(ip, id);
        KHeader header = new BasicHeader(selfAdr, peer, Transport.UDP);
        KContentMsg msg1 = new BasicContentMsg(header, new RemoveEdge(e1));
        //KContentMsg msg1 = createMsg(ip, id, 3, null, e1);
        trigger(msg1, networkPort);
    }

    public KContentMsg createMsg(String ipaddr, int ide, int type, Vertex w, Edge e) {
        KAddress peer = ScenarioSetup.getNodeAdr(ipaddr, ide);
        KHeader header = new BasicHeader(selfAdr, peer, Transport.UDP);
        if(type == 0) {
            return new BasicContentMsg(header, new AddVertex(w));
        } else if(type == 1) {
            return new BasicContentMsg(header, new RemoveVertex(w));
        } else if(type == 2) {
            return new BasicContentMsg(header, new AddEdge(e));
        } else {
            return new BasicContentMsg(header, new RemoveEdge(e));
        }
    }

    public void populate() {

        KContentMsg msg1 = createMsg(ip, id, 0, v1, null); // add vertex
        trigger(msg1, networkPort);
        KContentMsg msg2 = createMsg(ip, id, 0, v2, null); // add vertex
        trigger(msg2, networkPort);
        KContentMsg msg3 = createMsg(ip, id, 0, v3, null); // add vertex
        trigger(msg3, networkPort);
    }

    public void addEdges() {
        KContentMsg msg1 = createMsg(ip, id, 2, null, e1); // add edge
        trigger(msg1, networkPort);
    }

    public static class Init extends se.sics.kompics.Init<TwoPGraphClient> {

        public final KAddress selfAdr;
        public final String ip;
        public final int id;
        public final int type;
        public final String msg;

        public Init(KAddress selfAdr, String ip, int id, int type, String msg) {
            this.selfAdr = selfAdr;
            this.ip = ip;
            this.id = id;
            this.type = type;
            this.msg = msg;
        }
    }
}
