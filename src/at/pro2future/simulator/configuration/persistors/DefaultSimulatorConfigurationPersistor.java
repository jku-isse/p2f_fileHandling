package at.pro2future.simulator.configuration.persistors;

import java.util.List;
import org.eclipse.emf.ecore.EObject;
import at.pro2future.simulator.configuration.DefaultSimulatorConfiguration;

public class DefaultSimulatorConfigurationPersistor extends ConfigurationPersistor {

	private final DefaultSimulatorConfiguration sim;
	
	public DefaultSimulatorConfiguration getConfiguration() {
		return this.sim;
	}
		
	public DefaultSimulatorConfigurationPersistor() {
		this.sim = new DefaultSimulatorConfiguration();
	}
	
	@Override
	protected String getFileName() {
		return "target/defaultSimulator.xmi";
	}

	@Override
	protected List<EObject> getUncontainedObjects() {
		return this.sim.get();
	}
}
