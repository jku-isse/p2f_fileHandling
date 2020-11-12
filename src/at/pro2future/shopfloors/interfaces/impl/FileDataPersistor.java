package at.pro2future.shopfloors.interfaces.impl;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import at.pro2future.shopfloors.interfaces.DataPersistor;

public class FileDataPersistor implements DataPersistor{
	String folderPath;
	Resource target;
	ResourceSet set;
	File f;
	
	private URI dataUri;
	/**
	 * 
	 * @param Filename should end in ".xmi"
	 */
	public FileDataPersistor(String fileName) {
		set = new ResourceSetImpl();
		set.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());

		String completedName;
		if(fileName.contains(".xmi")) {
			completedName = fileName;
		} else {
			completedName = fileName + ".xmi";
		}
		f = new File(completedName);
		folderPath = f.getParent()+"\\";
		
		dataUri = URI.createFileURI(completedName);
		
		
	}
	

	public void persistShopfloorData(List<EObject> roots) throws IOException {

		target = set.createResource(dataUri);
		/*

		 * Iterator<AbstractCapability> content = s.getCapabilities().iterator();
		 * while(content.hasNext()) { EObject eo = content.next();
		 * target.getContents().add(eo); } Iterator<Process> pcontent =
		 * s.getProcesses().iterator(); while(pcontent.hasNext()) { EObject eo =
		 * pcontent.next(); target.getContents().add(eo); }
		 */
		

		Iterator<EObject> econtent = roots.iterator();
		while(econtent.hasNext()) {
			EObject eo = econtent.next();
			target.getContents().add(eo);
			
		}
		
		target.save(Collections.EMPTY_MAP);
	}


	@Override
	public void registerPackage(String uri, EPackage pack) {
		if(!set.getPackageRegistry().containsKey(uri))
			set.getPackageRegistry().put(uri, pack);
		
	}
	public void unload() {
		if(target != null) {
			target.unload();
		}
			
	}


	@Override
	public List<File> getResource() {
		List<File> files = new LinkedList<>();
		files.add(f);
		return files;
	}
}