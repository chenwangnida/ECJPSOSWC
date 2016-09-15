package wsc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.jgrapht.DirectedGraph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import ecj.ec.pso.GraphInitializer;
import wsc.data.pool.SWSPool;
import wsc.data.pool.SemanticsPool;
import wsc.data.pool.Service;
import wsc.graph.ParamterConn;
import wsc.graph.ServiceEdge;

public class InitialWSCPool {

	private SWSPool swsPool;

	// current output instance list for all relevant services
	private final HashSet<String> outputSet = new HashSet<String>();

	private final SemanticsPool semanticsPool;

	// save all the relevant services
	private final List<Service> serviceSequence = new LinkedList<Service>();
	// save all semantics
	private HashSet<String> graphOutputSet = new HashSet<String>();

	private static List<ParamterConn> pConnList = new ArrayList<ParamterConn>();
	private static Set<String> sourceSerIdSet = new HashSet<String>();

	public HashSet<String> getGraphOutputSet() {
		return graphOutputSet;
	}

	public void setGraphOutputSet(HashSet<String> graphOutputSet) {
		this.graphOutputSet = graphOutputSet;
	}

	// set and get
	public SWSPool getSwsPool() {
		return swsPool;
	}

	public HashSet<String> getOutputSet() {
		return outputSet;
	}

	public SemanticsPool getSemanticsPool() {
		return semanticsPool;
	}

	public List<Service> getServiceSequence() {
		return serviceSequence;
	}

	/**
	 * using service file and owl file to create semantics pool and service pool
	 *
	 * @param serviceFilePath
	 * @param owlFilePath
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 */

	public InitialWSCPool(String serviceFilePath, String owlFilePath) throws FileNotFoundException, JAXBException {
		this.semanticsPool = SemanticsPool.createSemanticsFromOWL(owlFilePath);
		this.swsPool = SWSPool.parseWSCServiceFile(this.semanticsPool, serviceFilePath);
	}

	/**
	 * check whether output is required by the task
	 *
	 * @param givenoutput
	 * @return
	 */
	private boolean checkOutputSet(DirectedGraph<String, ServiceEdge> directedGraph, SWSPool swsPool) {
		int numbermatched = 0;
		double summt = 0.00;
		double sumdst = 0.00;
		pConnList.clear();
		// List<ParamterConn> pConnList = new ArrayList<ParamterConn>();
		for (String outputrequ : GraphInitializer.taskOutput) {
			for (String outputInst : this.graphOutputSet) {
				ParamterConn pConn = this.semanticsPool.searchSemanticMatchTypeFromInst(outputInst, outputrequ);
				boolean foundmatched = pConn.isConsidered();
				if (foundmatched) {
					numbermatched++;
					double similarity = Service.CalculateSimilarityMeasure(GraphInitializer.ontologyDAG, outputInst,
							outputrequ, this.semanticsPool);
					pConn.setOutputInst(outputInst);
					pConn.setOutputrequ(outputrequ);
					pConn.setSourceServiceID(swsPool.getGraphOutputSetMap().get(outputInst).getServiceID());
					pConn.setSimilarity(similarity);
					pConnList.add(pConn);
				}

			}
		}
		if (GraphInitializer.taskOutput.size() == numbermatched) {
			directedGraph.addVertex("endNode");
			sourceSerIdSet.clear();
			for (ParamterConn p : pConnList) {
				String sourceSerID = p.getSourceServiceID();
				sourceSerIdSet.add(sourceSerID);
			}

			List<ServiceEdge> serEdgeList = new ArrayList<ServiceEdge>();
			for (String sourceSerID : sourceSerIdSet) {
				ServiceEdge serEdge = new ServiceEdge(0, 0);
				serEdge.setSourceService(sourceSerID);
				for (ParamterConn p : pConnList) {
					if (p.getSourceServiceID().equals(sourceSerID)) {
						serEdge.getpConnList().add(p);
					}
				}
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
				directedGraph.addEdge(edge.getSourceService(), "endNode", edge);
			}

			return true;
		} else {
			return false;
		}
	}

	/**
	 * given a task associated with input and output to find a potential
	 * services
	 *
	 * @param giveninput
	 *
	 * @param givenoutput
	 *
	 */
	public void allRelevantService(Set input, Set output) throws JAXBException, IOException {
		this.outputSet.addAll(input);
		int i = 0;
		do {
			Service service = this.swsPool.findPossibleService(this.outputSet);
			if (service == null) {
				// System.out.println("No more service satisfied");
				// System.out.println("No more service satisfied");
				// System.out.println("No more service satisfied");
				return;
			}
			serviceSequence.add(service);

			// i++;
			// System.out.println("No service :" + i);

		} while (true);// while(!this.checkOutputSet(output))
	}

	public void createGraphService(Set input, Set output, DirectedGraph<String, ServiceEdge> directedGraph,
			double[] weights, Map<String, Integer> serviceToIndexMap) {

		this.graphOutputSet.addAll(input);
		SWSPool swsPool = new SWSPool();

		SetWeightsToServiceList(serviceToIndexMap, serviceSequence, weights);

		List<Service> serviceCandidates = new ArrayList<Service>();
		serviceCandidates.addAll(serviceSequence);
		// Sort the service in serviceCandidates list by weights from Particle
		// location
		Collections.sort(serviceCandidates);

		Service service;
		do {
			service = swsPool.createGraphService(this.graphOutputSet, serviceCandidates, this.semanticsPool,
					directedGraph);
			if (service == null) {
				System.out.println("No more service satisfied");
				return;
			}
		} while (!this.checkOutputSet(directedGraph, swsPool));

	}

	private void SetWeightsToServiceList(Map<String, Integer> serviceToIndexMap, List<Service> serviceSequence,
			double[] weights) {
		// Go through all relevant nodes
		for (Service service : serviceSequence) {
			// Find the index for that node
			int index = serviceToIndexMap.get(service.getServiceID());
			service.setScore(weights[index]);
		}
	}

}
