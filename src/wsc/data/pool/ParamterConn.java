package wsc.data.pool;

public class ParamterConn {
	double matchType;
	double similarity;
	String outputInst;
	String outputrequ;
	String SourceServiceID;
	boolean isConsidered;

	public double getSimilarity() {
		return similarity;
	}

	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}

	public String getSourceServiceID() {
		return SourceServiceID;
	}

	public void setSourceServiceID(String sourceServiceID) {
		SourceServiceID = sourceServiceID;
	}

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
