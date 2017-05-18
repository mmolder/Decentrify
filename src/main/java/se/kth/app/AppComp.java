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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.app.test.TriggerMsg;
import se.kth.causalbroadcast.CRBDeliver;
import se.kth.causalbroadcast.CRBPort;
import se.kth.causalbroadcast.CRBroadcast;
import se.kth.growonlyset.Add;
import se.kth.growonlyset.GSet;
import se.kth.growonlyset.Remove;
import se.kth.growonlyset.TwoPhaseSet;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;
import se.sics.ktoolbox.util.identifiable.Identifier;
import se.sics.ktoolbox.util.network.KAddress;
import se.sics.ktoolbox.util.network.KContentMsg;
import se.sics.ktoolbox.util.network.KHeader;

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
    private GSet mySet;
    private TwoPhaseSet twoPhaseSet;

    public AppComp(Init init) {
        selfAdr = init.selfAdr;
        logPrefix = "<nid:" + selfAdr.getId() + ">";
        LOG.info("{}initiating...", logPrefix);

        mySet = new GSet();
        twoPhaseSet = new TwoPhaseSet();

        subscribe(handleStart, control);
        subscribe(crbDeliverHandler, crb);
        subscribe(simulationMsgHandler, networkPort);
        subscribe(addOperationHandler, networkPort);
        subscribe(removeOperationHandler, networkPort);
    }

    Handler handleStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            LOG.info("{}starting...", logPrefix);
        }
    };

    ClassMatchedHandler simulationMsgHandler = new ClassMatchedHandler<TriggerMsg, KContentMsg<?, KHeader<?>, TriggerMsg>>() {
        @Override
        public void handle(TriggerMsg msg, KContentMsg<?, KHeader<?>, TriggerMsg> cont) {
            trigger(new CRBroadcast(cont.getContent().getMsg()), crb);
        }
    };

    ClassMatchedHandler addOperationHandler = new ClassMatchedHandler<Add, KContentMsg<?, KHeader<?>, Add>>() {
        @Override
        public void handle(Add msg, KContentMsg<?, KHeader<?>, Add> cont) {
            //System.out.println("Add, Msg: " + msg + " other: " + cont.getContent());
            trigger(new CRBroadcast(cont.getContent()), crb);
        }
    };

    ClassMatchedHandler removeOperationHandler = new ClassMatchedHandler<Remove, KContentMsg<?, KHeader<?>, Remove>>() {
        @Override
        public void handle(Remove msg, KContentMsg<?, KHeader<?>, Remove> cont) {
            //System.out.println("Remove, Msg: " + msg + " other: " + cont.getContent());
            trigger(new CRBroadcast(cont.getContent()), crb);
        }
    };

    Handler crbDeliverHandler = new Handler<CRBDeliver>() {
        @Override
        public void handle(CRBDeliver crbDeliver) {
            if(crbDeliver.payload instanceof Add) {
                Add addOp = (Add)crbDeliver.payload;
                System.out.println(selfAdr + " received ADD, adding " + addOp.element);
                //mySet.add(addOp.element);
                twoPhaseSet.add(addOp.element);
                System.out.println(selfAdr + " my set now contains: " + twoPhaseSet.print());
            }
            else if(crbDeliver.payload instanceof Remove) {
                Remove removeOp = (Remove)crbDeliver.payload;
                System.out.println(selfAdr + " received REMOVE, removing " + removeOp.element);
                twoPhaseSet.remove(removeOp.element);
                System.out.println(selfAdr + " my set now contains: " + twoPhaseSet.print());
            }
            else {
                System.out.println("Operation not recognized: " + crbDeliver.payload);
            }
            //System.out.println("BROADCAST RECEIVED: " + crbDeliver.getMessage() + ", SOURCE: " +  crbDeliver.getSource() + ", SELF: " + selfAdr);
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
