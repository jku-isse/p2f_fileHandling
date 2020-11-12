package at.pro2future.shopfloors.interfaces.impl;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import at.pro2future.shopfloors.interfaces.DataRetriever;

public class FileDataSource implements DataRetriever{
	String folderPath;
	
	private URI dataUri;
	private EObject dataRoot;
	private Resource r;
	private ResourceSet set;
	private File f;
	
	public FileDataSource(String fileName) {

		set = new ResourceSetImpl();

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
	
	public List<EObject> getShopfloorData() throws IOException {

		set.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		
		r = set.getResource(dataUri, true);
		LinkedList<EObject> dataRoot = new LinkedList<>();
		for(EObject eo : r.getContents()) {
			if(eo instanceof EObject) {
				dataRoot.add((EObject)eo);
			}
		}
	
		return dataRoot;
	}

	@Override
	public void registerPackage(String uri, EPackage pack) {
		if(!set.getPackageRegistry().containsKey(uri))
			set.getPackageRegistry().put(uri, pack);
		
	}

	public void unload() {
		if(r!=null) {
			r.unload();
		}
	}



	@Override
	public List<File> getResource() {
		List<File> files = new LinkedList<>();
		files.add(f);
		return files;
	}
}
