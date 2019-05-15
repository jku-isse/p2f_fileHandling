package at.pro2future.shopfloors.interfaces.tests;


import at.pro2future.shopfloors.interfaces.impl.FileDataPersistor;
import at.pro2future.shopfloors.interfaces.impl.FileDataSource;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;


public class FileDataSourceTest {
	public static void main(String[] args) {
		FileDataSource pers = new FileDataSource("helloWorld.xmi");

		//register the generated model/code with the following line
		//pers.registerPackage(YourPackage.eNS_URI, YourPackage.eINSTANCE);
				
		EObject root;
		try {
			List<EObject> roots = pers.getShopfloorData();
			root = roots.get(0);
			//if o was of a type YourObject with a property name, it could be accessed like this
			/* 
			if(o instanceof YourObject) {
				YourObject yo = (YourObject)o;
				String s = yo.getName();
			}
			*/
			System.out.println(root);
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		System.out.println("======================================================");
		
		pers = new FileDataSource("failSafe");
		try {
			List<EObject> roots = pers.getShopfloorData();
			root = roots.get(0);
			System.out.println(root);
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
