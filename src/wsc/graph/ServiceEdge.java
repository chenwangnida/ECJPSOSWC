package wsc.graph;

import org.jgraph.graph.DefaultEdge;

public class ServiceEdge extends DefaultEdge {

	// average matching type value from source vertice to target vertice
	double avgmt;

	// average semantic distance value value from source vertice to target vertice
	double avgsdt;

	public double getAvgmt() {
		return avgmt;
	}

	public void setAvgmt(double avgmt) {
		this.avgmt = avgmt;
	}

	public double getAvgsdt() {
		return avgsdt;
	}

	public void setAvgsdt(double avgsdt) {
		this.avgsdt = avgsdt;
	}

}
