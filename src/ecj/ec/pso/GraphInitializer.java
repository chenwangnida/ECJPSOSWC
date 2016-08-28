package ecj.ec.pso;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import ec.EvolutionState;
import ec.Setup;
import ec.simple.SimpleInitializer;
import ec.util.Parameter;
import graph.Node;
import wsc.RelevantServices;

public class GraphInitializer extends SimpleInitializer {

	public double qos_w1;
	public double qos_w2;
	public double qos_w3;
	public double qos_w4;
	public static boolean dynamicNormalisation;

	public static final int TIME = 0;
	public static final int COST = 1;
	public static final int AVAILABILITY = 2;
	public static final int RELIABILITY = 3;

	public RelevantServices relevantSerivces;
	public Node relevantNode;


	@Override
	public void setup(EvolutionState state, Parameter base) {

		String service_wsdl = state.parameters.getString(new Parameter("service-wsdl"), null);
		String taxonomy_owl = state.parameters.getString(new Parameter("taxonomy-owl"), null);
		String service_wsla = state.parameters.getString(new Parameter("service-wsla"), null);

		qos_w1 = state.parameters.getDouble(new Parameter("fitness-weight1"), null);
		qos_w2 = state.parameters.getDouble(new Parameter("fitness-weight2"), null);
		qos_w3 =  state.parameters.getDouble(new Parameter("fitness-weight3"), null);
		qos_w4 = state.parameters.getDouble(new Parameter("fitness-weight4"), null);
		dynamicNormalisation = state.parameters.getBoolean(new Parameter("dynamic-normalisation"), null, false);

		//Find all relevant services
		try {
			relevantSerivces = new RelevantServices(service_wsdl, taxonomy_owl);
			relevantSerivces.allRelevantService("inst2139388127", "inst162515103");
			System.out.println("releveantService Size:" + relevantSerivces.getServiceSequence().size());

		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}

		double[] mockQos = new double[4];
		mockQos[TIME] = 0;
		mockQos[COST] = 0;
		mockQos[AVAILABILITY] = 1;
		mockQos[RELIABILITY] = 1;

		// Set size of particles
		Parameter genomeSizeParam = new Parameter("pop.subpop.0.species.genome-size");
		state.parameters.set(genomeSizeParam, ""+relevantSerivces.getServiceSequence().size());
	}

}
