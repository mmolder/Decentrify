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

import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.run.LauncherComp;

/**
 * @author Alex Ormenisan <aaor@kth.se>
 */
public class SimLauncher {
    public static void main(String[] args) {

        /** Broadcast simulation */
        SimulationScenario.setSeed(ScenarioSetup.scenarioSeed);
        SimulationScenario broadcastTest1 = ScenarioGen.broadcastTest1();
        SimulationScenario broadcastTest2 = ScenarioGen.broadcastTest2();
        SimulationScenario broadcastTest3 = ScenarioGen.broadcastTest3();
        //broadcastTest1.simulate(LauncherComp.class);
        //broadcastTest2.simulate(LauncherComp.class);
        //broadcastTest3.simulate(LauncherComp.class);

        /** GSet simulation */
        SimulationScenario gSetTest1 = GSetSimulation.gSetTest1();
        SimulationScenario gSetTest2 = GSetSimulation.gSetTest2();
        //gSetTest1.simulate(LauncherComp.class);
        //gSetTest2.simulate(LauncherComp.class);

        /** 2PSet simulation */
        SimulationScenario twoPTest1 = TwoPSetSimulation.twoPTest1();
        SimulationScenario twoPTest2 = TwoPSetSimulation.twoPTest2();
        //twoPTest1.simulate(LauncherComp.class);
        //twoPTest2.simulate(LauncherComp.class);

        /** ORSet simulation */
        SimulationScenario orSetTest1 = ORSetSimulation.orsettest1();
        SimulationScenario orSetTest2 = ORSetSimulation.orsettest2();
        SimulationScenario orSetTest3 = ORSetSimulation.orsettest3();
        //orSetTest1.simulate(LauncherComp.class);
        //orSetTest2.simulate(LauncherComp.class);
        //orSetTest3.simulate(LauncherComp.class);

        /** 2P2P Graph simulation */
        SimulationScenario twoPGraphTest1 = TwoPGraphSimulation.twopgraphtest1();
        SimulationScenario twoPGraphTest2 = TwoPGraphSimulation.twopgraphtest2();
        SimulationScenario twoPGraphTest3 = TwoPGraphSimulation.twopgraphtest3();
        SimulationScenario twoPGraphTest4 = TwoPGraphSimulation.twopgraphtest4();
        //twoPGraphTest1.simulate(LauncherComp.class);
        //twoPGraphTest2.simulate(LauncherComp.class);
        //twoPGraphTest3.simulate(LauncherComp.class);
        //twoPGraphTest4.simulate(LauncherComp.class);
    }
}
