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
import se.kth.graph.*;
import se.kth.growonlyset.*;
import se.kth.observedremovedset.ORSet;
import se.kth.observedremovedset.OR_Add;
import se.kth.observedremovedset.OR_Remove;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;
import se.sics.ktoolbox.util.identifiable.Identifier;
import se.sics.ktoolbox.util.network.KAddress;
import se.sics.ktoolbox.util.network.KContentMsg;
import se.sics.ktoolbox.util.network.KHeader;

import java.util.ArrayList;

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
    private ORSet orSet;
    private TwoPhaseGraph graph;

    public AppComp(Init init) {
        selfAdr = init.selfAdr;
        logPrefix = "<nid:" + selfAdr.getId() + ">";
        //LOG.info("{}initiating...", logPrefix);

        mySet = new GSet();
        twoPhaseSet = new TwoPhaseSet();
        orSet = new ORSet();
        graph = new TwoPhaseGraph();

        subscribe(handleStart, control);
        subscribe(crbDeliverHandler, crb);
        subscribe(simulationMsgHandler, networkPort);
        subscribe(gSetAddHandler, networkPort);
        subscribe(twpPAddHandler, networkPort);
        subscribe(twoPRemoveHandler, networkPort);
        subscribe(oraddhandler, networkPort);
        subscribe(orremovehandler, networkPort);
        subscribe(addvertexhandler, networkPort);
        subscribe(removevertexhandler, networkPort);
        subscribe(addedgehandler, networkPort);
        subscribe(removeedgehandler, networkPort);
    }

    Handler handleStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            //LOG.info("{}starting...", logPrefix);
        }
    };

    ClassMatchedHandler simulationMsgHandler = new ClassMatchedHandler<TriggerMsg, KContentMsg<?, KHeader<?>, TriggerMsg>>() {
        @Override
        public void handle(TriggerMsg msg, KContentMsg<?, KHeader<?>, TriggerMsg> cont) {
            trigger(new CRBroadcast(cont.getContent().payload), crb);
        }
    };

    ClassMatchedHandler gSetAddHandler = new ClassMatchedHandler<GSet_Add, KContentMsg<?, KHeader<?>, GSet_Add>>() {
        @Override
        public void handle(GSet_Add op, KContentMsg<?, KHeader<?>, GSet_Add> msg) {
            Object element = op.element;
            if(twoPhaseSet.add(element)) {
                System.out.println("Added " + element + " to source set, now contains: " + twoPhaseSet.print());
            }
            trigger(new CRBroadcast(op), crb);
        }
    };

    ClassMatchedHandler twpPAddHandler = new ClassMatchedHandler<TwoP_Add, KContentMsg<?, KHeader<?>, TwoP_Add>>() {
        @Override
        public void handle(TwoP_Add op, KContentMsg<?, KHeader<?>, TwoP_Add> msg) {
            Object element = op.element;
            if(mySet.add(element)) {
                System.out.println("Added " + element + " to source set");
            }
            trigger(new CRBroadcast(op), crb);
        }
    };

    ClassMatchedHandler twoPRemoveHandler = new ClassMatchedHandler<TwoP_Remove, KContentMsg<?, KHeader<?>, TwoP_Remove>>() {
        @Override
        public void handle(TwoP_Remove op, KContentMsg<?, KHeader<?>, TwoP_Remove> msg) {
            if(twoPhaseSet.remove(op.element)) {
                System.out.println("Removed " + op.element + " from source set, now contains: " + twoPhaseSet.print());
            }
            trigger(new CRBroadcast(op), crb);
        }
    };

    ClassMatchedHandler oraddhandler = new ClassMatchedHandler<OR_Add, KContentMsg<?, KHeader<?>, OR_Add>>() {
        @Override
        public void handle(OR_Add op, KContentMsg<?, KHeader<?>, OR_Add> msg) {
            // add to source set
            String tag = orSet.add(op.element, "");                     // get unique tag (will add it to source as well not empty string)
            //System.out.println("Adding " + op.element + " to source set with tag " + tag + ", now contains: " + orSet.print());
            trigger(new CRBroadcast(new OR_Add(op.element, tag)), crb);    // broadcast to all nodes
        }
    };

    ClassMatchedHandler orremovehandler = new ClassMatchedHandler<OR_Remove, KContentMsg<?, KHeader<?>, OR_Remove>>() {
        @Override
        public void handle(OR_Remove op, KContentMsg<?, KHeader<?>, OR_Remove> msg) {
            // remove from source set
            ArrayList<String> tags = orSet.remove(op.element, new ArrayList<String>());     // get list of tags associated with element
            //System.out.println("Removed " + op.element + " from source set with tags: " + tags + ", now contains: " + orSet.print());
            trigger(new CRBroadcast(new OR_Remove(op.element, tags)), crb);                 // broadcast to all nodes
        }
    };

    ClassMatchedHandler addvertexhandler = new ClassMatchedHandler<AddVertex, KContentMsg<?, KHeader<?>, AddVertex>>() {
        @Override
        public void handle(AddVertex op, KContentMsg<?, KHeader<?>, AddVertex> msg) {
            // only trigger if preconditions hold
            if(graph.addVertex(op.w, true)) {
                trigger(new CRBroadcast(new AddVertex(op.w)), crb);
            } else {
                System.out.println("Preconditions does not hold for add vertex");
            }
        }
    };

    ClassMatchedHandler removevertexhandler = new ClassMatchedHandler<RemoveVertex, KContentMsg<?, KHeader<?>, RemoveVertex>>() {
        @Override
        public void handle(RemoveVertex op, KContentMsg<?, KHeader<?>, RemoveVertex> msg) {
            // only trigger if preconditions hold
            if(graph.removeVertex(op.w, true)) {
                trigger(new CRBroadcast(new RemoveVertex(op.w)), crb);
            } else {
                System.out.println("Preconditions does not hold for remove vertex");
            }
        }
    };

    ClassMatchedHandler addedgehandler = new ClassMatchedHandler<AddEdge, KContentMsg<?, KHeader<?>, AddEdge>>() {
        @Override
        public void handle(AddEdge op, KContentMsg<?, KHeader<?>, AddEdge> msg) {
            // only trigger if preconditions hold
            if(graph.addEdge(op.e, true)) {
                trigger(new CRBroadcast(new AddEdge(op.e)), crb);
            } else {
                System.out.println("Preconditions does not hold for add edge");
            }
        }
    };

    ClassMatchedHandler removeedgehandler = new ClassMatchedHandler<RemoveEdge, KContentMsg<?, KHeader<?>, RemoveEdge>>() {
        @Override
        public void handle(RemoveEdge op, KContentMsg<?, KHeader<?>, RemoveEdge> msg) {
            // only trigger if preconditions hold
            if(graph.removeEdge(op.e, true)) {
                trigger(new CRBroadcast(new RemoveEdge(op.e)), crb);
            } else {
                System.out.println("Preconditions does not hold for remove edge");
            }
        }
    };

    Handler crbDeliverHandler = new Handler<CRBDeliver>() {
        @Override
        public void handle(CRBDeliver crbDeliver) {
            /** GROW ONLY SET ADD */
            if(crbDeliver.payload instanceof GSet_Add) {
                GSet_Add addOp = (GSet_Add)crbDeliver.payload;
                System.out.println(selfAdr + " received ADD, adding " + addOp.element);
                if(mySet.add(addOp.element)) {
                    System.out.println(selfAdr + " my set now contains: " + mySet.print());
                }
            }
            /** 2 PHASE SET ADD */
            if(crbDeliver.payload instanceof TwoP_Add) {
                TwoP_Add addOp = (TwoP_Add)crbDeliver.payload;
                System.out.println(selfAdr + " received TWOP_ADD, adding " + addOp.element);
                if(twoPhaseSet.add(addOp.element)) {
                    System.out.println(selfAdr + " my set now contains: " + twoPhaseSet.print());
                }
            }
            /** 2 PHASE SET REMOVE */
            else if(crbDeliver.payload instanceof TwoP_Remove) {
                TwoP_Remove removeOp = (TwoP_Remove)crbDeliver.payload;
                System.out.println(selfAdr + " received TWOP_REMOVE, removing " + removeOp.element);
                if(twoPhaseSet.remove(removeOp.element)) {
                    System.out.println(selfAdr + " my set now contains: " + twoPhaseSet.print());
                }
            }
            /** OBSERVE REMOVE SET ADD */
            else if(crbDeliver.payload instanceof OR_Add) {
                OR_Add orAddOp = (OR_Add)crbDeliver.payload;
                System.out.println(selfAdr + " received OR_ADD, adding " + orAddOp.element + " " + orAddOp.tag + ", before: " + orSet.print());
                orSet.add(orAddOp.element, orAddOp.tag);
                System.out.println(selfAdr + " my set now contains: " + orSet.print());
            }
            /** OBSERVE REMOVE SET REMOVE */
            else if(crbDeliver.payload instanceof  OR_Remove) {
                OR_Remove orRemoveOp = (OR_Remove)crbDeliver.payload;
                System.out.println(selfAdr + " received OR_REMOVE, removing " + orRemoveOp.element + " " + orRemoveOp.tags + ", before: " + orSet.print());
                orSet.remove(orRemoveOp.element, orRemoveOp.tags);
                System.out.println(selfAdr + " my set now contains: " + orSet.print());
            }
            /** 2P2P GRAPH ADD VERTEX */
            else if(crbDeliver.payload instanceof AddVertex) {
                AddVertex addVertex = (AddVertex)crbDeliver.payload;
                System.out.println(selfAdr + " received ADD_VERTEX , adding " + addVertex.w);
                graph.addVertex(addVertex.w, false);
                System.out.println(selfAdr + " my graph is: " + graph.print());

            }
            /** 2P2P GRAPH REMOVE VERTEX */
            else if(crbDeliver.payload instanceof RemoveVertex) {
                RemoveVertex removeVertex = (RemoveVertex)crbDeliver.payload;
                System.out.println(selfAdr + " received REMOVE_VERTEX, removing " + removeVertex.w);
                graph.removeVertex(removeVertex.w, false);
                System.out.println(selfAdr + " my graph is: " + graph.print());
            }
            /** 2P2P GRAPH ADD EDGE */
            else if(crbDeliver.payload instanceof AddEdge) {
                AddEdge addEdge = (AddEdge)crbDeliver.payload;
                System.out.println(selfAdr + " received ADD_EDGE, adding " + addEdge.e);
                graph.addEdge(addEdge.e, false);
                System.out.println(selfAdr + " my graph is: " + graph.print());
                graph.printDetails();
            }
            /** 2P2P GRAPH REMOVE EDGE */
            else if(crbDeliver.payload instanceof RemoveEdge) {
                RemoveEdge removeEdge = (RemoveEdge)crbDeliver.payload;
                System.out.println(selfAdr + " received REMOVE_EDGE, removing " + removeEdge.e);
                graph.removeEdge(removeEdge.e, false);
                System.out.println(selfAdr + " my graph is: " + graph.print());
                graph.printDetails();
            }
            /** OPERATION UNKNOWN */
            else {
                //System.out.println("Operation not recognized: " + crbDeliver.payload);
                System.out.println("BROADCAST RECEIVED: " + crbDeliver.payload + ", SOURCE: " +  crbDeliver.source + ", SELF: " + selfAdr);
            }

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
