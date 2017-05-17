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
package se.kth.app.sim;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import se.kth.sim.compatibility.SimNodeIdExtractor;
import se.kth.system.HostMngrComp;
import se.sics.kompics.Kill;
import se.sics.kompics.KompicsEvent;
import se.sics.kompics.network.Address;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.adaptor.Operation;
import se.sics.kompics.simulator.adaptor.Operation1;
import se.sics.kompics.simulator.adaptor.Operation2;
import se.sics.kompics.simulator.adaptor.distributions.ConstantDistribution;
import se.sics.kompics.simulator.adaptor.distributions.extra.BasicIntSequentialDistribution;
import se.sics.kompics.simulator.events.system.KillNodeEvent;
import se.sics.kompics.simulator.events.system.SetupEvent;
import se.sics.kompics.simulator.events.system.StartNodeEvent;
import se.sics.ktoolbox.util.identifiable.Identifier;
import se.sics.kompics.simulator.network.identifier.IdentifierExtractor;
import se.sics.ktoolbox.omngr.bootstrap.BootstrapServerComp;
import se.sics.ktoolbox.util.network.KAddress;
import se.sics.ktoolbox.util.network.basic.BasicAddress;

/**
 * @author Alex Ormenisan <aaor@kth.se>
 */
public class ScenarioGen {

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
    static Operation2 startSpecialNode = new Operation2<StartNodeEvent, Integer, Integer>() {

        @Override
        public StartNodeEvent generate(final Integer self, final Integer target) {
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
                    return SimulationClient.class;
                }

                @Override
                public String toString() {
                    return "StartClient<" + selfAdr.toString() + ">";
                }

                @Override
                public SimulationClient.Init getComponentInit() {
                    return new SimulationClient.Init(selfAdr, "193.0.0." + target, target, "test" + target);
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
     * simpleBoot
     *
     * This simulation scenario starts 100 normal nodes which at the moment wont do anything
     *
     **/
    public static SimulationScenario simpleBoot() {
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
                        raise(100, startNodeOp, new BasicIntSequentialDistribution(1));
                    }
                };

                systemSetup.start();
                startBootstrapServer.startAfterTerminationOf(1000, systemSetup);
                startPeers.startAfterTerminationOf(1000, startBootstrapServer);
                terminateAfterTerminationOf(1000*1000, startPeers);
            }
        };

        return scen;
    }

    /**
     *
     * broadcastTest1
     *
     * SimulationScenario which will start 10 normal nodes and start one special node that triggers an alive node to
     * broadcast a message. This should eventually be received by all alive nodes on order to validate the properties
     * of the algorithm.
     *
     **/
    public static SimulationScenario broadcastTest1() {
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
                        raise(1, startSpecialNode, new ConstantDistribution<>(Integer.class, 11), new ConstantDistribution<>(Integer.class, 4));
                    }
                };

                systemSetup.start();
                /** Start 10 normal nodes */
                startBootstrapServer.startAfterTerminationOf(1000, systemSetup);
                startPeers.startAfterTerminationOf(1000, startBootstrapServer);
                /** Start special node which triggers one normal node to broadcast a message */
                startSpecial.startAfterTerminationOf(1000, startPeers);
                terminateAfterTerminationOf(1000*1000, startPeers);
            }
        };

        return scen;
    }

    public static SimulationScenario broadcastTest2() {
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
                        raise(1, startSpecialNode, new ConstantDistribution<>(Integer.class, 11), new ConstantDistribution<>(Integer.class, 4));
                    }
                };
                StochasticProcess startSpecial2 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, startSpecialNode, new ConstantDistribution<>(Integer.class, 12), new ConstantDistribution<>(Integer.class, 1));
                    }
                };
                StochasticProcess killPeer = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, killNodeOp, new ConstantDistribution<>(Integer.class, 9));
                    }
                };

                StochasticProcess killPeer2 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, killNodeOp, new ConstantDistribution<>(Integer.class, 6));
                    }
                };


                systemSetup.start();
                /** Start 10 normal nodes */
                startBootstrapServer.startAfterTerminationOf(1000, systemSetup);
                startPeers.startAfterTerminationOf(1000, startBootstrapServer);
                /** Start special node which triggers one normal node to broadcast a message */
                startSpecial.startAfterTerminationOf(100000, startPeers);
                /** Kill two nodes */
                killPeer.startAfterTerminationOf(10000, startSpecial);
                killPeer2.startAfterTerminationOf(10000, killPeer);
                /** Start another special nodes which triggers an alive node to broadcast a message */
                startSpecial2.startAfterTerminationOf(100000, killPeer2);
                terminateAfterTerminationOf(1000*1000, startSpecial2);
            }
        };

        return scen;
    }

    public static SimulationScenario broadcastTest3() {
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
                        raise(1, startSpecialNode, new ConstantDistribution<>(Integer.class, 11), new ConstantDistribution<>(Integer.class, 4));
                    }
                };
                StochasticProcess startSpecial2 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, startSpecialNode, new ConstantDistribution<>(Integer.class, 12), new ConstantDistribution<>(Integer.class, 1));
                    }
                };
                StochasticProcess killPeer = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, killNodeOp, new ConstantDistribution<>(Integer.class, 9));
                    }
                };

                StochasticProcess killPeer2 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, killNodeOp, new ConstantDistribution<>(Integer.class, 6));
                    }
                };
                StochasticProcess startDeadPeer = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1100));
                        raise(10, startNodeOp, new BasicIntSequentialDistribution(9));
                    }
                };
                StochasticProcess startSpecial3 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1000));
                        raise(1, startSpecialNode, new ConstantDistribution<>(Integer.class, 13), new ConstantDistribution<>(Integer.class, 9));
                    }
                };


                systemSetup.start();
                /** Start 10 normal nodes */
                startBootstrapServer.startAfterTerminationOf(1000, systemSetup);
                startPeers.startAfterTerminationOf(1000, startBootstrapServer);
                /** Start special node which triggers one normal node to broadcast a message */
                startSpecial.startAfterTerminationOf(100000, startPeers);
                /** Kill two nodes */
                killPeer.startAfterTerminationOf(10000, startSpecial);
                killPeer2.startAfterTerminationOf(10000, killPeer);
                /** Start another special nodes which triggers an alive node to broadcast a message */
                startSpecial2.startAfterTerminationOf(100000, killPeer2);
                /** Start a node which was previously dead */
                startDeadPeer.startAfterTerminationOf(10000, startSpecial2);
                /** Start special peer who targets the newly started node in order to test it's properties */
                startSpecial3.startAfterTerminationOf(100000, startDeadPeer);
                terminateAfterTerminationOf(1000*1000, startSpecial3);
            }
        };

        return scen;
    }
}
