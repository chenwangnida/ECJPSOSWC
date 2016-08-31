package ecj.ec.pso;

import ec.simple.SimpleProblemForm;

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
		System.out.print("X");

		// Create graph
		init.relevantSerivces.getGraphOutputSet().clear();
		UndirectedGraph<String, DefaultEdge> undirectedGraph = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
		undirectedGraph.addVertex("startNode");
		init.relevantSerivces.createGraphService(init.taskInput.get(0), init.taskOutput.get(0), undirectedGraph);
		System.out.println("graph printing#########################################");
		System.out.println(undirectedGraph.toString());


		double a = 1.0;
		double r = 1.0;
		double t = 0.0;
		double c = 0.0;



//	    for (Node n : graph.nodeMap.values()) {
//        	double[] qos = n.getQos();
//        	a *= qos[GraphInitializer.AVAILABILITY];
//        	r *= qos[GraphInitializer.RELIABILITY];
//        	c += qos[GraphInitializer.COST];
//        }



	}

}
