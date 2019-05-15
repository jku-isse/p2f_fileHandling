package at.pro2future.shopfloors.interfaces.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import at.pro2future.shopfloors.interfaces.DataPersistor;
import at.pro2future.shopfloors.interfaces.DataRetriever;

public class MultiFileDataStorage implements DataRetriever, DataPersistor {

	private List<URI> dataUris;
	private List<File> files;
	private Set<EObject> roots;
	private List<Resource> resources;
	ResourceSet set;

	public MultiFileDataStorage() {
		dataUris = new ArrayList<>();
		files = new ArrayList<>();
		roots = new HashSet<>();
		resources = new ArrayList<>();
		set = new ResourceSetImpl();

		set.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());

	}

	public MultiFileDataStorage(String[] fileNames) {
		this();
		for (String s : fileNames) {
			String completedName;
			if (s.contains(".xmi")) {
				completedName = s;
			} else {
				completedName = s + ".xmi";
			}

			files.add(new File(completedName));
			dataUris.add(URI.createFileURI(completedName));

		}
	}

	public MultiFileDataStorage(File[] files) {
		this();
		for (File f : files) {
			String completedName;
			if (f.getPath().contains(".xmi")) {
				completedName = f.getPath();
			} else {
				completedName = f.getPath() + ".xmi";
			}

			this.files.add(new File(completedName));
			dataUris.add(URI.createFileURI(completedName));

		}
	}

	@Override
	public void persistShopfloorData(List<EObject> s) throws IOException {
		for (Resource r : set.getResources()) {
			r.save(Collections.EMPTY_MAP);
		}

	}

	@Override
	public List<EObject> getShopfloorData() throws IOException {

		for (URI u : dataUris) {
			resources.add(set.getResource(u, true));
		}
		EcoreUtil.resolveAll(set);

		for (Resource r : set.getResources()) {
			roots.addAll((Collection<? extends EObject>) r.getContents());
		}
		return new ArrayList<>(roots);
	}

	@Override
	public void registerPackage(String uri, EPackage pack) {
		set.getPackageRegistry().put(uri, pack);

	}

	@Override
	public List<File> getResource() {

		return files;
	}

	@Override
	public void unload() {
		for (Resource r : resources) {
			if (r != null) {
				r.unload();
			}
		}

	}

	public void addResource(String name) {
        files.add(new File(name));
        dataUris.add(URI.createFileURI(name));
    }
	
}
