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
import wsc.data.pool.ParamterConn;
import wsc.data.pool.SWSPool;
import wsc.data.pool.SemanticsPool;
import wsc.data.pool.Service;
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
	private boolean checkOutputSet(Set<String> output, DirectedGraph<String, ServiceEdge> directedGraph,
			SWSPool swsPool) {
		int numbermatched = 0;
		double mt = 1;
		double dst = 1;
		pConnList.clear();
//		List<ParamterConn> pConnList = new ArrayList<ParamterConn>();
		for (String outputrequ : output) {
			for (String outputInst : this.graphOutputSet) {
				ParamterConn pConn = this.semanticsPool.searchSemanticMatchTypeFromInst(outputInst, outputrequ);
				boolean foundmatched = pConn.isConsidered();
				if (foundmatched) {
					numbermatched++;
					pConn.setOutputInst(outputInst);
					pConn.setOutputrequ(outputrequ);
					pConnList.add(pConn);
				}

			}
		}
		if (output.size() == numbermatched) {
			directedGraph.addVertex("endNode");

			for (ParamterConn pConn : pConnList) {
				String outputInst = pConn.getOutputInst();
				String outputrequ = pConn.getOutputrequ();

				mt = pConn.getMatchType();
				dst = Service.CalculateSimilarityMeasure(GraphInitializer.ontologyDAG, outputInst, outputrequ,
						this.semanticsPool);
				String serviceID = swsPool.getGraphOutputSetMap().get(outputInst).getServiceID();
				directedGraph.addEdge(serviceID, "endNode", new ServiceEdge(mt, dst));

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
		do {
			Service service = this.swsPool.findPossibleService(this.outputSet);
			if (service == null) {
				System.out.println("No more service satisfied");
				return;
			}
			serviceSequence.add(service);
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
		} while (!this.checkOutputSet(output, directedGraph, swsPool));

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
