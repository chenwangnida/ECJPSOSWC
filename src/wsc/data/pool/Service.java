package wsc.data.pool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.NaiveLcaFinder;
import org.jgrapht.graph.DefaultEdge;

import ecj.ec.pso.GraphInitializer;
import wsc.graph.ParamterConn;
import wsc.graph.ServiceEdge;
import wsc.graph.ServiceInput;
import wsc.graph.ServiceOutput;
import wsc.owl.bean.OWLClass;

public class Service implements Comparable<Service> {

	private final String serviceID;
	// list of inputInstances(individuals), rather than list of input parameter.
	private double[] qos;

	private List<ServiceInput> inputList = new ArrayList<ServiceInput>();
	// list of outputInstances(individuals), ranther than list of output
	private List<ServiceOutput> outputList = new ArrayList<ServiceOutput>();

	private static List<ParamterConn> pConnList0 = new ArrayList<ParamterConn>();
	private static List<ServiceInput> inputList0 = new ArrayList<ServiceInput>();
	private static Set<String> sourceSerIdSet = new HashSet<String>();

	private double score;

	public Service(String serviceID, double[] qos, List<ServiceInput> inputList, List<ServiceOutput> outputList) {
		super();
		this.serviceID = serviceID;
		this.qos = qos;
		this.inputList = inputList;
		this.outputList = outputList;
	}

	public List<ServiceInput> getInputList() {
		return inputList;
	}

	public void setInputList(List<ServiceInput> inputList) {
		this.inputList = inputList;
	}

	public List<ServiceOutput> getOutputList() {
		return outputList;
	}

	public void setOutputList(List<ServiceOutput> outputList) {
		this.outputList = outputList;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public String getServiceID() {
		return this.serviceID;
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

	@Override
	public int compareTo(Service o) {
		if (score > o.score)
			return -1;
		else if (score < o.score)
			return 1;
		else
			return 0;
	}

	@Override
	public String toString() {
		return "(" + score + ", " + serviceID + ")";
	}

	// /**
	// * Initial service from SemMessageExt
	// *
	// * @param request
	// * @param response
	// * @return
	// */
	// public static Service initialServicefromMECE(SemMessageExt request,
	// SemMessageExt response) {
	// String serviceID = request.getServiceID();
	// // verify request and response from same service.
	// if (response.getServiceID().equals(serviceID) == false) {
	// System.err.println("Service ID does not match");
	// return null;
	// }
	// // valid request and response
	// if (!request.isRequestMessage() || response.isRequestMessage()) {
	// System.err.println("SemMessageExt type does not match");
	// return null;
	// }
	// // initial data for service
	// Service service = new Service(serviceID);
	// for (SemExt se : request.getSemExt()) {
	// int instBeginPos = se.getOntologyRef().indexOf("inst");
	// service.inputList.add(se.getOntologyRef().substring(instBeginPos));
	// }
	// for (SemExt se : response.getSemExt()) {
	// int instBeginPos = se.getOntologyRef().indexOf("inst");
	// service.outputList.add(se.getOntologyRef().substring(instBeginPos));
	// }
	// return service;
	// }

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

				String existInput = this.inputList.get(i).getInput();
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
			List<String> graphOutputList, DirectedGraph<String, ServiceEdge> directedGraph,
			Map<String, Service> graphOutputListMap) {
		pConnList0.clear();
		inputList0.clear();
		int inputMatchCount = 0;
		double summt = 0.00;
		double sumdst = 0.00;
		inputList0.addAll(service.getInputList());

		for (int i = 0; i < graphOutputList.size(); i++) {
			String giveninput = graphOutputList.get(i);
			for (int j = 0; j < inputList0.size(); j++) {
				ServiceInput serInput = inputList0.get(j);
				if (!serInput.isSatified()) {
					String existInput = inputList0.get(j).getInput();
					ParamterConn pConn = semanticsPool.searchSemanticMatchTypeFromInst(giveninput, existInput);
					boolean foundmatched = pConn.isConsidered();
					if (foundmatched) {
						serInput.setSatified(true);
						pConn.setOutputInst(giveninput);
						if (GraphInitializer.taskInput.contains(giveninput)) {
							pConn.setSourceServiceID("startNode");
						} else {
							pConn.setSourceServiceID(graphOutputListMap.get(giveninput).getServiceID());
						}
						double similarity = CalculateSimilarityMeasure(GraphInitializer.ontologyDAG, giveninput,
								existInput, semanticsPool);
						pConn.setSimilarity(similarity);
						pConnList0.add(pConn);
//						System.out.println("##########found One");
						break;// each inst can only be used for one time
					}

				}

			}

		}

		for (ServiceInput sInput : inputList0) {
			boolean sf = sInput.isSatified();
			if (sf == true) {
				inputMatchCount++;
			}
		}

		if (inputMatchCount == inputList0.size()) {

			directedGraph.addVertex(service.getServiceID());
			sourceSerIdSet.clear();
			// how many sourceService are connected
			for (ParamterConn p : pConnList0) {
				String sourceSerID = p.getSourceServiceID();
				sourceSerIdSet.add(sourceSerID);
			}
			List<ServiceEdge> serEdgeList = new ArrayList<ServiceEdge>();
			// Edge are needed for each sourceService
			for (String sourceSerID : sourceSerIdSet) {
				ServiceEdge serEdge = new ServiceEdge(0, 0);
				serEdge.setSourceService(sourceSerID);
				// how many parameter connection needed for each Edge
				for (ParamterConn p : pConnList0) {
					if (p.getSourceServiceID().equals(sourceSerID)) {
						serEdge.getpConnList().add(p);
					}
				}
				// add Edge to a EdgeList to calcute each edge aggregation
				// and buld edge for graph
				serEdgeList.add(serEdge);
			}

			for (ServiceEdge edge : serEdgeList) {
				for (int i1 = 0; i1 < edge.getpConnList().size(); i1++) {
					ParamterConn pCo = edge.getpConnList().get(i1);
					summt += pCo.getMatchType();
					sumdst += pCo.getSimilarity();

				}
				int count = edge.getpConnList().size();
				edge.setAvgmt(summt / count);
				edge.setAvgsdt(sumdst / count);
				directedGraph.addEdge(edge.getSourceService(), service.getServiceID(), edge);
			}
			return true;
		}

		return false;
	}

	public static double CalculateSimilarityMeasure(DirectedGraph<String, DefaultEdge> g, String giveninput,
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
		//
		// if (isNeighbourConcept(g, a, b) == true) {
		// double L = new DijkstraShortestPath(g, lca, a).getPathLength()
		// + new DijkstraShortestPath(g, lca, b).getPathLength();
		//
		// int D = MaxDepth(g) + 1;
		// int r = 1;
		// double simNew = 2 * N * (Math.pow(Math.E, -r * L / D)) / (N1 + N2);
		// // System.out.println("SemanticDistance2:" + simNew + "
		// // ##################");
		// similarityValue = simNew;
		// } else {
		// similarityValue = sim;
		// }

		return sim;
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
