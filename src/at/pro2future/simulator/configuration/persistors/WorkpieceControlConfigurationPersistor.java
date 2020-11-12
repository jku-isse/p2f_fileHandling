package at.pro2future.simulator.configuration.persistors;

import java.util.List;
import org.eclipse.emf.ecore.EObject;
import at.pro2future.simulator.configuration.WorkpieceControlConfiguration;

public class WorkpieceControlConfigurationPersistor extends ConfigurationPersistor {
	
	private final WorkpieceControlConfiguration sim;
	
	public WorkpieceControlConfiguration getConfiguration() {
		return sim;
	}
	
	public WorkpieceControlConfigurationPersistor() {
		this.sim = new WorkpieceControlConfiguration();
	}	 
	
	@Override
	protected String getFileName() {
		return "target/workpieceControl.xmi";
	}

	@Override
	protected List<EObject> getUncontainedObjects() {
		return this.sim.get();
	}
}
