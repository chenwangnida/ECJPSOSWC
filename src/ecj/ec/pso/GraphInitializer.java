package ecj.ec.pso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.jgrapht.DirectedGraph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import ec.EvolutionState;
import ec.simple.SimpleInitializer;
import ec.util.Parameter;
import wsc.InitialWSCPool;
import wsc.data.pool.SemanticsPool;
import wsc.data.pool.Service;
import wsc.owl.bean.OWLClass;

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

	public static final int EXACT = 0;
	public static final int PlUGIN = 1;
	public static final int SUBSUME = 2;
	public static final int INTERSECTION = 3;

	public InitialWSCPool initialWSCPool;

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

		// Initial all data related to Web service composition  pools
		try {
			initialWSCPool = new InitialWSCPool(service_wsdl, taxonomy_owl);
			initialWSCPool.allRelevantService(taskInput.get(0), taskOutput.get(0));
			System.out.println("releveantService Size:" + initialWSCPool.getServiceSequence().size());

		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}

		// Initial ontology DAG data
		DirectedGraph<String, DefaultEdge> ontologyDAG = createOntologyDAG(initialWSCPool);
		System .out.println("&&&&&&&&&&&&&&ontology DAG&&&& vertice size : "+ ontologyDAG.vertexSet().size()+"edge number : "+ontologyDAG.edgeSet().size());

		// Initial StartNode and EndNode
		double[] mockQos = new double[4];
		mockQos[TIME] = 0;
		mockQos[COST] = 0;
		mockQos[AVAILABILITY] = 1;
		mockQos[RELIABILITY] = 1;

		// Set size of particles
		Parameter genomeSizeParam = new Parameter("pop.subpop.0.species.genome-size");
		state.parameters.set(genomeSizeParam, "" + initialWSCPool.getServiceSequence().size());
	}

	private static DirectedAcyclicGraph<String, DefaultEdge> createOntologyDAG(InitialWSCPool initialWSCPool) {

		DirectedAcyclicGraph<String, DefaultEdge> g = new DirectedAcyclicGraph<String, DefaultEdge>(DefaultEdge.class);

		HashMap<String, OWLClass> owlClassMap = initialWSCPool.getSemanticsPool().getOwlClassHashMap();

		for (String concept : owlClassMap.keySet()) {
			g.addVertex(concept);

		}

		for (OWLClass owlClass : owlClassMap.values()) {
			if (owlClass.getSubClassOf() != null) {
				String source = owlClass.getSubClassOf().getResource().substring(1);
				String target = owlClass.getID();
				g.addEdge(source, target);
			}else{
				System.out.println("&&&&&&&&&&&&&&&&&&&&&&& found  null in getSubClassOf");
			}
		}
		return g;
	}

}
