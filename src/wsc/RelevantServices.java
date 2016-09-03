package wsc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.jgrapht.DirectedGraph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import wsc.data.pool.SWSPool;
import wsc.data.pool.SemanticsPool;
import wsc.data.pool.Service;

public class RelevantServices {

	private SWSPool swsPool;

	// current output instance list for all relevant services
	private final HashSet<String> outputSet = new HashSet<String>();

	private final SemanticsPool semanticsPool;

	// save all the relevant services
	private final List<Service> serviceSequence = new LinkedList<Service>();
	// save all semantics
	private HashSet<String>  graphOutputSet = new HashSet<String>();

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

	public RelevantServices(String serviceFilePath, String owlFilePath) throws FileNotFoundException, JAXBException {
		this.semanticsPool = SemanticsPool.createSemanticsFromOWL(owlFilePath);
		this.swsPool = SWSPool.parseXML(this.semanticsPool, serviceFilePath);
	}

	/**
	 * check whether output is required by the task
	 *
	 * @param givenoutput
	 * @return
	 */
	private boolean checkOutputSet(String output, DirectedGraph<String, DefaultEdge> undirectedGraph,
			Service service) {
		for (String outputInst : this.graphOutputSet) {
			if (this.semanticsPool.searchSemanticMatchFromInst(outputInst, output)) {

				undirectedGraph.addVertex("endNode");
				undirectedGraph.addEdge(service.getServiceID(), "endNode");
				return true;
			}
		}
		return false;
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
	public void allRelevantService(String input, String output) throws JAXBException, IOException {
		this.outputSet.add(input);
		do {
			Service service = this.swsPool.findPossibleService(this.outputSet);
			if (service == null) {
				System.out.println("No more service satisfied");
				return;
			}
			serviceSequence.add(service);
		} while (true);// while(!this.checkOutputSet(output))
	}

	public void createGraphService(String input, String output, DirectedGraph<String, DefaultEdge> directedGraph) {

		this.graphOutputSet.add(input);
		SWSPool swsPool = new SWSPool();

		List<Service> serviceCandidates = new ArrayList<Service>();
		serviceCandidates.addAll(serviceSequence);

		Service service;
		do {
			service = swsPool.createGraphService(this.graphOutputSet, serviceCandidates, this.semanticsPool,
					directedGraph);
			if (service == null) {
				System.out.println("No more service satisfied");
				return;
			}
		} while (!this.checkOutputSet(output, directedGraph, service));

	}

}
