package wsc.data.pool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import wsc.wsdl.bean.SemExt;
import wsc.wsdl.bean.SemMessageExt;

public class Service {

	private final String serviceID;
	// list of inputInstances(individuals), rather than list of input parameter.
	private  double[] qos;

	private final List<String> inputList = new ArrayList<String>();
	// list of outputInstances(individuals), ranther than list of output
	// parameter.
	private final List<String> outputList = new ArrayList<String>();

	public String getServiceID() {
		return this.serviceID;
	}

	public List<String> getInputList() {
		return this.inputList;
	}

	public List<String> getOutputList() {
		return this.outputList;
	}


	public double[] getQos() {
		return qos;
	}

	public void setQos(double[] qos) {
		this.qos = qos;
	}

	public Service(String serviceID) {
		this.serviceID = serviceID;
	}

	/**
	 * Initial service from SemMessageExt
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	public static Service initialServicefromMECE(SemMessageExt request, SemMessageExt response) {
		String serviceID = request.getServiceID();
		// verify request and response from same service.
		if (response.getServiceID().equals(serviceID) == false) {
			System.err.println("Service ID does not match");
			return null;
		}
		// valid request and response
		if (!request.isRequestMessage() || response.isRequestMessage()) {
			System.err.println("SemMessageExt type does not match");
			return null;
		}
		// initial data for service
		Service service = new Service(serviceID);
		for (SemExt se : request.getSemExt()) {
			int instBeginPos = se.getOntologyRef().indexOf("inst");
			service.inputList.add(se.getOntologyRef().substring(instBeginPos));
		}
		for (SemExt se : response.getSemExt()) {
			int instBeginPos = se.getOntologyRef().indexOf("inst");
			service.outputList.add(se.getOntologyRef().substring(instBeginPos));
		}
		return service;
	}

	/**
	 * search for services matched with current inputSet
	 *
	 * @param semanticsPool
	 * @param intputList
	 * @return boolean
	 */
	public boolean searchServiceMatchFromInputSet(SemanticsPool semanticsPool, HashSet<String> inputSet) {
		int inputMatchCount = 0;
		// check if the inputSet contains all the required inputs from services
		for (String giveninput : inputSet) {
			for (int i = 0; i < this.inputList.size(); i++) {

				String existInput = this.inputList.get(i);
				if (semanticsPool.searchSemanticMatchFromInst(giveninput, existInput)) {
					inputMatchCount++;
					// contain complete match from a single service
					if (inputMatchCount == this.inputList.size()) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
