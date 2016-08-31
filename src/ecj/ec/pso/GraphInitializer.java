package ecj.ec.pso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import ec.EvolutionState;
import ec.simple.SimpleInitializer;
import ec.util.Parameter;
import wsc.RelevantServices;
import wsc.data.pool.SemanticsPool;
import wsc.data.pool.Service;

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


	List<String> taskInput;
	List<String> taskOutput;

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

		// define task
		taskInput = new ArrayList<String>();
		taskInput.add("inst2139388127");
		taskOutput = new ArrayList<String>();
		taskOutput.add("inst162515103");

		// Find all relevant services
		try {
			relevantSerivces = new RelevantServices(service_wsdl, taxonomy_owl);
			relevantSerivces.allRelevantService(taskInput.get(0), taskOutput.get(0));
			System.out.println("releveantService Size:" + relevantSerivces.getServiceSequence().size());


		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}

		// Initial StartNode and EndNode
		double[] mockQos = new double[4];
		mockQos[TIME] = 0;
		mockQos[COST] = 0;
		mockQos[AVAILABILITY] = 1;
		mockQos[RELIABILITY] = 1;

		// Set size of particles
		Parameter genomeSizeParam = new Parameter("pop.subpop.0.species.genome-size");
		state.parameters.set(genomeSizeParam, "" + relevantSerivces.getServiceSequence().size());

		 for(int i=0;i<=5;i++){
		 relevantSerivces.getGraphOutputSet().clear();
		 UndirectedGraph<String, DefaultEdge> undirectedGraph = new
		 SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
		 undirectedGraph.addVertex("startNode");
		 relevantSerivces.createGraphService(taskInput.get(0),
		 taskOutput.get(0), undirectedGraph);
		 System.out.println("graph printing#########################################");
		 System.out.println(undirectedGraph.toString());


		 }

	}

}
