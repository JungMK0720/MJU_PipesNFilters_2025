package components.homework;

import framework.LifeCycleManager;
import scenario.HWScenario;

public class HWSystemA {
	public static void main(String[] args) {
	    System.out.println("[HWSystemA] Running System A...");
	    LifeCycleManager mgr = new LifeCycleManager();
	    mgr.runBatch(HWScenario.A1, HWScenario.A2, HWScenario.A3);
	}

}