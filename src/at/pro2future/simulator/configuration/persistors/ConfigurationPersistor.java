package at.pro2future.simulator.configuration.persistors;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

import OpcUaDefinition.OpcUaDefinitionPackage;
import ProcessCore.ProcessCorePackage;
import Simulator.SimulatorPackage;
import at.pro2future.shopfloors.interfaces.impl.FileDataPersistor;

public abstract class ConfigurationPersistor implements Runnable{

	@Override
	public void run() {
		FileDataPersistor pers = new FileDataPersistor(getFileName());
		
		//register the generated model/code with the following line
		pers.registerPackage(ProcessCorePackage.eNS_URI, ProcessCorePackage.eINSTANCE);
		pers.registerPackage(OpcUaDefinitionPackage.eNS_URI, OpcUaDefinitionPackage.eINSTANCE);	
		pers.registerPackage(SimulatorPackage.eNS_URI, SimulatorPackage.eINSTANCE);		
		

		//if o was of a type with a property name, it could be set like this
		//o.setName("name");
		
		List<EObject> roots = getUncontainedObjects();
		
		try {
			pers.persistShopfloorData(roots);
			System.out.println("The configuration has been successfully persited in " + getFileName() + ".");
		} catch(Exception e) {
			e.printStackTrace();
			
			pers = new FileDataPersistor("failSafe");
			try {
				pers.persistShopfloorData(roots);
				System.out.println("The configuration has been successfully persited in \"failSafe\".");
			} catch(Exception e2) {
				e2.printStackTrace();
			}
		}		
		
	}
	
	protected abstract String getFileName();
	
	protected abstract List<EObject> getUncontainedObjects();
}
