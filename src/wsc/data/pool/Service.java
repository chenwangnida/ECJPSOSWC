package wsc.data.pool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graphs;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.NaiveLcaFinder;
import org.jgrapht.graph.DefaultEdge;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ecj.ec.pso.GraphInitializer;
import wsc.graph.ServiceEdge;
import wsc.owl.bean.OWLClass;
import wsc.wsdl.bean.SemExt;
import wsc.wsdl.bean.SemMessageExt;

public class Service {

	private final String serviceID;
	// list of inputInstances(individuals), rather than list of input parameter.
	private double[] qos;

	private final List<String> inputList = new ArrayList<String>();
	// list of outputInstances(individuals), ranther than list of output
	// parameter.
	private final List<String> outputList = new ArrayList<String>();

	public String getServiceID() {
		return this.serviceID;
	}

	public List<String> getInputList() {
		return this.inputList;
	}

	public List<String> getOutputList() {
		return this.outputList;
	}

	public double[] getQos() {
		return qos;
	}

	public void setQos(double[] qos) {
		this.qos = qos;
	}

	public Service(String serviceID) {
		this.serviceID = serviceID;
	}

	/**
	 * Initial service from SemMessageExt
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	public static Service initialServicefromMECE(SemMessageExt request, SemMessageExt response) {
		String serviceID = request.getServiceID();
		// verify request and response from same service.
		if (response.getServiceID().equals(serviceID) == false) {
			System.err.println("Service ID does not match");
			return null;
		}
		// valid request and response
		if (!request.isRequestMessage() || response.isRequestMessage()) {
			System.err.println("SemMessageExt type does not match");
			return null;
		}
		// initial data for service
		Service service = new Service(serviceID);
		for (SemExt se : request.getSemExt()) {
			int instBeginPos = se.getOntologyRef().indexOf("inst");
			service.inputList.add(se.getOntologyRef().substring(instBeginPos));
		}
		for (SemExt se : response.getSemExt()) {
			int instBeginPos = se.getOntologyRef().indexOf("inst");
			service.outputList.add(se.getOntologyRef().substring(instBeginPos));
		}
		return service;
	}

	/**
	 * search for services matched with current inputSet
	 *
	 * @param semanticsPool
	 * @param intputList
	 * @return boolean
	 */
	public boolean searchServiceMatchFromInputSet(SemanticsPool semanticsPool, HashSet<String> inputSet) {
		int inputMatchCount = 0;
		// check if the inputSet contains all the required inputs from services
		for (String giveninput : inputSet) {
			for (int i = 0; i < this.inputList.size(); i++) {

				String existInput = this.inputList.get(i);
				if (semanticsPool.searchSemanticMatchFromInst(giveninput, existInput)) {
					inputMatchCount++;
					// contain complete match from a single service
					if (inputMatchCount == this.inputList.size()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * search for services matched with current inputSet
	 *
	 * @param semanticsPool
	 * @param graphOutputSetMap
	 * @param intputList
	 * @return boolean
	 */
	public boolean searchServiceGraphMatchFromInputSet(SemanticsPool semanticsPool, Service service,
			HashSet<String> inputSet, DirectedGraph<String, ServiceEdge> directedGraph,
			Map<String, Service> graphOutputSetMap) {
		int inputMatchCount = 0;
		double sumsdt = 0.00;
		// check if the inputSet contains all the required inputs from services
		for (String giveninput : inputSet) {
			for (int i = 0; i < service.getInputList().size(); i++) {

				String existInput = service.getInputList().get(i);
				boolean foundmatched = semanticsPool.searchSemanticMatchFromInst(giveninput, existInput);

				if (foundmatched) {
					if (i == 0) {
						sumsdt = 0.00;
					}
					double semanticDistance = CalculateSimilarityMeasure(GraphInitializer.ontologyDAG, giveninput,
							existInput, semanticsPool);
					sumsdt += semanticDistance;
//					System.out.println("semanticDistance#############" + semanticDistance + "");
//					System.out.println("sumsdt#############" + sumsdt + "");


					inputMatchCount++;
					// contain complete match from a single service
					if (inputMatchCount == service.getInputList().size()) {

						double avgsdt = sumsdt / inputMatchCount;
						if (giveninput == "inst2139388127") {
							directedGraph.addVertex(service.getServiceID());
							directedGraph.addEdge("startNode", service.getServiceID(), new ServiceEdge(0.00, avgsdt));

						} else {
							String oldServiceID = graphOutputSetMap.get(giveninput).getServiceID();
							directedGraph.addVertex(service.getServiceID());
							directedGraph.addEdge(oldServiceID, service.getServiceID(), new ServiceEdge(0.00, avgsdt));

						}
						return true;
					}
				}
			}
		}
		return false;
	}

	private static double CalculateSimilarityMeasure(DirectedGraph<String, DefaultEdge> g, String giveninput,
			String existInput, SemanticsPool semanticsPool) {

		double similarityValue;
		// find instance related concept
		OWLClass givenClass = semanticsPool.getOwlClassHashMap()
				.get(semanticsPool.getOwlInstHashMap().get(giveninput).getRdfType().getResource().substring(1));
		OWLClass relatedClass = semanticsPool.getOwlClassHashMap()
				.get(semanticsPool.getOwlInstHashMap().get(existInput).getRdfType().getResource().substring(1));

		String a = givenClass.getID();
		String b = relatedClass.getID();

		// find the lowest common ancestor
		String lca = new NaiveLcaFinder<String, DefaultEdge>(g).findLca(a, b);

		double N = new DijkstraShortestPath(g, GraphInitializer.rootconcept, lca).getPathLength() + 1;
		double N1 = new DijkstraShortestPath(g, GraphInitializer.rootconcept, a).getPathLength() + 1;
		double N2 = new DijkstraShortestPath(g, GraphInitializer.rootconcept, b).getPathLength() + 1;

		double sim = 2 * N / (N1 + N2);
		// System.out.println("SemanticDistance:" + sim + "
		// ##################");

		if (isNeighbourConcept(g, a, b) == true) {
			double L = new DijkstraShortestPath(g, lca, a).getPathLength()
					+ new DijkstraShortestPath(g, lca, b).getPathLength();

			int D = MaxDepth(g) + 1;
			int r = 1;
			double simNew = 2 * N * (Math.pow(Math.E, -r * L / D)) / (N1 + N2);
			// System.out.println("SemanticDistance2:" + simNew + "
			// ##################");
			similarityValue = simNew;
		} else {
			similarityValue = sim;
		}

		return similarityValue;
	}

	private static boolean isNeighbourConcept(DirectedGraph<String, DefaultEdge> g, String a, String b) {

		boolean isNeighbourConcept = false;
		Set<DefaultEdge> incomingEdgeList1 = g.incomingEdgesOf(a);
		Set<DefaultEdge> incomingEdgeList2 = g.incomingEdgesOf(b);

		for (DefaultEdge e1 : incomingEdgeList1) {
			String source1 = g.getEdgeSource(e1);
			for (DefaultEdge e2 : incomingEdgeList2) {
				String source2 = g.getEdgeSource(e2);
				if (source1.equals(source2)) {
					isNeighbourConcept = true;
				}
			}
		}

		return isNeighbourConcept;
	}

	private static int MaxDepth(DirectedGraph<String, DefaultEdge> g) {

		int depth = 0;

		Set<String> verticeset = g.vertexSet();

		// update the depth while iterator successor
		for (String v : verticeset) {
			List<String> verticeList = Graphs.successorListOf(g, v);

			if (verticeList.size() > 0) {
				depth++;
			}
		}

		return depth;

	}

}
