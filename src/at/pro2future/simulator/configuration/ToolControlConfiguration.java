package at.pro2future.simulator.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.Diagnostician;
import ProcessCore.AbstractLoop;
import ProcessCore.Condition;
import ProcessCore.Decision;
import ProcessCore.Event;
import ProcessCore.LocalVariable;
import ProcessCore.Operator;
import Simulator.MachineSimulator;
import Simulator.MsClientInterface;
import OpcUaDefinition.MsObjectNode;
import OpcUaDefinition.MsObjectTypeNode;
import OpcUaDefinition.MsPropertyNode;
import Simulator.MsServerInterface;
import OpcUaDefinition.MsVariableNode;
import Simulator.SimulatorFactory;

public class ToolControlConfiguration implements Supplier<List<EObject>> {


    MachineSimulator sim;
	
	private final List<EObject> uncontainedObjects = new ArrayList<>();

	public MachineSimulator getSim() {
		return sim;
	}

	public List<EObject> getUncontainedObjects() {
		return uncontainedObjects;
	}
	
	@Override
	public List<EObject> get() {
		return uncontainedObjects;
	}

	public ToolControlConfiguration() {
		// setup simulator
		this.sim = SimulatorFactory.eINSTANCE.createMachineSimulator();
		this.sim.setName("ToolControl");
		this.sim.setInstanceInformation(CommonObjects.ToolControlInstanceInformation);
		
		// setup instance information
		MsClientInterface opcUaClientInterface = SimulatorFactory.eINSTANCE.createMsClientInterface();
		opcUaClientInterface.setTargetInstanceInformation(CommonObjects.ToolControlInstanceInformation); 	

		// setup process
		LocalVariable toolInt = ConfigurationUtil.initializeLocalVariable("toolInt", "String", 0);
		LocalVariable startInt = ConfigurationUtil.initializeLocalVariable("startInt", "Boolean", false);
		LocalVariable isMillingInt = ConfigurationUtil.initializeLocalVariable("isMillingInt", "Boolean", false);
		Event toolChangedEvent = ConfigurationUtil.initializeEvent("ToolChangedEvent", CommonObjects.DefaultAssignment, toolInt);
		Event startEvent = ConfigurationUtil.initializeEvent("StartEvent", CommonObjects.DefaultAssignment, startInt);
		Event isMillingEvent = ConfigurationUtil.initializeEvent("IsMillingEvent", CommonObjects.DefaultAssignment, isMillingInt);
		ProcessCore.Process mainProcess = setupMainProcess(toolInt, startInt, isMillingEvent, isMillingInt);
		this.sim.setStateMachine(mainProcess);
		

		// setup opcua interface
		MsObjectTypeNode objectType = ConfigurationUtil.initializeMsObjectTypeNode("RootType", "RootType", "RootType");
		MsPropertyNode tool = ConfigurationUtil.initializeMsPropertyNode("Tool", "Tool", "Tool",  "Tool", ConfigurationUtil.createMsNodeId(true), 0, CommonObjects.StringDataType, CommonObjects.ModellingRuleMandatory);
		MsPropertyNode start = ConfigurationUtil.initializeMsPropertyNode("Start", "Start", "Start",  "Start", ConfigurationUtil.createMsNodeId(true), 0, CommonObjects.BooleanDataType, CommonObjects.ModellingRuleMandatory);
		MsPropertyNode isMilling = ConfigurationUtil.initializeMsPropertyNode("IsMilling", "IsMilling", "IsMilling",  "IsMilling", ConfigurationUtil.createMsNodeId(true), 0, CommonObjects.BooleanDataType, CommonObjects.ModellingRuleMandatory);
		objectType.getHasComponent().addAll(Arrays.asList(tool, start, isMilling));
		
		MsObjectNode object = ConfigurationUtil.initializeMsObjectNode("Root", "Root", "Root");
		object.setHasTypeDefinition(objectType);
		MsVariableNode opcUaTool = ConfigurationUtil.copyWithNewNodeId(tool, true);
		MsVariableNode opcUaStart = ConfigurationUtil.copyWithNewNodeId(start, true);
		MsVariableNode opcUaIsMilling = ConfigurationUtil.copyWithNewNodeId(isMilling, true);
		object.getHasComponent().addAll(Arrays.asList(opcUaTool, opcUaStart, opcUaIsMilling));
		
		
		MsObjectNode baseFolder = ConfigurationUtil.initializeMsObjectNode("BaseFolder", "BaseFolder", "BaseFolder");
		baseFolder.setHasTypeDefinition(CommonObjects.FolderType);
		baseFolder.getOrganizes().add(object);
		
		MsServerInterface opcUaServerInterface = SimulatorFactory.eINSTANCE.createMsServerInterface();
		opcUaServerInterface.getNodes().add(baseFolder);
		this.sim.setOpcUaServerInterface(opcUaServerInterface);
		
		this.sim.getActions().add(ConfigurationUtil.initializeAction(opcUaClientInterface, toolChangedEvent, Arrays.asList(opcUaTool), SimulatorFactory.eINSTANCE.createMsReadAction()));
		this.sim.getActions().add(ConfigurationUtil.initializeAction(opcUaClientInterface, startEvent, Arrays.asList(opcUaStart), SimulatorFactory.eINSTANCE.createMsReadAction()));
		this.sim.getActions().add(ConfigurationUtil.initializeAction(opcUaClientInterface, isMillingEvent, Arrays.asList(opcUaIsMilling), SimulatorFactory.eINSTANCE.createMsWriteAction()));
						
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
		uncontainedObjects.add(toolChangedEvent);
		uncontainedObjects.add(startEvent);
		uncontainedObjects.add(isMillingEvent);
		uncontainedObjects.add(objectType);
	}

	private ProcessCore.Process setupMainProcess(LocalVariable toolInt, LocalVariable startInt, Event isMillingEvent, LocalVariable isMillingInt) {	
		ProcessCore.Process milling = ConfigurationUtil.initializeProcess("Milling");
		//TODO: check if sending event works
		milling.getSteps().add(ConfigurationUtil.initializeEventSource("isMillingInt Event Source", isMillingEvent, Arrays.asList(CommonObjects.True)));
		//TODO: add delay
		
		Condition startIntIsTrue  = ConfigurationUtil.initializeSimpleCondition(startInt, Operator.EQ, CommonObjects.True);
		Decision checkStarted = ConfigurationUtil.initializeDecision("Start", milling, ConfigurationUtil.initializeProcess("EmptyProcess"), startIntIsTrue);
		ProcessCore.Process checkStartedProcess = ConfigurationUtil.initializeProcess("CheckStartedProcess");
		checkStartedProcess.getSteps().add(checkStarted); 
		
		Condition toolIntNull  = ConfigurationUtil.initializeSimpleCondition(toolInt, Operator.EQ, CommonObjects.NullString);
		AbstractLoop whileToolIntNullDoNoting = ConfigurationUtil.initializeHeadLoop("while tool null", ConfigurationUtil.initializeProcess("EmptyProcess"), toolIntNull);
		Condition toolIntNotNull  = ConfigurationUtil.initializeSimpleCondition(toolInt, Operator.NEQ, CommonObjects.NullString);
		AbstractLoop whileToolIntNotNullCheckStarted = ConfigurationUtil.initializeHeadLoop("while tool not null check started", checkStartedProcess, toolIntNotNull);
	
		ProcessCore.Process subProcess = ConfigurationUtil.initializeProcess("SubProcess");
		subProcess.getSteps().add(whileToolIntNullDoNoting); // wait until there is a tool
		subProcess.getSteps().add(whileToolIntNotNullCheckStarted); // check condition as long as there is a tool.
		
		ProcessCore.Process mainProcess = ConfigurationUtil.initializeProcess("MainProcess");
		Condition alwaysTrue  = ConfigurationUtil.initializeSimpleCondition(CommonObjects.True, Operator.EQ, CommonObjects.True);
		mainProcess.getSteps().add(ConfigurationUtil.initializeHeadLoop("Root loop", subProcess, alwaysTrue)); // execute the subprocess forever.
		return mainProcess;
	}
}