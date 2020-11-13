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
import ProcessCore.Event;
import ProcessCore.LocalVariable;
import ProcessCore.Operator;
import ProcessCore.Process;
import Simulator.MachineSimulator;
import Simulator.MsClientInterface;
import OpcUaDefinition.MsObjectNode;
import OpcUaDefinition.MsObjectTypeNode;
import OpcUaDefinition.MsPropertyNode;
import Simulator.MsServerInterface;
import OpcUaDefinition.MsVariableNode;
import Simulator.SimulatorFactory;

public class MillingControlConfiguration implements Supplier<List<EObject>> {
	 	
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

	public MillingControlConfiguration(ToolControlConfiguration tcc, WorkpieceControlConfiguration wcc) {
		// setup simulator
		this.sim = SimulatorFactory.eINSTANCE.createMachineSimulator();
		this.sim.setName("MillingControl");
		this.sim.setInstanceInformation(CommonObjects.MillingControlInstanceInformation);
		
		// setup instance information
		MsClientInterface millingControlInterface = SimulatorFactory.eINSTANCE.createMsClientInterface();
		millingControlInterface.setTargetInstanceInformation(CommonObjects.MillingControlInstanceInformation); 
		MsClientInterface toolControlInterface = SimulatorFactory.eINSTANCE.createMsClientInterface();
		toolControlInterface.setTargetInstanceInformation(CommonObjects.ToolControlInstanceInformation);
		MsClientInterface workpieceControlInterface = SimulatorFactory.eINSTANCE.createMsClientInterface();
		workpieceControlInterface.setTargetInstanceInformation(CommonObjects.WorkpieceControlInstanceInformation);

		
		// setup process
		LocalVariable beginInt = ConfigurationUtil.initializeLocalVariable("beginInt", "Boolean", false);
		LocalVariable workpieceInt = ConfigurationUtil.initializeLocalVariable("workpieceInt", "String", "");
		LocalVariable toolInt = ConfigurationUtil.initializeLocalVariable("toolInt", "String", "");
		LocalVariable startInt = ConfigurationUtil.initializeLocalVariable("startInt", "Boolean", false);
		
		LocalVariable defaultWorkpiece = ConfigurationUtil.initializeLocalVariable("defaultToolInt", "String", "DefaultWorkpiece");
		LocalVariable defaultTool = ConfigurationUtil.initializeLocalVariable("defaultWorkpieceInt", "String", "DefaultTool");

		Event beginEvent = ConfigurationUtil.initializeEvent("BeginEvent", CommonObjects.DefaultAssignment, beginInt);
		Event workpieceEvent = ConfigurationUtil.initializeEvent("WorkpieceEvent", CommonObjects.DefaultAssignment, defaultWorkpiece);
		Event toolEvent = ConfigurationUtil.initializeEvent("ToolEvent", CommonObjects.DefaultAssignment, defaultTool);
		Event startEvent = ConfigurationUtil.initializeEvent("StartEvent", CommonObjects.DefaultAssignment, startInt);
		ProcessCore.Process mainProcess = setupMainProcess(beginInt, workpieceEvent, workpieceInt, toolEvent, toolInt, startEvent, startInt, defaultWorkpiece, defaultTool);
		this.sim.setStateMachine(mainProcess);
		

		// setup opcua interface
		MsObjectTypeNode objectType = ConfigurationUtil.initializeMsObjectTypeNode("RootType", "RootType", "RootType");
		MsPropertyNode begin = ConfigurationUtil.initializeMsPropertyNode("Begin", "Begin", "Begin",  "Begin", ConfigurationUtil.createMsNodeId(true), 0, CommonObjects.BooleanDataType, CommonObjects.ModellingRuleMandatory);
		objectType.getHasComponent().addAll(Arrays.asList(begin));
		
		MsObjectNode object = ConfigurationUtil.initializeMsObjectNode("Root", "Root", "Root");
		object.setHasTypeDefinition(objectType);
		MsVariableNode opcUaBegin = ConfigurationUtil.copyWithNewNodeId(begin, true);
		object.getHasComponent().addAll(Arrays.asList(opcUaBegin));

		MsObjectNode baseFolder = ConfigurationUtil.initializeMsObjectNode("BaseFolder", "BaseFolder", "BaseFolder");
		baseFolder.setHasTypeDefinition(CommonObjects.FolderType);
		baseFolder.getOrganizes().add(object);
		
		MsServerInterface opcUaServerInterface = SimulatorFactory.eINSTANCE.createMsServerInterface();
		opcUaServerInterface.getNodes().add(baseFolder);
		this.sim.setOpcUaServerInterface(opcUaServerInterface);
		
		
		//get remote controled variables
		MsVariableNode opcUaWorkpiece = (MsVariableNode) ((MsObjectNode)((MsObjectNode)wcc.sim.getOpcUaServerInterface().getNodes().get(0)).getOrganizes().get(0)).getHasComponent().get(0);
		MsVariableNode opcUaTool = (MsVariableNode) ((MsObjectNode)((MsObjectNode)tcc.sim.getOpcUaServerInterface().getNodes().get(0)).getOrganizes().get(0)).getHasComponent().get(0);
		MsVariableNode opcUaStart = (MsVariableNode) ((MsObjectNode)((MsObjectNode)tcc.sim.getOpcUaServerInterface().getNodes().get(0)).getOrganizes().get(0)).getHasComponent().get(1);
		
		this.sim.getActions().add(ConfigurationUtil.initializeAction(millingControlInterface, beginEvent, Arrays.asList(opcUaBegin), SimulatorFactory.eINSTANCE.createMsReadAction()));
		this.sim.getActions().add(ConfigurationUtil.initializeAction(workpieceControlInterface, workpieceEvent, Arrays.asList(opcUaWorkpiece), SimulatorFactory.eINSTANCE.createMsWriteAction()));
		this.sim.getActions().add(ConfigurationUtil.initializeAction(toolControlInterface, toolEvent, Arrays.asList(opcUaTool), SimulatorFactory.eINSTANCE.createMsWriteAction()));
		this.sim.getActions().add(ConfigurationUtil.initializeAction(toolControlInterface, startEvent, Arrays.asList(opcUaStart), SimulatorFactory.eINSTANCE.createMsWriteAction()));
		
		// validate sim after setup to find out errors
		Diagnostic validate = Diagnostician.INSTANCE.validate(this.sim);
		if (Diagnostic.ERROR == validate.getSeverity()) {
			throw new RuntimeException(validate.toString());
		}

		// add all objects which do not have a container (are not target of an contained
		// reference)
		uncontainedObjects.add(this.sim);
		uncontainedObjects.addAll(CommonObjects.getAllDefaultObects());
		uncontainedObjects.add(millingControlInterface);
		uncontainedObjects.add(CommonObjects.WorkpieceControlInstanceInformation);
		uncontainedObjects.add(workpieceControlInterface);
		uncontainedObjects.add(CommonObjects.ToolControlInstanceInformation);
		uncontainedObjects.add(toolControlInterface);
		uncontainedObjects.add(beginEvent);
		uncontainedObjects.add(workpieceEvent);
		uncontainedObjects.add(toolEvent);
		uncontainedObjects.add(startEvent);
		uncontainedObjects.add(beginEvent);
		uncontainedObjects.add(opcUaWorkpiece);
		uncontainedObjects.add(opcUaTool);
		uncontainedObjects.add(opcUaStart);
		uncontainedObjects.add(objectType);
	}

	private Process setupMainProcess(LocalVariable beginInt, Event workpieceEvent, LocalVariable workpieceInt,
			Event toolEvent, LocalVariable toolInt, Event startEvent, LocalVariable startInt, LocalVariable defaultWorkpiece, LocalVariable defaultTool) {
		
		
		Condition beginIntFalse  = ConfigurationUtil.initializeSimpleCondition(beginInt, Operator.EQ, CommonObjects.False);
		AbstractLoop whileBeginIntFalse = ConfigurationUtil.initializeHeadLoop("while beginInt null", ConfigurationUtil.initializeProcess("EmptyProcess"), beginIntFalse);
		
		ProcessCore.ProcessStep sendWorkpieceEventWorkpiece = ConfigurationUtil.initializeEventSource("sendWorkpieceEvent Event Source", workpieceEvent, Arrays.asList(defaultWorkpiece));
		ProcessCore.ProcessStep sendWorkpieceEventEmpty = ConfigurationUtil.initializeEventSource("sendWorkpieceEvent Event Source", toolEvent, Arrays.asList(CommonObjects.NullString));
		ProcessCore.ProcessStep sendToolEventTool = ConfigurationUtil.initializeEventSource("sendToolEvent Event Source", toolEvent, Arrays.asList(defaultTool));
		ProcessCore.ProcessStep sendToolEventEmpty = ConfigurationUtil.initializeEventSource("sendToolEvent Event Source", toolEvent, Arrays.asList(CommonObjects.NullString));
		ProcessCore.ProcessStep sendStartEventTrue = ConfigurationUtil.initializeEventSource("sendStartEvent Event Source", startEvent,  Arrays.asList(CommonObjects.True));
		ProcessCore.ProcessStep sendStartEventFalse = ConfigurationUtil.initializeEventSource("sendStartEvent Event Source", startEvent,  Arrays.asList(CommonObjects.False));

	
		ProcessCore.Process subProcess = ConfigurationUtil.initializeProcess("SubProcess");
		subProcess.getSteps().add(whileBeginIntFalse); // wait until the bgin flag is set
		subProcess.getSteps().add(sendWorkpieceEventWorkpiece); 
		subProcess.getSteps().add(sendToolEventTool);
		subProcess.getSteps().add(sendStartEventTrue);
		subProcess.getSteps().add(sendStartEventFalse);
		subProcess.getSteps().add(sendToolEventEmpty);
		subProcess.getSteps().add(sendWorkpieceEventEmpty);
		
		ProcessCore.Process mainProcess = ConfigurationUtil.initializeProcess("MainProcess");
		Condition alwaysTrue  = ConfigurationUtil.initializeSimpleCondition(CommonObjects.True, Operator.EQ, CommonObjects.True);
		mainProcess.getSteps().add(ConfigurationUtil.initializeHeadLoop("Root loop", subProcess, alwaysTrue)); // execute the subprocess forever.
		return mainProcess;
	}
}