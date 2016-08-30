package ecj.ec.pso;

import ec.simple.SimpleProblemForm;
import wsc.data.pool.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

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
		System.out.print("A");
		
//		UndirectedGraph<String, DefaultEdge> undirectedGraph = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
//		undirectedGraph.addVertex("startNode");
//		init.relevantSerivces.createGraphService(init.taskInput.get(0), init.taskOutput.get(0), undirectedGraph);
//		 System.out.println("graph printing#########################################");
//        System.out.println(undirectedGraph.toString());


	}



}
