package ecj.ec.pso;

import ec.simple.SimpleProblemForm;
import graph.Graph;
import graph.Node;
import graph.Edge;


import wsc.data.pool.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.UndirectedGraph;

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
		GraphParticle ind2 = (GraphParticle) ind;
		System.out.println("check point");

		init.relevantSerivces.createGraphService(input, output, undirectedGraph);


		createGraphService(String input, String output, UndirectedGraph undirectedGraph)


        Graph graph = createNewGraph(state, init.startNode.clone(), init.endNode.clone(), init.relevantSerivces.getServiceSequence(), ind2.genome);


	}


	public void createNewGraph(EvolutionState state, Node start, Node end, List<Service> relevant, double[] weights) {
		GraphInitializer init = (GraphInitializer) state.initializer;








	}


}
