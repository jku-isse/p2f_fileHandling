package at.pro2future.simulator.configuration;

import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.util.TypeUtil;

import OpcUaDefinition.MsNodeId;
import OpcUaDefinition.MsObjectTypeNode;
import ProcessCore.Assignment;
import ProcessCore.LocalVariable;
import ProcessCore.ProcessCoreFactory;
import Simulator.MsInstanceInformation;
import Simulator.SimulatorFactory;

public class CommonObjects {
	
	protected static final MsObjectTypeNode FolderType = ConfigurationUtil.initializeMsObjectTypeNode("FolderType", "FolderType", "FolderType");;
	protected static final MsNodeId ModellingRuleMandatory = ConfigurationUtil.createMsNodeId(false,Identifiers.ModellingRule_Mandatory);
	protected static final MsNodeId StringDataType = ConfigurationUtil.createMsNodeId(false, TypeUtil.getBuiltinTypeId(String.class));
	protected static final MsNodeId IntegerDataType = ConfigurationUtil.createMsNodeId(false, TypeUtil.getBuiltinTypeId(Integer.class));
	protected static final MsNodeId BooleanDataType = ConfigurationUtil.createMsNodeId(false, TypeUtil.getBuiltinTypeId(Boolean.class));
	
	protected static final MsInstanceInformation DefaultSimulatorInstanceInformation = SimulatorFactory.eINSTANCE.createMsInstanceInformation();
	protected static final MsInstanceInformation MillingControlInstanceInformation = SimulatorFactory.eINSTANCE.createMsInstanceInformation();
	protected static final MsInstanceInformation ToolControlInstanceInformation = SimulatorFactory.eINSTANCE.createMsInstanceInformation();
	protected static final MsInstanceInformation WorkpieceControlInstanceInformation = SimulatorFactory.eINSTANCE.createMsInstanceInformation();
	
	protected static final Assignment DefaultAssignment = ProcessCoreFactory.eINSTANCE.createAssignment();
	
	protected static final LocalVariable True = ConfigurationUtil.initializeLocalVariable("true", "Boolean", true);
	protected static final LocalVariable False = ConfigurationUtil.initializeLocalVariable("false", "Boolean", false);
	
	protected static final LocalVariable NullString = ConfigurationUtil.initializeLocalVariable("nullString", "String", null);

	
	static {
		FolderType.setNodeId(ConfigurationUtil.createMsNodeId(false, Identifiers.FolderType));
		
		DefaultSimulatorInstanceInformation.setDisplayName("Default Simulator");
		DefaultSimulatorInstanceInformation.setHost("localhost");
		DefaultSimulatorInstanceInformation.setPort(2000);

		
		MillingControlInstanceInformation.setDisplayName("MillingControl");
		MillingControlInstanceInformation.setHost("localhost");
		MillingControlInstanceInformation.setPort(3000);
		
		ToolControlInstanceInformation.setDisplayName("ToolControl");
		ToolControlInstanceInformation.setHost("localhost");
		ToolControlInstanceInformation.setPort(3020);
		
		WorkpieceControlInstanceInformation.setDisplayName("WorkpieceControl");
		WorkpieceControlInstanceInformation.setHost("localhost");
		WorkpieceControlInstanceInformation.setPort(3040);
		
		DefaultAssignment.setName("DefaultAssignment");
	}
	
	public static List<EObject> getAllDefaultObects() {
		return Arrays.asList(
				FolderType,
				ModellingRuleMandatory,
				StringDataType,
				IntegerDataType,
				BooleanDataType,
				DefaultAssignment,
				True,
				False,
				NullString
		);		
	}
	

}
