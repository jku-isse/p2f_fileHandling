package at.pro2future.simulator.configuration.persistors;

import java.util.List;
import org.eclipse.emf.ecore.EObject;
import at.pro2future.simulator.configuration.MillingControlConfiguration;
import at.pro2future.simulator.configuration.ToolControlConfiguration;
import at.pro2future.simulator.configuration.WorkpieceControlConfiguration;

public class MillingControlConfigurationPersistor extends ConfigurationPersistor{
	
	private final MillingControlConfiguration sim;
	
	public MillingControlConfiguration getConfiguration() {
		return this.sim;
	}
		
	public MillingControlConfigurationPersistor(ToolControlConfiguration tcc, WorkpieceControlConfiguration mcc) {
		this.sim = new MillingControlConfiguration(tcc, mcc);
	}
		
	@Override
	protected String getFileName() {
		return "target/millingControl.xmi";
	}

	@Override
	protected List<EObject> getUncontainedObjects() {
		return this.sim.get();
	}
}
