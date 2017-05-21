package se.kth.app.sim;

import se.kth.sim.compatibility.SimNodeIdExtractor;
import se.kth.system.HostMngrComp;
import se.sics.kompics.network.Address;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.adaptor.Operation;
import se.sics.kompics.simulator.adaptor.Operation1;
import se.sics.kompics.simulator.adaptor.Operation2;
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
 * Created by mikael on 2017-05-17.
 */
public class ORSetSimulation {

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
                    return ORSetClient.class;
                }

                @Override
                public String toString() {
                    return "StartClient<" + selfAdr.toString() + ">";
                }

                @Override
                public ORSetClient.Init getComponentInit() {
                    return new ORSetClient.Init(selfAdr, "193.0.0." + target, target, settype, "test" + target);
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
     * operationTest1
     *
     * SimulationScenario which will start 10 normal nodes and start one special node that triggers an alive node to
     * broadcast an add operation. This should eventually be received by all alive nodes on order to validate the properties
     * of the algorithm.
     *
     **/
    public static SimulationScenario orsettest1() {
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
                        raise(1, startSpecialNode, new ConstantDistribution<>(Integer.class, 11), new ConstantDistribution<>(Integer.class, 4), new ConstantDistribution<>(Integer.class, 0));
                    }
                };
                StochasticProcess startSpecial2 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, startSpecialNode, new ConstantDistribution<>(Integer.class, 12), new ConstantDistribution<>(Integer.class, 4), new ConstantDistribution<>(Integer.class, 0));
                    }
                };
                StochasticProcess startSpecial3 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, startSpecialNode, new ConstantDistribution<>(Integer.class, 13), new ConstantDistribution<>(Integer.class, 5), new ConstantDistribution<>(Integer.class, 0));
                    }
                };

                systemSetup.start();
                /** Start 10 normal nodes */
                startBootstrapServer.startAfterTerminationOf(1000, systemSetup);
                startPeers.startAfterTerminationOf(1000, startBootstrapServer);
                /** Start special nodes which sends two add operations storing two different values and one unique */
                startSpecial.startAfterTerminationOf(10000, startPeers);         // add("value4")
                startSpecial2.startAfterTerminationOf(10000, startSpecial);      // add("value4")
                startSpecial3.startAfterTerminationOf(10000, startSpecial2);     // add("value5")
                terminateAfterTerminationOf(1000*1000, startSpecial3);
            }
        };

        return scen;
    }

    /**
     *
     * operationTest2
     *
     * SimulationScenario which will start 10 normal nodes and start one special node that triggers an alive node to
     * broadcast an add operation. Thereafter a remove operation ios triggered which will remove the object again.
     * The operations should eventually be received by all alive nodes on order to validate the properties
     * of the algorithms.
     *
     **/
    public static SimulationScenario orsettest2() {
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
                        raise(1, startSpecialNode, new ConstantDistribution<>(Integer.class, 11), new ConstantDistribution<>(Integer.class, 4), new ConstantDistribution<>(Integer.class, 0));
                    }
                };
                StochasticProcess startSpecial2 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, startSpecialNode, new ConstantDistribution<>(Integer.class, 12), new ConstantDistribution<>(Integer.class, 4), new ConstantDistribution<>(Integer.class, 0));
                    }
                };
                StochasticProcess startSpecial3 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, startSpecialNode, new ConstantDistribution<>(Integer.class, 13), new ConstantDistribution<>(Integer.class, 6), new ConstantDistribution<>(Integer.class, 0));
                    }
                };
                StochasticProcess startSpecial4 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, startSpecialNode, new ConstantDistribution<>(Integer.class, 14), new ConstantDistribution<>(Integer.class, 4), new ConstantDistribution<>(Integer.class, 1));
                    }
                };

                systemSetup.start();
                /** Start 10 normal nodes */
                startBootstrapServer.startAfterTerminationOf(1000, systemSetup);
                startPeers.startAfterTerminationOf(1000, startBootstrapServer);
                /** Start special nodes which will store 2 duplicate values and one lone, of of the duplicates then gets removed */
                startSpecial.startAfterTerminationOf(1000, startPeers);         // add("value4")
                startSpecial2.startAfterTerminationOf(1000, startSpecial);      // add("value4")
                startSpecial3.startAfterTerminationOf(1000, startSpecial2);     // add("value6")
                startSpecial4.startAfterTerminationOf(1000000, startSpecial3);  // remove("value4")
                terminateAfterTerminationOf(1000*1000, startSpecial4);
            }
        };

        return scen;
    }

    /**
     *
     * operationTest3
     *
     * SimulationScenario which will start 10 normal nodes and start one special node that triggers an alive node to
     * broadcast an add operation. Thereafter a remove operation ios triggered which will remove the object again.
     * The operations should eventually be received by all alive nodes on order to validate the properties
     * of the algorithms.
     *
     **/
    public static SimulationScenario orsettest3() {
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
                        raise(1, startSpecialNode, new ConstantDistribution<>(Integer.class, 11), new ConstantDistribution<>(Integer.class, 4), new ConstantDistribution<>(Integer.class, 0));
                    }
                };
                StochasticProcess startSpecial2 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, startSpecialNode, new ConstantDistribution<>(Integer.class, 12), new ConstantDistribution<>(Integer.class, 6), new ConstantDistribution<>(Integer.class, 0));
                    }
                };
                StochasticProcess startSpecial3 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, startSpecialNode, new ConstantDistribution<>(Integer.class, 13), new ConstantDistribution<>(Integer.class, 4), new ConstantDistribution<>(Integer.class, 0));
                    }
                };
                StochasticProcess startSpecial4 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, startSpecialNode, new ConstantDistribution<>(Integer.class, 14), new ConstantDistribution<>(Integer.class, 4), new ConstantDistribution<>(Integer.class, 1));
                    }
                };

                systemSetup.start();
                /** Start 10 normal nodes */
                startBootstrapServer.startAfterTerminationOf(1000, systemSetup);
                startPeers.startAfterTerminationOf(1000, startBootstrapServer);
                /** Start special nodes which will store 2 duplicate values and one lone, of of the duplicates then gets removed */
                startSpecial.startAfterTerminationOf(10000, startPeers);            // add("value4")
                startSpecial2.startAfterTerminationOf(10000, startSpecial);         // add("value6")
                /** Start the next two simultaneously */
                startSpecial3.startAfterTerminationOf(10000, startSpecial2);         // add("value4")
                startSpecial4.startAfterTerminationOf(10000, startSpecial2);         // remove("value4")
                terminateAfterTerminationOf(1000*1000, startSpecial4);
            }
        };

        return scen;
    }
}
