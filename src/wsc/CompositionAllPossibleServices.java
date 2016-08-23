package wsc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.JAXBException;

import wsc.data.pool.SWSPool;
import wsc.data.pool.SemanticsPool;

public class CompositionAllPossibleServices {

	private final SWSPool swsPool;

	// current output instance list
	private final HashSet<String> outputSet = new HashSet<String>();
	private final SemanticsPool semanticsPool;

	/**
	 * check whether output is required by the task
	 *
	 * @param givenoutput
	 * @return
	 */
	private boolean checkOutputSet(String output) {
		for (String outputInst : this.outputSet) {
			if (this.semanticsPool.searchSemanticMatchFromInst(outputInst, output)) {
				return true;
			}
		}
		return false;
	}
	


	/**
	 * given a task associated with input and output to find a potential
	 * services
	 *
	 * @param giveninput
	 *
	 * @param givenoutput
	 *
	 */
	public void compositeServices(String input, String output) throws JAXBException, IOException {
		this.outputSet.add(input);
		List<String> serviceSequence = new LinkedList<String>();
		do {
			String serviceID = this.swsPool.findPossibleService(this.outputSet);
			if (serviceID == null) {
				System.err.println("No service is satisfied");
				return;
			}
			System.out.println("choose service " + serviceID);
			serviceSequence.add(serviceID);
		} while (!this.checkOutputSet(output));
	}

	/**
	 * using service file and owl file to create semantics pool and service pool
	 *
	 * @param serviceFilePath
	 * @param owlFilePath
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 */
	public CompositionAllPossibleServices(String serviceFilePath, String owlFilePath)
			throws FileNotFoundException, JAXBException {
		this.semanticsPool = SemanticsPool.createSemanticsFromOWL(owlFilePath);
		this.swsPool = SWSPool.parseXML(this.semanticsPool, serviceFilePath);
	}
}
