package wsc.graph;

public class ServiceOutput {

	private String output;
	boolean isSatified;

	public ServiceOutput(String output, boolean isSatified) {
		super();
		this.output = output;
		this.isSatified = isSatified;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public boolean isSatified() {
		return isSatified;
	}

	public void setSatified(boolean isSatified) {
		this.isSatified = isSatified;
	}
}
