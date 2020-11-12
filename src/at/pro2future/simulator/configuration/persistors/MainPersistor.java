package at.pro2future.simulator.configuration.persistors;

public class MainPersistor {
	
	public static void main(String[] args) {
		DefaultSimulatorConfigurationPersistor dscp = new DefaultSimulatorConfigurationPersistor();
		dscp.run();
		
		ToolControlConfigurationPersistor tccp = new ToolControlConfigurationPersistor();
		tccp.run();
		WorkpieceControlConfigurationPersistor wccp = new WorkpieceControlConfigurationPersistor();
		wccp.run();
		
		MillingControlConfigurationPersistor mccp = new MillingControlConfigurationPersistor(tccp.getConfiguration(), wccp.getConfiguration());
		mccp.run();
	}
}
