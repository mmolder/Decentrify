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

        /** Operation simulation */
        SimulationScenario operationTest1 = OperationSimulation.operationTest1();
        SimulationScenario operationTest2 = OperationSimulation.operationTest2();
        operationTest1.simulate(LauncherComp.class);
        //operationTest2.simulate(LauncherComp.class);
    }
}
