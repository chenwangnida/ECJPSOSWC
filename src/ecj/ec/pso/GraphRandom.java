package ecj.ec.pso;

import java.util.Random;

import ec.util.MersenneTwisterFast;

public class GraphRandom extends Random {
	private static final long serialVersionUID = 1L;

	private MersenneTwisterFast m;

	public GraphRandom(MersenneTwisterFast m) {
		this.m = m;
	}

	@Override
	public boolean nextBoolean() {
		return m.nextBoolean();
	}

	@Override
	public double nextDouble() {
		return m.nextDouble();
	}

	@Override
	public float nextFloat() {
		return m.nextFloat();
	}

	@Override
	public int nextInt() {
		return m.nextInt();
	}

	@Override
	public int nextInt(int n) {
		return m.nextInt(n);
	}

	@Override
	public long nextLong() {
		return m.nextLong();
	}
}
