package se.kth.app.sim;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.app.test.Ping;
import se.kth.app.test.Pong;
import se.kth.app.test.TriggerMsg;
import se.kth.causalbroadcast.CRBDeliver;
import se.kth.causalbroadcast.CRBPort;
import se.kth.causalbroadcast.CRBroadcast;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.Transport;
import se.sics.kompics.timer.Timer;
import se.sics.ktoolbox.util.identifiable.Identifier;
import se.sics.ktoolbox.util.network.KAddress;
import se.sics.ktoolbox.util.network.KContentMsg;
import se.sics.ktoolbox.util.network.KHeader;
import se.sics.ktoolbox.util.network.basic.BasicContentMsg;
import se.sics.ktoolbox.util.network.basic.BasicHeader;

/**
 * Created by Mikael on 2017-04-29.
 */
public class SimulationClient extends ComponentDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(se.kth.app.sim.SimulationClient.class);
    private String logPrefix = " ";

    //*******************************CONNECTIONS********************************
    Positive<Timer> timerPort = requires(Timer.class);
    Positive<Network> networkPort = requires(Network.class);

    Positive<CRBPort> crb = requires(CRBPort.class);
    //**************************************************************************
    private KAddress selfAdr;
    private String ip;
    private int id;
    private String message;

    public SimulationClient(SimulationClient.Init init) {
        selfAdr = init.selfAdr;
        ip = init.ip;
        id = init.id;
        message = init.msg;
        subscribe(handleStart, control);
    }

    Handler handleStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            //LOG.info("{}starting...", logPrefix);
            System.out.println("Special client starting, sending msg: " + message + " to " + ip);
            KAddress peer = ScenarioSetup.getNodeAdr(ip, id);
            KHeader header = new BasicHeader(selfAdr, peer, Transport.UDP);
            KContentMsg msg = new BasicContentMsg(header, new TriggerMsg(message));
            trigger(msg, networkPort);
        }
    };



    public static class Init extends se.sics.kompics.Init<se.kth.app.sim.SimulationClient> {

        public final KAddress selfAdr;
        public final String ip;
        public final int id;
        public final String msg;

        public Init(KAddress selfAdr, String ip, int id, String msg) {
            this.selfAdr = selfAdr;
            this.ip = ip;
            this.id = id;
            this.msg = msg;
        }
    }
}
