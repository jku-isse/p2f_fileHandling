package at.pro2future.simulator.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.Diagnostician;
import ProcessCore.Constant;
import ProcessCore.Event;
import ProcessCore.LocalVariable;
import ProcessCore.ProcessCoreFactory;
import Simulator.MachineSimulator;
import Simulator.MsCallMethodAction;
import Simulator.MsClientInterface;
import OpcUaDefinition.MsMethodNode;
import OpcUaDefinition.MsObjectNode;
import OpcUaDefinition.MsObjectTypeNode;
import OpcUaDefinition.MsPropertyNode;
import Simulator.MsServerInterface;
import OpcUaDefinition.MsVariableNode;
import Simulator.SimulatorFactory;

public class DefaultSimulatorConfiguration implements Supplier<List<EObject>> {

	MachineSimulator sim;

	private final List<EObject> uncontainedObjects = new ArrayList<>();
	
	@Override
	public List<EObject> get() {
		return uncontainedObjects;
	}


	public DefaultSimulatorConfiguration() {
		// setup simulator
		this.sim = SimulatorFactory.eINSTANCE.createMachineSimulator();
		this.sim.setName("DefaultSimulator");
		this.sim.setInstanceInformation(CommonObjects.DefaultSimulatorInstanceInformation);
		
		// setup instance information
		MsClientInterface opcUaClientInterface = SimulatorFactory.eINSTANCE.createMsClientInterface();
		opcUaClientInterface.setTargetInstanceInformation(CommonObjects.DefaultSimulatorInstanceInformation); 
	
		// setup process
		LocalVariable tool = ConfigurationUtil.initializeLocalVariable("Tool", "String", "none");
		Constant none = ConfigurationUtil.initializeConstance("None", "String", "none");
		Constant welding = ConfigurationUtil.initializeConstance("Welding", "String", "welding");
		LocalVariable speed = ConfigurationUtil.initializeLocalVariable("Speed", "Integer", 0);
		Constant zero = ConfigurationUtil.initializeConstance("zero", "Integer", 0);
		Constant five = ConfigurationUtil.initializeConstance("five", "Integer", 5);

		ProcessCore.Process mainProcess = ConfigurationUtil.initializeProcess("MainProcess");
		mainProcess.getSteps().add(ConfigurationUtil.initializeSetVariableStep("Sets a welding tool", tool, welding));
		mainProcess.getSteps().add(ConfigurationUtil.initializeSetVariableStep("Start welding", speed, five));
		mainProcess.getSteps().add(ConfigurationUtil.initializeSetVariableStep("Stop welding", speed, zero));
		mainProcess.getSteps().add(ConfigurationUtil.initializeSetVariableStep("To start position", tool, none));

		this.sim.setStateMachine(mainProcess);


		// setup opcua interface
		MsObjectTypeNode objectType = ConfigurationUtil.initializeMsObjectTypeNode("RootType", "RootType", "RootType");
		MsPropertyNode opcUaSpeed = ConfigurationUtil.initializeMsPropertyNode("Speed", "Speed", "Speed",  "Speed", ConfigurationUtil.createMsNodeId(true), 0, CommonObjects.IntegerDataType, CommonObjects.ModellingRuleMandatory);
		MsPropertyNode opcUaTool = ConfigurationUtil.initializeMsPropertyNode("Tool", "Tool", "Tool", "Tool", ConfigurationUtil.createMsNodeId(true),  "none", CommonObjects.StringDataType, CommonObjects.ModellingRuleMandatory);
		MsMethodNode methodNode = ConfigurationUtil.initializeMsMethodNode("NullMethod", "NullMethod", "NullMethod",
				ConfigurationUtil.createMsNodeId(true, "Method"),  true, true, new ArrayList<MsVariableNode>(),  new ArrayList<MsVariableNode>(),
			"public Variant[] testMethod(Variant[] test){"
			+ "     return new Variant[0]; "
			+ "}");
		objectType.getHasComponent().add(opcUaSpeed);
		objectType.getHasComponent().add(opcUaTool);
		objectType.getHasComponent().add(methodNode);
		
				
		MsObjectNode object = ConfigurationUtil.initializeMsObjectNode("Root", "Root", "Root");
		object.setHasTypeDefinition(objectType);
		MsVariableNode opcUaSpeedVariable = ConfigurationUtil.copyWithNewNodeId(opcUaSpeed, true);
		MsVariableNode opcUaToolVariable = ConfigurationUtil.copyWithNewNodeId(opcUaTool, true);
		MsMethodNode methodNodeVariable = ConfigurationUtil.copyWithNewNodeId(methodNode, true);
		object.getHasComponent().add(opcUaSpeedVariable);
		object.getHasComponent().add(opcUaToolVariable);
		object.getHasComponent().add(methodNodeVariable);
		
		
		MsObjectNode baseFolder = ConfigurationUtil.initializeMsObjectNode("BaseFolder", "BaseFolder", "BaseFolder");
		baseFolder.setHasTypeDefinition(CommonObjects.FolderType);
		baseFolder.getOrganizes().add(object);
		
		MsServerInterface opcUaServerInterface = SimulatorFactory.eINSTANCE.createMsServerInterface();
		opcUaServerInterface.getNodes().add(baseFolder);

		// MsMethodNode		
		
		this.sim.setOpcUaServerInterface(opcUaServerInterface);
		this.sim.eClass();
		// this.sim.eSet(SimulatorPackage.eINSTANCE.getMachineSimulator_Opcuaserverinterface(),
		// opcUaServerInterface);

		// setup actions
		Event speedChangedEvent = ProcessCoreFactory.eINSTANCE.createEvent();
		speedChangedEvent.setName("Speed Changed Event");
		speedChangedEvent.setRole(CommonObjects.DefaultAssignment);
		speedChangedEvent.getParameters().add(speed);
		this.sim.getActions().add(ConfigurationUtil.initializeAction(opcUaClientInterface, speedChangedEvent, Arrays.asList(opcUaSpeedVariable), SimulatorFactory.eINSTANCE.createMsWriteAction()));
		
		Event toolChangedEvent = ProcessCoreFactory.eINSTANCE.createEvent();
		toolChangedEvent.setName("Tool Changed Event");
		toolChangedEvent.setRole(CommonObjects.DefaultAssignment);
		toolChangedEvent.getParameters().add(tool);
		this.sim.getActions().add(ConfigurationUtil.initializeAction(opcUaClientInterface, toolChangedEvent, Arrays.asList(opcUaToolVariable), SimulatorFactory.eINSTANCE.createMsReadAction()));
		
		Event callMethodEvent = ProcessCoreFactory.eINSTANCE.createEvent();
		callMethodEvent.setName("Call Method Event");
		callMethodEvent.setRole(CommonObjects.DefaultAssignment);
		MsCallMethodAction callMethodAction = ConfigurationUtil.initializeAction(opcUaClientInterface, callMethodEvent, Arrays.asList(), SimulatorFactory.eINSTANCE.createMsCallMethodAction());
		callMethodAction.setCallesMethod(methodNode);
		callMethodAction.setObjectContainingMethod(object);
		this.sim.getActions().add(callMethodAction);
		
		
		// validate sim after setup to find out errors
		Diagnostic validate = Diagnostician.INSTANCE.validate(this.sim);
		if (Diagnostic.ERROR == validate.getSeverity()) {
			throw new RuntimeException(validate.toString());
		}

		// add all objects which do not have a container (are not target of an contained
		// reference)
		uncontainedObjects.add(this.sim);
		uncontainedObjects.addAll(CommonObjects.getAllDefaultObects());
		uncontainedObjects.add(opcUaClientInterface);
		uncontainedObjects.add(objectType);
		uncontainedObjects.add(five);
		uncontainedObjects.add(tool);
		uncontainedObjects.add(none);
		uncontainedObjects.add(welding);
		uncontainedObjects.add(speed);
		uncontainedObjects.add(zero);
		uncontainedObjects.add(five);
		uncontainedObjects.add(speedChangedEvent);
		uncontainedObjects.add(toolChangedEvent);
		uncontainedObjects.add(callMethodEvent);
	}

	
}
