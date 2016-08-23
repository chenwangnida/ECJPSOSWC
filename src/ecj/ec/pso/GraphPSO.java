package ecj.ec.pso;

import ec.simple.SimpleProblemForm;
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

	}

}
