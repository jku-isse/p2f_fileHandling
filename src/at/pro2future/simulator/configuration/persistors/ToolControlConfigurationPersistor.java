package at.pro2future.simulator.configuration.persistors;

import java.util.List;
import org.eclipse.emf.ecore.EObject;
import at.pro2future.simulator.configuration.ToolControlConfiguration;

public class ToolControlConfigurationPersistor extends ConfigurationPersistor {

	private final ToolControlConfiguration sim;
	
	public ToolControlConfiguration getConfiguration() {
		return sim;
	}
		
	public ToolControlConfigurationPersistor() {
		this.sim = new ToolControlConfiguration();
	}
	
	@Override
	protected String getFileName() {
		return "target/toolControl.xmi";
	}

	@Override
	protected List<EObject> getUncontainedObjects() {
		return this.sim.get();
	}	
}
