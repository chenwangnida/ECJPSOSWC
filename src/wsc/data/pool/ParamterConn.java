package wsc.data.pool;

public class ParamterConn {
	double matchType;
	String outputInst;
	String outputrequ;
	boolean isConsidered;

	public double getMatchType() {
		return matchType;
	}

	public String getOutputInst() {
		return outputInst;
	}

	public void setOutputInst(String outputInst) {
		this.outputInst = outputInst;
	}

	public String getOutputrequ() {
		return outputrequ;
	}

	public void setOutputrequ(String outputrequ) {
		this.outputrequ = outputrequ;
	}

	public void setMatchType(double matchType) {
		this.matchType = matchType;
	}

	public boolean isConsidered() {
		return isConsidered;
	}

	public void setConsidered(boolean isConsidered) {
		this.isConsidered = isConsidered;
	}

}
