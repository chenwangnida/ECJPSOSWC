package wsc.data.pool;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBException;

import wsc.wsdl.bean.Definitions;

public class SWSPool {

	private final List<Service> serviceList = new LinkedList<Service>();
	private SemanticsPool semantics;


	/**
	 * Semantic web service pool initialization
	 *
	 * @param semantics
	 * @param servicefile Path
	 *
	 * @return SemanticWebServicePool
	 */
	public static SWSPool parseXML(SemanticsPool semantics,
			String serviceFilePath) throws FileNotFoundException, JAXBException {
		SWSPool swsp = new SWSPool();
		swsp.semantics = semantics;
		Definitions def = Definitions.parseXML(serviceFilePath);
		for (int i = 0; i < def.getSemExtension().getSemMessageExtList().size(); i+=2){
			swsp.serviceList.add(Service.initialServicefromMECE(def.getSemExtension()
					.getSemMessageExtList().get(i), def.getSemExtension()
					.getSemMessageExtList().get(i + 1)));
		}

		System.out.println("No.of Service:"+ swsp.serviceList.size());
		return swsp;
	}

	/**
	 * find a single service that can be applied now and update the output list and
	 * delete the service
	 *
	 * @param inputSet
	 */
	public Service findPossibleService(HashSet<String> inputSet) {
		int foundServiceIndex = -1;
		for (int i = 0; i < this.serviceList.size(); i++) {
			Service service = this.serviceList.get(i);

			if (service.searchServiceMatchFromInputSet(this.semantics, inputSet)) {
				foundServiceIndex = i;
				break;
			}
		}
		if (foundServiceIndex == -1) {
			System.out.println("no matching for inputSet");
			return null;
		}
		Service service = this.serviceList.get(foundServiceIndex);
		this.serviceList.remove(foundServiceIndex);
		//add found service outputs to inputSet
		for (String output : service.getOutputList()) {
			if (!inputSet.contains(output)) {
				inputSet.add(output);
			}
		}
		return service;
	}

}
