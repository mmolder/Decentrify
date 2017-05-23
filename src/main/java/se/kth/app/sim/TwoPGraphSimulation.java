package se.kth.app.sim;

import se.kth.graph.Edge;
import se.kth.graph.Vertex;
import se.kth.sim.compatibility.SimNodeIdExtractor;
import se.kth.system.HostMngrComp;
import se.sics.kompics.network.Address;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.adaptor.Operation;
import se.sics.kompics.simulator.adaptor.Operation1;
import se.sics.kompics.simulator.adaptor.Operation3;
import se.sics.kompics.simulator.adaptor.distributions.ConstantDistribution;
import se.sics.kompics.simulator.adaptor.distributions.extra.BasicIntSequentialDistribution;
import se.sics.kompics.simulator.events.system.KillNodeEvent;
import se.sics.kompics.simulator.events.system.SetupEvent;
import se.sics.kompics.simulator.events.system.StartNodeEvent;
import se.sics.kompics.simulator.network.identifier.IdentifierExtractor;
import se.sics.ktoolbox.omngr.bootstrap.BootstrapServerComp;
import se.sics.ktoolbox.util.network.KAddress;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mikael on 2017-05-21.
 */
public class TwoPGraphSimulation {

    static Vertex v1 = new Vertex(1);
    static Vertex v2 = new Vertex(2);
    static Vertex v3 = new Vertex(3);
    static Edge e1 = new Edge(v1, v2);
    static Edge e2 = new Edge(v2, v3);
    static Edge e3 = new Edge(v3, v1);

    static Operation<SetupEvent> systemSetupOp = new Operation<SetupEvent>() {
        @Override
        public SetupEvent generate() {
            return new SetupEvent() {
                @Override
                public IdentifierExtractor getIdentifierExtractor() {
                    return new SimNodeIdExtractor();
                }
            };
        }
    };

    static Operation<StartNodeEvent> startBootstrapServerOp = new Operation<StartNodeEvent>() {

        @Override
        public StartNodeEvent generate() {
            return new StartNodeEvent() {
                KAddress selfAdr;

                {
                    selfAdr = ScenarioSetup.bootstrapServer;
                }

                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }

                @Override
                public Class getComponentDefinition() {
                    return BootstrapServerComp.class;
                }

                @Override
                public BootstrapServerComp.Init getComponentInit() {
                    return new BootstrapServerComp.Init(selfAdr);
                }
            };
        }
    };

    /**
     *
     * startNodeOp
     *
     * Starts an AppComp node with the ip 193.0.0.<nodeID>
     *
     **/
    static Operation1<StartNodeEvent, Integer> startNodeOp = new Operation1<StartNodeEvent, Integer>() {

        @Override
        public StartNodeEvent generate(final Integer nodeId) {
            return new StartNodeEvent() {
                KAddress selfAdr;

                {
                    String nodeIp = "193.0.0." + nodeId;
                    selfAdr = ScenarioSetup.getNodeAdr(nodeIp, nodeId);
                    System.out.println("Starting node: " + nodeIp);
                }

                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }

                @Override
                public Class getComponentDefinition() {
                    //return SimulationClient.class;
                    return HostMngrComp.class;
                }

                @Override
                public HostMngrComp.Init getComponentInit() {
                    //return new SimulationClient.Init(selfAdr);
                    return new HostMngrComp.Init(selfAdr, ScenarioSetup.bootstrapServer, ScenarioSetup.croupierOId);
                }

                @Override
                public Map<String, Object> initConfigUpdate() {
                    Map<String, Object> nodeConfig = new HashMap<>();
                    nodeConfig.put("system.id", nodeId);
                    nodeConfig.put("system.seed", ScenarioSetup.getNodeSeed(nodeId));
                    nodeConfig.put("system.port", ScenarioSetup.appPort);
                    return nodeConfig;
                }
            };
        }
    };


    /**
     *
     * startSpecialNode
     *
     * Starts an special node which will trigger the node with address 193.0.0.<target>
     * to broadcast a message
     *
     **/
    static Operation3 startSpecialNode = new Operation3<StartNodeEvent, Integer, Integer, Integer>() {

        @Override
        public StartNodeEvent generate(final Integer self, final Integer target, final Integer settype) {
            return new StartNodeEvent() {
                final KAddress selfAdr;
                {
                    selfAdr = ScenarioSetup.getNodeAdr("193.0.0." + self, self);
                    System.out.println("Starting node: " + selfAdr);
                }

                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }

                @Override
                public Class getComponentDefinition() {
                    return TwoPGraphClient.class;
                }

                @Override
                public String toString() {
                    return "StartClient<" + selfAdr.toString() + ">";
                }

                @Override
                public TwoPGraphClient.Init getComponentInit() {
                    return new TwoPGraphClient.Init(selfAdr, "193.0.0." + target, target, settype, target);
                }
            };
        }
    };

    /**
     *
     * vertexNode1
     *
     * Starts a special node which will broadcast operations done with vertex v1
     */
    static Operation3 vertexNode1 = new Operation3<StartNodeEvent, Integer, Integer, Integer>() {

        @Override
        public StartNodeEvent generate(final Integer self, final Integer target, final Integer settype) {
            return new StartNodeEvent() {
                final KAddress selfAdr;
                {
                    selfAdr = ScenarioSetup.getNodeAdr("193.0.0." + self, self);
                    System.out.println("Starting node: " + selfAdr);
                }

                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }

                @Override
                public Class getComponentDefinition() {
                    return TwoPGraphClient.class;
                }

                @Override
                public String toString() {
                    return "StartClient<" + selfAdr.toString() + ">";
                }

                @Override
                public TwoPGraphClient.Init getComponentInit() {
                    return new TwoPGraphClient.Init(selfAdr, "193.0.0." + target, target, settype, v1);
                }
            };
        }
    };

    /**
     *
     * vertexNode2
     *
     * Starts a special node which will broadcast operations done with vertex v2
     */
    static Operation3 vertexNode2 = new Operation3<StartNodeEvent, Integer, Integer, Integer>() {

        @Override
        public StartNodeEvent generate(final Integer self, final Integer target, final Integer settype) {
            return new StartNodeEvent() {
                final KAddress selfAdr;
                {
                    selfAdr = ScenarioSetup.getNodeAdr("193.0.0." + self, self);
                    System.out.println("Starting node: " + selfAdr);
                }

                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }

                @Override
                public Class getComponentDefinition() {
                    return TwoPGraphClient.class;
                }

                @Override
                public String toString() {
                    return "StartClient<" + selfAdr.toString() + ">";
                }

                @Override
                public TwoPGraphClient.Init getComponentInit() {
                    return new TwoPGraphClient.Init(selfAdr, "193.0.0." + target, target, settype, v2);
                }
            };
        }
    };

    /**
     *
     * vertexNode3
     *
     * Starts a special node which will broadcast operations done with vertex v3
     */
    static Operation3 vertexNode3 = new Operation3<StartNodeEvent, Integer, Integer, Integer>() {

        @Override
        public StartNodeEvent generate(final Integer self, final Integer target, final Integer settype) {
            return new StartNodeEvent() {
                final KAddress selfAdr;
                {
                    selfAdr = ScenarioSetup.getNodeAdr("193.0.0." + self, self);
                    System.out.println("Starting node: " + selfAdr);
                }

                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }

                @Override
                public Class getComponentDefinition() {
                    return TwoPGraphClient.class;
                }

                @Override
                public String toString() {
                    return "StartClient<" + selfAdr.toString() + ">";
                }

                @Override
                public TwoPGraphClient.Init getComponentInit() {
                    return new TwoPGraphClient.Init(selfAdr, "193.0.0." + target, target, settype, v3);
                }
            };
        }
    };

    /**
     *
     * edgeNode1
     *
     * Starts a special node which will broadcast operations done with edge e1
     */
    static Operation3 edgeNode1 = new Operation3<StartNodeEvent, Integer, Integer, Integer>() {

        @Override
        public StartNodeEvent generate(final Integer self, final Integer target, final Integer settype) {
            return new StartNodeEvent() {
                final KAddress selfAdr;
                {
                    selfAdr = ScenarioSetup.getNodeAdr("193.0.0." + self, self);
                    System.out.println("Starting node: " + selfAdr);
                }

                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }

                @Override
                public Class getComponentDefinition() {
                    return TwoPGraphClient.class;
                }

                @Override
                public String toString() {
                    return "StartClient<" + selfAdr.toString() + ">";
                }

                @Override
                public TwoPGraphClient.Init getComponentInit() {
                    return new TwoPGraphClient.Init(selfAdr, "193.0.0." + target, target, settype, e1);
                }
            };
        }
    };

    /**
     *
     * edgeNode2
     *
     * Starts a special node which will broadcast operations done with edge e2
     */
    static Operation3 edgeNode2 = new Operation3<StartNodeEvent, Integer, Integer, Integer>() {

        @Override
        public StartNodeEvent generate(final Integer self, final Integer target, final Integer settype) {
            return new StartNodeEvent() {
                final KAddress selfAdr;
                {
                    selfAdr = ScenarioSetup.getNodeAdr("193.0.0." + self, self);
                    System.out.println("Starting node: " + selfAdr);
                }

                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }

                @Override
                public Class getComponentDefinition() {
                    return TwoPGraphClient.class;
                }

                @Override
                public String toString() {
                    return "StartClient<" + selfAdr.toString() + ">";
                }

                @Override
                public TwoPGraphClient.Init getComponentInit() {
                    return new TwoPGraphClient.Init(selfAdr, "193.0.0." + target, target, settype, e2);
                }
            };
        }
    };

    /**
     *
     * edgeNode3
     *
     * Starts a special node which will broadcast operations done with edge e3
     */
    static Operation3 edgeNode3 = new Operation3<StartNodeEvent, Integer, Integer, Integer>() {

        @Override
        public StartNodeEvent generate(final Integer self, final Integer target, final Integer settype) {
            return new StartNodeEvent() {
                final KAddress selfAdr;
                {
                    selfAdr = ScenarioSetup.getNodeAdr("193.0.0." + self, self);
                    System.out.println("Starting node: " + selfAdr);
                }

                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }

                @Override
                public Class getComponentDefinition() {
                    return TwoPGraphClient.class;
                }

                @Override
                public String toString() {
                    return "StartClient<" + selfAdr.toString() + ">";
                }

                @Override
                public TwoPGraphClient.Init getComponentInit() {
                    return new TwoPGraphClient.Init(selfAdr, "193.0.0." + target, target, settype, e3);
                }
            };
        }
    };


    /**
     *
     * killNodeOp
     *
     * Kills the node with address 193.0.0.<nodeId>
     *
     **/
    private static final Operation1 killNodeOp = new Operation1<KillNodeEvent, Integer>() {
        @Override
        public KillNodeEvent generate(final Integer nodeID) {
            return new KillNodeEvent() {
                KAddress selfAdr;

                {
                    String nodeIp = "193.0.0." + nodeID;
                    selfAdr = ScenarioSetup.getNodeAdr(nodeIp, nodeID);
                    System.out.println("Killing node: " + nodeIp);
                }

                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }
            };
        }
    };

    /**
     *
     * twopgraphtest1
     *
     **/
    public static SimulationScenario twopgraphtest1() {
        SimulationScenario scen = new SimulationScenario() {
            {
                StochasticProcess systemSetup = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, systemSetupOp);
                    }
                };
                StochasticProcess startBootstrapServer = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, startBootstrapServerOp);
                    }
                };
                StochasticProcess startPeers = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1100));
                        raise(10, startNodeOp, new BasicIntSequentialDistribution(1));
                    }
                };
                StochasticProcess startSpecial = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, vertexNode1, new ConstantDistribution<>(Integer.class, 11), new ConstantDistribution<>(Integer.class, 5), new ConstantDistribution<>(Integer.class, 0));
                    }
                };
                StochasticProcess startSpecial2 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, vertexNode2, new ConstantDistribution<>(Integer.class, 12), new ConstantDistribution<>(Integer.class, 5), new ConstantDistribution<>(Integer.class, 0));
                    }
                };
                StochasticProcess startSpecial3 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, vertexNode3, new ConstantDistribution<>(Integer.class, 13), new ConstantDistribution<>(Integer.class, 5), new ConstantDistribution<>(Integer.class, 0));
                    }
                };
                StochasticProcess startSpecial4 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, edgeNode1, new ConstantDistribution<>(Integer.class, 14), new ConstantDistribution<>(Integer.class, 7), new ConstantDistribution<>(Integer.class, 3));
                    }
                };
                StochasticProcess startSpecial5 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, edgeNode2, new ConstantDistribution<>(Integer.class, 15), new ConstantDistribution<>(Integer.class, 7), new ConstantDistribution<>(Integer.class, 3));
                    }
                };
                StochasticProcess startSpecial6 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, edgeNode3, new ConstantDistribution<>(Integer.class, 16), new ConstantDistribution<>(Integer.class, 7), new ConstantDistribution<>(Integer.class, 3));
                    }
                };

                systemSetup.start();
                /** Start 10 normal nodes */
                startBootstrapServer.startAfterTerminationOf(1000, systemSetup);
                startPeers.startAfterTerminationOf(1000, startBootstrapServer);
                /** Start special nodes */
                startSpecial.startAfterTerminationOf(10000, startPeers);        // add vertex v1
                startSpecial2.startAfterTerminationOf(10000, startSpecial);     // add vertex v2
                startSpecial3.startAfterTerminationOf(10000, startSpecial2);    // add vertex v3
                startSpecial4.startAfterTerminationOf(10000, startSpecial3);    // add edge   v1->v2
                startSpecial5.startAfterTerminationOf(10000, startSpecial4);    // add edge   v2->v3
                startSpecial6.startAfterTerminationOf(10000, startSpecial5);    // add edge   v3->v1

                terminateAfterTerminationOf(1000*1000, startSpecial6);
            }
        };

        return scen;
    }

    /**
     *
     * twopgraphtest2
     *
     **/
    public static SimulationScenario twopgraphtest2() {
        SimulationScenario scen = new SimulationScenario() {
            {
                StochasticProcess systemSetup = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, systemSetupOp);
                    }
                };
                StochasticProcess startBootstrapServer = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, startBootstrapServerOp);
                    }
                };
                StochasticProcess startPeers = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1100));
                        raise(10, startNodeOp, new BasicIntSequentialDistribution(1));
                    }
                };
                StochasticProcess startSpecial = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, vertexNode1, new ConstantDistribution<>(Integer.class, 11), new ConstantDistribution<>(Integer.class, 3), new ConstantDistribution<>(Integer.class, 0));
                    }
                };
                StochasticProcess startSpecial2 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, vertexNode2, new ConstantDistribution<>(Integer.class, 12), new ConstantDistribution<>(Integer.class, 3), new ConstantDistribution<>(Integer.class, 0));
                    }
                };
                StochasticProcess startSpecial3 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, vertexNode3, new ConstantDistribution<>(Integer.class, 13), new ConstantDistribution<>(Integer.class, 3), new ConstantDistribution<>(Integer.class, 0));
                    }
                };
                StochasticProcess startSpecial4 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, edgeNode1, new ConstantDistribution<>(Integer.class, 14), new ConstantDistribution<>(Integer.class, 7), new ConstantDistribution<>(Integer.class, 2));
                    }
                };
                StochasticProcess startSpecial5 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, edgeNode2, new ConstantDistribution<>(Integer.class, 15), new ConstantDistribution<>(Integer.class, 7), new ConstantDistribution<>(Integer.class, 2));
                    }
                };
                StochasticProcess startSpecial6 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, edgeNode3, new ConstantDistribution<>(Integer.class, 16), new ConstantDistribution<>(Integer.class, 7), new ConstantDistribution<>(Integer.class, 2));
                    }
                };
                StochasticProcess startSpecial7 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, vertexNode1, new ConstantDistribution<>(Integer.class, 17), new ConstantDistribution<>(Integer.class, 1), new ConstantDistribution<>(Integer.class, 1));
                    }
                };

                systemSetup.start();
                /** Start 10 normal nodes */
                startBootstrapServer.startAfterTerminationOf(1000, systemSetup);
                startPeers.startAfterTerminationOf(1000, startBootstrapServer);
                /** Start special nodes */
                startSpecial.startAfterTerminationOf(10000, startPeers);         // add vertex v1
                startSpecial2.startAfterTerminationOf(10000, startSpecial);      // add vertex v2
                startSpecial3.startAfterTerminationOf(10000, startSpecial2);     // add vertex v3
                startSpecial4.startAfterTerminationOf(10000, startSpecial3);    // add edge e1 v1->v2
                startSpecial5.startAfterTerminationOf(10000, startSpecial4);    // add edge e2 v2->v3
                startSpecial6.startAfterTerminationOf(10000, startSpecial5);    // add edge e3 v3->v1
                startSpecial7.startAfterTerminationOf(10000, startSpecial6);    // try to remove a vertex which is connected by an edge
                terminateAfterTerminationOf(1000*1000, startSpecial7);
            }
        };

        return scen;
    }

    /**
     *
     * twopgraphtest3
     *
     **/
    public static SimulationScenario twopgraphtest3() {
        SimulationScenario scen = new SimulationScenario() {
            {
                StochasticProcess systemSetup = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, systemSetupOp);
                    }
                };
                StochasticProcess startBootstrapServer = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, startBootstrapServerOp);
                    }
                };
                StochasticProcess startPeers = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1100));
                        raise(10, startNodeOp, new BasicIntSequentialDistribution(1));
                    }
                };
                StochasticProcess startSpecial = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, vertexNode1, new ConstantDistribution<>(Integer.class, 11), new ConstantDistribution<>(Integer.class, 3), new ConstantDistribution<>(Integer.class, 0));
                    }
                };
                StochasticProcess startSpecial2 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, vertexNode2, new ConstantDistribution<>(Integer.class, 12), new ConstantDistribution<>(Integer.class, 3), new ConstantDistribution<>(Integer.class, 0));
                    }
                };
                StochasticProcess startSpecial3 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, vertexNode3, new ConstantDistribution<>(Integer.class, 13), new ConstantDistribution<>(Integer.class, 3), new ConstantDistribution<>(Integer.class, 0));
                    }
                };
                StochasticProcess startSpecial4 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, edgeNode1, new ConstantDistribution<>(Integer.class, 14), new ConstantDistribution<>(Integer.class, 7), new ConstantDistribution<>(Integer.class, 2));
                    }
                };
                StochasticProcess startSpecial5 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, edgeNode2, new ConstantDistribution<>(Integer.class, 15), new ConstantDistribution<>(Integer.class, 7), new ConstantDistribution<>(Integer.class, 2));
                    }
                };
                StochasticProcess startSpecial6 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, edgeNode3, new ConstantDistribution<>(Integer.class, 16), new ConstantDistribution<>(Integer.class, 7), new ConstantDistribution<>(Integer.class, 2));
                    }
                };
                StochasticProcess startSpecial7 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, edgeNode1, new ConstantDistribution<>(Integer.class, 17), new ConstantDistribution<>(Integer.class, 1), new ConstantDistribution<>(Integer.class, 3));
                    }
                };
                StochasticProcess startSpecial8 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, edgeNode3, new ConstantDistribution<>(Integer.class, 18), new ConstantDistribution<>(Integer.class, 7), new ConstantDistribution<>(Integer.class, 3));
                    }
                };
                StochasticProcess startSpecial9 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, vertexNode1, new ConstantDistribution<>(Integer.class, 19), new ConstantDistribution<>(Integer.class, 7), new ConstantDistribution<>(Integer.class, 1));
                    }
                };

                systemSetup.start();
                /** Start 10 normal nodes */
                startBootstrapServer.startAfterTerminationOf(1000, systemSetup);
                startPeers.startAfterTerminationOf(1000, startBootstrapServer);
                /** Start special nodes */
                startSpecial.startAfterTerminationOf(10000, startPeers);         // add vertex v1
                startSpecial2.startAfterTerminationOf(10000, startSpecial);      // add vertex v2
                startSpecial3.startAfterTerminationOf(10000, startSpecial2);     // add vertex v3
                startSpecial4.startAfterTerminationOf(10000, startSpecial3);    // add edge e1 v1->v2
                startSpecial5.startAfterTerminationOf(10000, startSpecial4);    // add edge e2 v2->v3
                startSpecial6.startAfterTerminationOf(10000, startSpecial5);    // add edge e3 v3->v1
                startSpecial7.startAfterTerminationOf(10000, startSpecial6);    // remove edge e1 v1->v2
                startSpecial8.startAfterTerminationOf(10000, startSpecial7);    // remove edge e3 v3->v1
                startSpecial9.startAfterTerminationOf(10000, startSpecial8);    // remove edge e3 v3->v1
                terminateAfterTerminationOf(1000 * 1000, startSpecial9);
            }
        };

        return scen;
    }
}
