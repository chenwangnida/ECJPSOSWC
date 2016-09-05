package ecj.ec.pso;

import ec.simple.SimpleProblemForm;

import wsc.data.pool.Service;
import wsc.graph.ServiceEdge;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.AllDirectedPaths;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.NaiveLcaFinder;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;

public class GraphPSO extends Problem implements SimpleProblemForm {

	@Override
	public void evaluate(EvolutionState state, Individual ind, int subpopulation, int threadnum) {
		GraphInitializer init = (GraphInitializer) state.initializer;
		if (ind.evaluated)
			return;
		if (!(ind instanceof GraphParticle))
			state.output.fatal("It is not a graph particle objective !", null);

		GraphParticle individual = (GraphParticle) ind;

		// Create graph
		init.initialWSCPool.getGraphOutputSet().clear();
		DirectedGraph<String, ServiceEdge> directedGraph = new DefaultDirectedGraph<String, ServiceEdge>(
				ServiceEdge.class);
		directedGraph.addVertex("startNode");
		init.initialWSCPool.createGraphService(init.taskInput.get(0), init.taskOutput.get(0), directedGraph);
		// System.out.println(directedGraph.toString());

		// initially find current dangleVertice
		Set<String> allVertice = directedGraph.vertexSet();
		List<String> dangleVerticeList = new ArrayList<String>();
		for (String v : allVertice) {
			int relatedOutDegree = directedGraph.outDegreeOf(v);
			if (relatedOutDegree == 0 && !v.equals("v5")) {
				dangleVerticeList.add(v);
			}
		}

		// recursion for find end tangle after remove the initial ones
		removeAlltangle(directedGraph, dangleVerticeList);

		System.out.println("graph printing after removing all dangle#########################################");
		System.out.println(directedGraph.toString());

		// set both functional and nonfunctional attributes( QoS )
		double a = 1.0;
		double r = 1.0;
		double t = 0.0;
		double c = 0.0;
		double mt = 1.0;
		double dst = 1.0; // Exact Match dst = 1 ; 0 < = dst < = 1

		// set availability, reliability, cost
		Set<String> verticeSet = directedGraph.vertexSet();

		Map<String, double[]> qosMap = init.initialWSCPool.getSwsPool().getQosServiceMap();

		for (String v : verticeSet) {
			if (!v.equals("startNode") && !v.equals("endNode")) {
				double qos[] = qosMap.get(v);
				a *= qos[GraphInitializer.AVAILABILITY];
				r *= qos[GraphInitializer.RELIABILITY];
				c += qos[GraphInitializer.COST];
			}
		}

		// set time

		// get edge list of the LogestPaths
		List<String> longestVertexList = getLongestPathVertexList(directedGraph);
		for (String v : longestVertexList) {
			if (!v.equals("startNode") && !v.equals("endNode")) {
				double qos[] = qosMap.get(v);
				t += qos[GraphInitializer.TIME];
			}
		}

		System.out.println("#########AVAILABILITY:" + a + "##########RELIABILITY:" + r + "#########COST:" + c
				+ "#########time" + t);

		// set average matching type, average semantic distance value for each match
		
		for(ServiceEdge serviceEdge: directedGraph.edgeSet()){
			mt *= serviceEdge.getAvgmt();
			dst += serviceEdge.getAvgsdt();
		}

		individual.setStrRepresentation(directedGraph.toString());
		individual.setAvailability(a);
		individual.setReliability(r);
		individual.setTime(t);
		individual.setCost(c);
		individual.setSemanticDistance(dst);
		individual.setMatchingType(mt);
		
	}

	@Override
	public void finishEvaluating(EvolutionState state, int threadnum) {
		GraphInitializer init = (GraphInitializer) state.initializer;

	}

	public static void removeAlltangle(DirectedGraph<String, ServiceEdge> directedGraph,
			List<String> dangleVerticeList) {
		// Iterator the endTangle
		for (String danglevertice : dangleVerticeList) {

			Set<ServiceEdge> relatedEdge = directedGraph.incomingEdgesOf(danglevertice);
			Set<String> potentialTangleVerticeList = new HashSet<String>();


			for (ServiceEdge edge : relatedEdge) {
				String potentialTangleVertice = directedGraph.getEdgeSource(edge);
				System.out.println("potentialTangleVertice:" + potentialTangleVertice);
				potentialTangleVerticeList.add(potentialTangleVertice);
			}

			directedGraph.removeVertex(danglevertice);

			for (String potentialTangleVertice : potentialTangleVerticeList) {
				int relatedOutDegree = directedGraph.outDegreeOf(potentialTangleVertice);
				List<String> dangleVerticeList1 = new ArrayList<String>();
				if (relatedOutDegree == 0) {
					dangleVerticeList1.add(potentialTangleVertice);
					removeAlltangle(directedGraph, dangleVerticeList1);
				} else {
					return;
				}
			}

		}
	}

	public static List<String> getLongestPathVertexList(DirectedGraph g) {
		// A algorithm to find all paths
		AllDirectedPaths<String, DefaultEdge> allPath = new AllDirectedPaths<String, DefaultEdge>(g);
		List<GraphPath<String, DefaultEdge>> pathList = allPath.getAllPaths("startNode", "endNode", true, null);

		List<DefaultEdge> edgeList;
		List<DefaultEdge> LongestEdgeList;
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
}
