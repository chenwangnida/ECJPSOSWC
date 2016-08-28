package wsc.data.pool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import wsc.wsdl.bean.Definitions;

public class SWSPool {

	private List<Service> serviceList = new LinkedList<Service>();
	private SemanticsPool semantics;

	/**
	 * Semantic web service pool initialization
	 *
	 * @param semantics
	 * @param servicefile
	 *            Path
	 *
	 * @return SemanticWebServicePool
	 */
	public static SWSPool parseXML(SemanticsPool semantics, String serviceFilePath)
			throws FileNotFoundException, JAXBException {
		SWSPool swsp = new SWSPool();
		swsp.semantics = semantics;

		List<double[]> list = initialQoSfromSLA("qos.xml");

		Definitions def = Definitions.parseXML(serviceFilePath);
		for (int i = 0; i < def.getSemExtension().getSemMessageExtList().size(); i += 2) {
			swsp.serviceList.add(Service.initialServicefromMECE(def.getSemExtension().getSemMessageExtList().get(i),
					def.getSemExtension().getSemMessageExtList().get(i + 1)));
		}

		for(int i=0; i< list.size();i++){

			swsp.serviceList.get(i).setQos(list.get(i));
		}

		System.out.println("No.of Service:" + swsp.serviceList.size());
		return swsp;
	}

	/**
	 * Initial service nonfunctional attributes from SLA
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	public static List<double[]> initialQoSfromSLA(String fileName) {
		List<double[]> qosList = new ArrayList<double[]>();


		try {
			File fXmlFile = new File(fileName);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			NodeList nList = doc.getElementsByTagName("service");

			for (int i = 0; i < nList.getLength(); i++) {
				org.w3c.dom.Node nNode = nList.item(i);
				Element eElement = (Element) nNode;
				double[] qos = new double[4];
				qos[0] = Double.valueOf(eElement.getAttribute("Res"));
				qos[1] = Double.valueOf(eElement.getAttribute("Pri"));
				qos[2] = Double.valueOf(eElement.getAttribute("Ava"));
				qos[3] = Double.valueOf(eElement.getAttribute("Rel"));
				qosList.add(qos);

			}

		} catch (IOException ioe) {
			System.out.println("Service file parsing failed...");
		} catch (ParserConfigurationException e) {
			System.out.println("Service file parsing failed...");
		} catch (SAXException e) {
			System.out.println("Service file parsing failed...");
		}
		System.out.println(qosList.size());
		return qosList;
	}

	/**
	 * find a single service that can be applied now and update the output list
	 * and delete the service
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
		// add found service outputs to inputSet
		for (String output : service.getOutputList()) {
			if (!inputSet.contains(output)) {
				inputSet.add(output);
			}
		}
		return service;
	}

}
