package ecj.ec.pso;

import ec.simple.SimpleFitness;
import ec.simple.SimpleProblemForm;
import wsc.data.pool.Service;
import wsc.graph.ServiceEdge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.AllDirectedPaths;
import org.jgrapht.graph.DefaultDirectedGraph;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;

public class GraphPSO extends Problem implements SimpleProblemForm {

	/**
	 *
	 */
	private static final long serialVersionUID = 2181889697257509451L;

	@Override
	public void evaluate(EvolutionState state, Individual ind, int subpopulation, int threadnum) {
		GraphInitializer init = (GraphInitializer) state.initializer;
		if (ind.evaluated)
			return;
		if (!(ind instanceof GraphParticle))
			state.output.fatal("It is not a graph particle objective !", null);

		GraphParticle individual = (GraphParticle) ind;

		// Create graph
		DirectedGraph<String, ServiceEdge> directedGraph = graphRepresentation(init, individual);

		// set both functional and nonfunctional attributes for fitness
		// functiona
		SetAttributes(state, init, individual, directedGraph);

	}

	private void SetAttributes(EvolutionState state, GraphInitializer init, GraphParticle individual,
			DirectedGraph<String, ServiceEdge> directedGraph) {

		double a = 1.0;
		double r = 1.0;
		double t = 0.0;
		double c = 0.0;
		double mt = 1.0;
		double dst = 1.0; // Exact Match dst = 1 ; 0 < = dst < = 1

		// set a, r, c aggregation
		Set<String> verticeSet = directedGraph.vertexSet();

		Map<String, double[]> ServiceQoSMap = init.ServiceQoSMap;

		for (String v : verticeSet) {
			if (!v.equals("startNode") && !v.equals("endNode")) {
				double qos[] = ServiceQoSMap.get(v);
				a *= qos[GraphInitializer.AVAILABILITY];
				r *= qos[GraphInitializer.RELIABILITY];
				c += qos[GraphInitializer.COST];

			}
		}

		// set time aggregation
		List<String> longestVertexList = getLongestPathVertexList(directedGraph);
		for (String v : longestVertexList) {
			if (!v.equals("startNode") && !v.equals("endNode")) {
				double qos[] = ServiceQoSMap.get(v);
				t += qos[GraphInitializer.TIME];
			}
		}

		// set mt,dst aggregation

		for (ServiceEdge serviceEdge : directedGraph.edgeSet()) {
			mt *= serviceEdge.getAvgmt();
			dst += serviceEdge.getAvgsdt();
		}

		individual.setAvailability(a);
		individual.setReliability(r);
		individual.setTime(t);
		individual.setCost(c);
		individual.setSemanticDistance(dst);
		individual.setMatchingType(mt);

		if (GraphInitializer.normalisation) {
			double fitness = calculateFitness(mt, dst, a, r, t, c, init);

			((SimpleFitness) individual.fitness).setFitness(state,
					// ...the fitness...
					fitness,
					/// ... is the individual ideal? Indicate here...
					false);
			individual.evaluated = true;
		}

		individual.setStrRepresentation(directedGraph.toString());
	}

	private DirectedGraph<String, ServiceEdge> graphRepresentation(GraphInitializer init, GraphParticle individual) {
		init.initialWSCPool.getGraphOutputSet().clear();
		DirectedGraph<String, ServiceEdge> directedGraph = new DefaultDirectedGraph<String, ServiceEdge>(
				ServiceEdge.class);
		directedGraph.addVertex("startNode");
		init.initialWSCPool.createGraphService(init.taskInput, init.taskOutput, directedGraph, individual.genome,
				init.serviceToIndexMap);

		// System.out.println("orginal graph##########" +
		// directedGraph.toString());

		while (true) {
			List<String> dangleVerticeList = dangleVerticeList(directedGraph);
			if (dangleVerticeList.size() == 0) {
				break;
			}
			removeCurrentdangle(directedGraph, dangleVerticeList);
		}

		System.out.println("compositeServiceGraph:" + directedGraph.toString());
		return directedGraph;

	}

	private static List<String> dangleVerticeList(DirectedGraph<String, ServiceEdge> directedGraph) {
		Set<String> allVertice = directedGraph.vertexSet();

		List<String> dangleVerticeList = new ArrayList<String>();
		for (String v : allVertice) {
			int relatedOutDegree = directedGraph.outDegreeOf(v);

			if (relatedOutDegree == 0 && !v.equals("endNode")) {
				dangleVerticeList.add(v);

			}
		}
		return dangleVerticeList;
	}

	private static void removeCurrentdangle(DirectedGraph<String, ServiceEdge> directedGraph,
			List<String> dangleVerticeList) {
		// Iterator the endTangle
		for (String danglevertice : dangleVerticeList) {

			Set<ServiceEdge> relatedEdge = directedGraph.incomingEdgesOf(danglevertice);
			Set<String> potentialTangleVerticeList = new HashSet<String>();

			for (ServiceEdge edge : relatedEdge) {
				String potentialTangleVertice = directedGraph.getEdgeSource(edge);
				// System.out.println("potentialTangleVertice:" +
				// potentialTangleVertice);
				potentialTangleVerticeList.add(potentialTangleVertice);
			}

			directedGraph.removeVertex(danglevertice);
		}
	}

	public static List<String> getLongestPathVertexList(DirectedGraph<String, ServiceEdge> g) {
		// A algorithm to find all paths
		AllDirectedPaths<String, ServiceEdge> allPath = new AllDirectedPaths<String, ServiceEdge>(g);
		List<GraphPath<String, ServiceEdge>> pathList = allPath.getAllPaths("startNode", "endNode", true, null);

		int MaxPathLength = 0;
		int IndexPathLength = 0;

		for (int i = 0; i < pathList.size(); i++) {

			int pathLength = pathList.get(i).getEdgeList().size();
			if (pathLength > MaxPathLength) {
				IndexPathLength = i;
				MaxPathLength = pathLength;
			}
		}
		// return pathList.get(IndexPathLength).getEdgeList();
		return Graphs.getPathVertexList(pathList.get(IndexPathLength));
	}

	private double calculateFitness(double mt, double dst, double a, double r, double t, double c,
			GraphInitializer init) {
		mt = normaliseMatchType(mt, init);
		dst = normaliseDistanceValue(dst, init);
		a = normaliseAvailability(a, init);
		r = normaliseReliability(r, init);
		t = normaliseTime(t, init);
		c = normaliseCost(c, init);

		double fitness = ((init.sf_w1 * mt) + (init.sf_w2 * dst) + (init.qos_w1 * a) + (init.qos_w1 * r)
				+ (init.qos_w1 * t) + (init.qos_w1 * c));
		return fitness;
	}

	private double normaliseMatchType(double matchType, GraphInitializer init) {
		if (init.maxMatchType - init.minMatchType == 0.0)
			return 1.0;
		else
			return (matchType - init.minMatchType) / (init.maxMatchType - init.minMatchType);
	}

	private double normaliseDistanceValue(double distanceValue, GraphInitializer init) {
		if (init.maxDistanceValue - init.minDistanceValue == 0.0)
			return 1.0;
		else
			return (distanceValue - init.minDistanceValue) / (init.maxDistanceValue - init.minDistanceValue);
	}

	private double normaliseAvailability(double availability, GraphInitializer init) {
		if (init.maxAvailability - init.minAvailability == 0.0)
			return 1.0;
		else
			return (availability - init.minAvailability) / (init.maxAvailability - init.minAvailability);
	}

	private double normaliseReliability(double reliability, GraphInitializer init) {
		if (init.maxReliability - init.minReliability == 0.0)
			return 1.0;
		else
			return (reliability - init.minReliability) / (init.maxReliability - init.minReliability);
	}

	private double normaliseTime(double time, GraphInitializer init) {
		if (init.maxTime - init.minTime == 0.0)
			return 1.0;
		else
			return (init.maxTime - time) / (init.maxTime - init.minTime);
	}

	private double normaliseCost(double cost, GraphInitializer init) {
		if (init.maxCost - init.minCost == 0.0)
			return 1.0;
		else
			return (init.maxCost - cost) / (init.maxCost - init.minCost);
	}

}