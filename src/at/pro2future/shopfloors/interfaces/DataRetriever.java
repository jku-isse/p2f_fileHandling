package at.pro2future.shopfloors.interfaces;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

public interface DataRetriever {
	public List<EObject> getShopfloorData() throws IOException;
	public void registerPackage(String uri, EPackage pack);
	public List<File> getResource();
	public void unload();
}
