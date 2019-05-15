package at.pro2future.shopfloors.interfaces.tests;


import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;

import at.pro2future.shopfloors.interfaces.impl.FileDataPersistor;

public class FileDataPersistorTest {
	public static void main(String[] args) {
		FileDataPersistor pers = new FileDataPersistor("helloWorld.xmi");
		
		//register the generated model/code with the following line
		//pers.registerPackage(YourPackage.eNS_URI, YourPackage.eINSTANCE);
		
		
		EObject o = EcoreFactory.eINSTANCE.createEObject();
		
		//if o was of a type with a property name, it could be set like this
		//o.setName("name");
		
		List<EObject> roots = new LinkedList<>();
		roots.add(o);
		try {
			pers.persistShopfloorData(roots);
		} catch(Exception e) {
			e.printStackTrace();
		}
		pers = new FileDataPersistor("failSafe");
		try {
			pers.persistShopfloorData(roots);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
