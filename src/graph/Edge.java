package graph;

import java.util.Set;

import ec.pso.Edge;

public class Edge {
	private Node fromNode;
	private Node toNode;
	private Set<String> intersect;
	private boolean consider = true;

	public Node getFromNode() {
		return fromNode;
	}

	public void setFromNode(Node fromNode) {
		this.fromNode = fromNode;
	}

	public Node getToNode() {
		return toNode;
	}

	public void setToNode(Node toNode) {
		this.toNode = toNode;
	}

	public Set<String> getIntersect() {
		return intersect;
	}

	public void setIntersect(Set<String> intersect) {
		this.intersect = intersect;
	}

	public boolean isConsider() {
		return consider;
	}

	public void setConsider(boolean consider) {
		this.consider = consider;
	}

	@Override
	public int hashCode() {
		return (fromNode.getName() + toNode.getName()).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (other instanceof Edge) {
			Edge o = (Edge) other;
			return fromNode.getName().equals(o.fromNode.getName()) && toNode.getName().equals(o.toNode.getName());
		} else
			return false;

	}

	@Override
	public String toString() {
		if (consider)
			return String.format("%s -> %s", fromNode, toNode);
		else
			return String.format("%s **> %s", fromNode, toNode);
	}
}
