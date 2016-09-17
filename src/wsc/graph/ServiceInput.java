package wsc.graph;

public class ServiceInput {

	private String input;
	boolean isSatified;

	public ServiceInput(String input, boolean isSatified) {
		super();
		this.input = input;
		this.isSatified = isSatified;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public boolean isSatified() {
		return isSatified;
	}

	public void setSatified(boolean isSatified) {
		this.isSatified = isSatified;
	}

}
