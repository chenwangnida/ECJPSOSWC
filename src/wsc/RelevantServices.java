package wsc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.JAXBException;

import wsc.data.pool.SWSPool;
import wsc.data.pool.SemanticsPool;
import wsc.data.pool.Service;

public class RelevantServices {

	private final SWSPool swsPool;

	// current output instance list
	private final HashSet<String> outputSet = new HashSet<String>();
	private final SemanticsPool semanticsPool;

	// save all the relevant services
	private final List<Service> serviceSequence = new LinkedList<Service>();

	//set and get
	public SWSPool getSwsPool() {
		return swsPool;
	}

	public HashSet<String> getOutputSet() {
		return outputSet;
	}

	public SemanticsPool getSemanticsPool() {
		return semanticsPool;
	}

	public List<Service> getServiceSequence() {
		return serviceSequence;
	}

	/**
	 * using service file and owl file to create semantics pool and service pool
	 *
	 * @param serviceFilePath
	 * @param owlFilePath
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 */
	public RelevantServices(String serviceFilePath, String owlFilePath) throws FileNotFoundException, JAXBException {
		this.semanticsPool = SemanticsPool.createSemanticsFromOWL(owlFilePath);
		this.swsPool = SWSPool.parseXML(this.semanticsPool, serviceFilePath);
	}

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
	public void allRelevantService(String input, String output) throws JAXBException, IOException {
		this.outputSet.add(input);
		do {
			Service service = this.swsPool.findPossibleService(this.outputSet);
			if (service == null) {
				System.out.println("No more service satisfied");
				return;
			}
			System.out.println("choose service " + service.getServiceID());
			serviceSequence.add(service);
		} while (true);// while(!this.checkOutputSet(output))
	}

}