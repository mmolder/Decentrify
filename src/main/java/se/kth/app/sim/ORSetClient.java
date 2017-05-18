package se.kth.app.sim;

import se.kth.growonlyset.Add;
import se.kth.growonlyset.Remove;
import se.kth.observedremovedset.OR_Add;
import se.kth.observedremovedset.OR_Remove;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.Transport;
import se.sics.kompics.timer.Timer;
import se.sics.ktoolbox.util.network.KAddress;
import se.sics.ktoolbox.util.network.KContentMsg;
import se.sics.ktoolbox.util.network.KHeader;
import se.sics.ktoolbox.util.network.basic.BasicContentMsg;
import se.sics.ktoolbox.util.network.basic.BasicHeader;

/**
 * Created by Mikael on 2017-04-29.
 */
public class ORSetClient extends ComponentDefinition {

    //*******************************CONNECTIONS********************************
    Positive<Timer> timerPort = requires(Timer.class);
    Positive<Network> networkPort = requires(Network.class);
    //**************************************************************************
    private KAddress selfAdr;
    private String ip;
    private int id;
    private int type;

    public ORSetClient(ORSetClient.Init init) {
        selfAdr = init.selfAdr;
        ip = init.ip;
        id = init.id;
        type = init.type;

        subscribe(handleStart, control);
    }

    Handler handleStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            switch (type) {
                case 0:
                    addOp();
                    break;
                case 1:
                    removeOp();
                    break;
                default:
                    break;

            }
        }
    };

    public void addOp() {
        KContentMsg msg = createMsg(ip, id, 0);
        trigger(msg, networkPort);
    }

    public void removeOp() {
        KContentMsg msg = createMsg(ip, id, 1);
        trigger(msg, networkPort);
    }

    public KContentMsg createMsg(String ipaddr, int ide, int type) {
        KAddress peer = ScenarioSetup.getNodeAdr(ipaddr, ide);
        KHeader header = new BasicHeader(selfAdr, peer, Transport.UDP);
        if(type == 0) {
            return new BasicContentMsg(header, new OR_Add("dog"));
        } else {
            return new BasicContentMsg(header, new OR_Remove("dog"));
        }
    }


    public static class Init extends se.sics.kompics.Init<se.kth.app.sim.OperationClient> {

        public final KAddress selfAdr;
        public final String ip;
        public final int id;
        public final int type;

        public Init(KAddress selfAdr, String ip, int id, int type) {
            this.selfAdr = selfAdr;
            this.ip = ip;
            this.id = id;
            this.type = type;
        }
    }
}
