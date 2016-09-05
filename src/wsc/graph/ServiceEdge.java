package wsc.graph;

import org.jgraph.graph.DefaultEdge;

public class ServiceEdge extends DefaultEdge implements Cloneable {

	// average matching type value from source vertice to target vertice
	double avgmt;

	// average semantic distance value value from source vertice to target
	// vertice
	double avgsdt;

	public ServiceEdge(double avgmt, double avgsdt) {
		super();
		this.avgmt = avgmt;
		this.avgsdt = avgsdt;
	}

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

	@Override
	public ServiceEdge clone() {
		// TODO Auto-generated method stub
		return (ServiceEdge) super.clone();
	}

	@Override
	public String toString() {
		return this.avgmt+";"+this.avgsdt + super.toString();
	}

}
