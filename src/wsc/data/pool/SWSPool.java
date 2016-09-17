package wsc.data.pool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.jgrapht.DirectedGraph;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import wsc.graph.ServiceEdge;

public class SWSPool {

	private List<Service> serviceList = new LinkedList<Service>();
	private SemanticsPool semantics;

	private final Map<String, Service> graphOutputSetMap = new HashMap<String, Service>();

	public Map<String, Service> getGraphOutputSetMap() {
		return graphOutputSetMap;
	}

	public List<Service> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<Service> serviceList) {
		this.serviceList = serviceList;
	}

	public SemanticsPool getSemantics() {
		return semantics;
	}

	public void setSemantics(SemanticsPool semantics) {
		this.semantics = semantics;
	}


	/**
	 * Semantic web service pool initialization
	 *
	 * @param semantics
	 * @param servicefile
	 *            Path
	 *
	 * @return SemanticWebServicePool
	 */
//	public static SWSPool parseXML(SemanticsPool semantics, String serviceFilePath)
//			throws FileNotFoundException, JAXBException {
//		SWSPool swsp = new SWSPool();
//		swsp.semantics = semantics;
//		List<double[]> list = initialQoSfromSLA("qos.xml");
//
//		Definitions def = Definitions.parseXML(serviceFilePath);
//		for (int i = 0; i < def.getSemExtension().getSemMessageExtList().size(); i += 2) {
//			swsp.serviceList.add(Service.initialServicefromMECE(def.getSemExtension().getSemMessageExtList().get(i),
//					def.getSemExtension().getSemMessageExtList().get(i + 1)));
//		}
//
//		// manually add QoS attributes
//		for (int i = 0; i < list.size(); i++) {
//			swsp.qosServiceMap.put(swsp.serviceList.get(i).getServiceID(), list.get(i));
//			swsp.serviceList.get(i).setQos(list.get(i));
//		}
//
//		System.out.println("No.of Service:" + swsp.serviceList.size());
//		return swsp;
//	}

	public static SWSPool parseWSCServiceFile(SemanticsPool semantics, String fileName) {

		SWSPool swsp = new SWSPool();
		swsp.semantics = semantics;

		List<String> inputs = new ArrayList<String>();
		List<String> outputs = new ArrayList<String>();
		double[] qos = new double[4];

		try {
			File fXmlFile = new File(fileName);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			NodeList nList = doc.getElementsByTagName("service");

			for (int i = 0; i < nList.getLength(); i++) {
				org.w3c.dom.Node nNode = nList.item(i);
				Element eElement = (Element) nNode;
				// service name, for example serv904934656
				String name = eElement.getAttribute("name");

				qos[0] = Double.valueOf(eElement.getAttribute("Res"));
				qos[1] = Double.valueOf(eElement.getAttribute("Pri"));
				qos[2] = Double.valueOf(eElement.getAttribute("Ava"));
				qos[3] = Double.valueOf(eElement.getAttribute("Rel"));

				// Get inputs, instance name, for example inst995667695
				org.w3c.dom.Node inputNode = eElement.getElementsByTagName("inputs").item(0);
				NodeList inputNodes = ((Element) inputNode).getElementsByTagName("instance");
				for (int j = 0; j < inputNodes.getLength(); j++) {
					org.w3c.dom.Node in = inputNodes.item(j);
					Element e = (Element) in;
					inputs.add(e.getAttribute("name"));
				}

				// Get outputs instance name, for example inst1348768777
				org.w3c.dom.Node outputNode = eElement.getElementsByTagName("outputs").item(0);
				NodeList outputNodes = ((Element) outputNode).getElementsByTagName("instance");
				for (int j = 0; j < outputNodes.getLength(); j++) {
					org.w3c.dom.Node out = outputNodes.item(j);
					Element e = (Element) out;
					outputs.add(e.getAttribute("name"));
				}

				Service ws = new Service(name, qos, inputs, outputs);
				swsp.serviceList.add(ws);

				inputs = new ArrayList<String>();
				outputs = new ArrayList<String>();
				qos = new double[4];
			}

		} catch (IOException ioe) {
			System.out.println("Service file parsing failed...");
		} catch (ParserConfigurationException e) {
			System.out.println("Service file parsing failed...");
		} catch (SAXException e) {
			System.out.println("Service file parsing failed...");
		}
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

	/**
	 * find a single service that can be applied now and update the output list
	 * and delete the service
	 *
	 * @param inputSet
	 */
	public Service createGraphService(List<String> inputSet, List<Service> serviceCandidates,
			SemanticsPool semanticsPool, DirectedGraph<String, ServiceEdge> directedGraph) {
		int foundServiceIndex = -1;

		for (int i = 0; i < serviceCandidates.size(); i++) {
			Service s = new Service(serviceCandidates.get(i).getServiceID());
			if (s.searchServiceGraphMatchFromInputSet(semanticsPool, serviceCandidates.get(i), inputSet, directedGraph,
					this.graphOutputSetMap)) {
				foundServiceIndex = i;
				break;
			}
		}
		if (foundServiceIndex == -1) {
			System.out.println("no matching for inputSet");
			return null;
		}
		Service service = serviceCandidates.get(foundServiceIndex);
		serviceCandidates.remove(foundServiceIndex);
		// add found service outputs to inputSet
		for (String output : service.getOutputList()) {
			if (!inputSet.contains(output)) {
				inputSet.add(output);
				// output mapped back to service
				graphOutputSetMap.put(output, service);
			}
		}
		return service;
	}

}
