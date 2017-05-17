package se.kth.app.sim;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.causalbroadcast.CRBPort;
import se.kth.growonlyset.Add;
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
public class OperationClient extends ComponentDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(se.kth.app.sim.OperationClient.class);
    private String logPrefix = " ";

    //*******************************CONNECTIONS********************************
    Positive<Timer> timerPort = requires(Timer.class);
    Positive<Network> networkPort = requires(Network.class);

    Positive<CRBPort> crb = requires(CRBPort.class);
    //**************************************************************************
    private KAddress selfAdr;
    private String ip;
    private int id;

    public OperationClient(OperationClient.Init init) {
        selfAdr = init.selfAdr;
        ip = init.ip;
        id = init.id;
        subscribe(handleStart, control);
    }

    Handler handleStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            KAddress peer = ScenarioSetup.getNodeAdr(ip, id);
            KHeader header = new BasicHeader(selfAdr, peer, Transport.UDP);
            KContentMsg msg = new BasicContentMsg(header, new Add("hello"));
            trigger(msg, networkPort);

            //msg = new BasicContentMsg(header, new Remove("hello"));
        }
    };



    public static class Init extends se.sics.kompics.Init<se.kth.app.sim.OperationClient> {

        public final KAddress selfAdr;
        public final String ip;
        public final int id;

        public Init(KAddress selfAdr, String ip, int id) {
            this.selfAdr = selfAdr;
            this.ip = ip;
            this.id = id;
        }
    }
}
