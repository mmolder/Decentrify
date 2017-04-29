package se.kth.app.sim;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.app.test.Ping;
import se.kth.app.test.Pong;
import se.kth.causalbroadcast.CRBDeliver;
import se.kth.causalbroadcast.CRBPort;
import se.kth.causalbroadcast.CRBroadcast;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;
import se.sics.ktoolbox.util.identifiable.Identifier;
import se.sics.ktoolbox.util.network.KAddress;
import se.sics.ktoolbox.util.network.KContentMsg;
import se.sics.ktoolbox.util.network.KHeader;

/**
 * Created by Mikael on 2017-04-29.
 */
public class SimulationClient extends ComponentDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(se.kth.app.sim.SimulationClient.class);
    private String logPrefix = " ";

    //*******************************CONNECTIONS********************************
    Positive<Timer> timerPort = requires(Timer.class);
    Positive<Network> networkPort = requires(Network.class);
    //Positive<CroupierPort> croupierPort = requires(CroupierPort.class);

    Positive<CRBPort> crb = requires(CRBPort.class);
    //**************************************************************************
    private KAddress selfAdr;

    public SimulationClient(se.kth.app.AppComp.Init init) {
        selfAdr = init.selfAdr;
        logPrefix = "<nid:" + selfAdr.getId() + ">";
        LOG.info("{}initiating...", logPrefix);

        subscribe(handleStart, control);
        //subscribe(handleCroupierSample, croupierPort);
        subscribe(crbDeliverHandler, crb);
    }

    Handler handleStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            LOG.info("{}starting...", logPrefix);
            trigger(new CRBroadcast("testing"), crb);
        }
    };

    Handler crbDeliverHandler = new Handler<CRBDeliver>() {
        @Override
        public void handle(CRBDeliver crbDeliver) {
            System.out.println(selfAdr.getId() + " RECEIVED BROADCAST");
        }
    };

    public static class Init extends se.sics.kompics.Init<se.kth.app.sim.SimulationClient> {

        public final KAddress selfAdr;

        public Init(KAddress selfAdr) {
            this.selfAdr = selfAdr;
        }
    }
}
