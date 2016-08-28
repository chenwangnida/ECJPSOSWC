package graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Node implements Cloneable {
	private String name;
	private double[] qos;
	private Set<String> inputs;
	private Set<String> outputs;
	private boolean consider = true;

	private List<Edge> incomingEdgeList = new ArrayList<Edge>();
	private List<Edge> outgoingEdgeList = new ArrayList<Edge>();
	// private List<TaxonomyNode> taxonomyOutputs = new
	// ArrayList<TaxonomyNode>();

	public Node(String name, double[] qos, Set<String> inputs, Set<String> outputs) {
		this.name = name;
		this.qos = qos;
		this.inputs = inputs;
		this.outputs = outputs;
	}

	public double[] getQos() {
		return qos;
	}

	public List<Edge> getIncomingEdgeList() {
		return incomingEdgeList;
	}

	public void setIncomingEdgeList(List<Edge> incomingEdgeList) {
		this.incomingEdgeList = incomingEdgeList;
	}

	public List<Edge> getOutgoingEdgeList() {
		return outgoingEdgeList;
	}

	public void setOutgoingEdgeList(List<Edge> outgoingEdgeList) {
		this.outgoingEdgeList = outgoingEdgeList;
	}

	public void setQos(double[] qos) {
		this.qos = qos;
	}

	public Set<String> getInputs() {
		return inputs;
	}

	public void setInputs(Set<String> inputs) {
		this.inputs = inputs;
	}

	public Set<String> getOutputs() {
		return outputs;
	}

	public void setOutputs(Set<String> outputs) {
		this.outputs = outputs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isConsider() {
		return consider;
	}

	public void setConsider(boolean consider) {
		this.consider = consider;
	}

	@Override
	public String toString() {
		if (consider)
			return name;
		else
			return name + "*";
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Node) {
			Node o = (Node) other;
			return name.equals(o.name);
		} else
			return false;
	}

}
