package ecj.ec.pso;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import ec.EvolutionState;
import ec.Setup;
import ec.simple.SimpleInitializer;
import ec.util.Parameter;
import wsc.CompositionAllPossibleServices;

public class GraphInitializer extends SimpleInitializer {

	public double qos_w1;
	public double qos_w2;
	public double qos_w3;
	public double qos_w4;
	public static boolean dynamicNormalisation;

	@Override
	public void setup(EvolutionState state, Parameter base) {

		String service_wsdl = state.parameters.getString(new Parameter("service-wsdl"), null);
		String taxonomy_owl = state.parameters.getString(new Parameter("taxonomy-owl"), null);
		String service_wsla = state.parameters.getString(new Parameter("service-wsla"), null);

		qos_w1 = state.parameters.getDouble(new Parameter("fitness-weight1"), null);
		qos_w2 = state.parameters.getDouble(new Parameter("fitness-weight2"), null);
		qos_w3 = state.parameters.getDouble(new Parameter("fitness-weight3"), null);
		qos_w4 = state.parameters.getDouble(new Parameter("fitness-weight4"), null);
		dynamicNormalisation = state.parameters.getBoolean(new Parameter("dynamic-normalisation"), null, false);

		CompositionAllPossibleServices compositionSollution;
		try {
			compositionSollution = new CompositionAllPossibleServices(service_wsdl, taxonomy_owl);
			compositionSollution.compositeServices("inst2139388127", "inst162515103");

		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}


	}

}
