/*
 * 2016 Royal Institute of Technology (KTH)
 *
 * LSelector is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package se.kth.app;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.app.test.TriggerMsg;
import se.kth.causalbroadcast.CRBDeliver;
import se.kth.causalbroadcast.CRBPort;
import se.kth.causalbroadcast.CRBroadcast;
import se.kth.croupier.util.CroupierHelper;
import se.kth.app.test.Ping;
import se.kth.app.test.Pong;
import se.kth.gossipingbroadcast.HistoryResponse;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.Transport;
import se.sics.kompics.timer.Timer;
import se.sics.ktoolbox.croupier.CroupierPort;
import se.sics.ktoolbox.croupier.event.CroupierSample;
import se.sics.ktoolbox.util.identifiable.Identifier;
import se.sics.ktoolbox.util.network.KAddress;
import se.sics.ktoolbox.util.network.KContentMsg;
import se.sics.ktoolbox.util.network.KHeader;
import se.sics.ktoolbox.util.network.basic.BasicContentMsg;
import se.sics.ktoolbox.util.network.basic.BasicHeader;

/**
 * @author Alex Ormenisan <aaor@kth.se>
 */
public class AppComp extends ComponentDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(AppComp.class);
    private String logPrefix = " ";

    //*******************************CONNECTIONS********************************
    Positive<Timer> timerPort = requires(Timer.class);
    Positive<Network> networkPort = requires(Network.class);

    Positive<CRBPort> crb = requires(CRBPort.class);
    //**************************************************************************
    private KAddress selfAdr;

    public AppComp(Init init) {
        selfAdr = init.selfAdr;
        logPrefix = "<nid:" + selfAdr.getId() + ">";
        LOG.info("{}initiating...", logPrefix);

        subscribe(handleStart, control);
        subscribe(crbDeliverHandler, crb);
        subscribe(simulationMsgHandler, networkPort);
    }

    Handler handleStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            LOG.info("{}starting...", logPrefix);
            //trigger(new CRBroadcast("request"), crb);
        }
    };

    ClassMatchedHandler simulationMsgHandler = new ClassMatchedHandler<TriggerMsg, KContentMsg<?, KHeader<?>, TriggerMsg>>() {
        @Override
        public void handle(TriggerMsg msg, KContentMsg<?, KHeader<?>, TriggerMsg> cont) {
            trigger(new CRBroadcast(cont.getContent().getMsg()), crb);
        }
    };

    Handler crbDeliverHandler = new Handler<CRBDeliver>() {
        @Override
        public void handle(CRBDeliver crbDeliver) {
            System.out.println("BROADCAST RECEIVED: " + crbDeliver.getMessage() + ", SOURCE: " +  crbDeliver.getSource() + ", SELF: " + selfAdr);
            //trigger(new CRBroadcast("response"), crb);
            //LOG.info("{} broadcast received: {}", selfAdr, crbDeliver.getMessage());
        }
    };

    public static class Init extends se.sics.kompics.Init<AppComp> {

        public final KAddress selfAdr;
        public final Identifier gradientOId;

        public Init(KAddress selfAdr, Identifier gradientOId) {
            this.selfAdr = selfAdr;
            this.gradientOId = gradientOId;
        }
    }
}
