package wsc.data.pool;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import wsc.graph.ParamterConn;
import wsc.owl.bean.OWLClass;
import wsc.owl.bean.OWLInst;
import wsc.owl.bean.RDF;

public class SemanticsPool {

	private final HashMap<String, OWLClass> owlClassHashMap = new HashMap<String, OWLClass>();
	private final HashMap<String, OWLInst> owlInstHashMap = new HashMap<String, OWLInst>();

	public HashMap<String, OWLClass> getOwlClassHashMap() {
		return this.owlClassHashMap;
	}

	public HashMap<String, OWLInst> getOwlInstHashMap() {
		return this.owlInstHashMap;
	}

	/**
	 * Semantics Initialization from OWL
	 *
	 * @param filepath
	 * @return semantics
	 */
	public static SemanticsPool createSemanticsFromOWL(String filePath) throws FileNotFoundException, JAXBException {
		RDF rdf = RDF.parseXML(filePath);
		SemanticsPool sp = new SemanticsPool();
		System.out.println("No.of Class put in HashMap:" + rdf.getOwlClassList().size());
		System.out.println("No.of Instance put in HashMap:" + rdf.getOwlInstList().size());
		for (OWLClass cl : rdf.getOwlClassList()) {
			sp.owlClassHashMap.put(cl.getID(), cl);
		}
		for (OWLInst inst : rdf.getOwlInstList()) {
			sp.owlInstHashMap.put(inst.getID(), inst);
		}
		return sp;
	}

	/**
	 * check semantic matching given two instances check if givenInst equals to
	 * relatedInst or givenInst is subclass of relatedInst
	 *
	 * @param givenInst
	 * @param existInst
	 * @return boolean
	 */
//	public boolean searchSemanticMatchFromInst(String givenInst, String existInst) {
//
//		OWLClass givenClass = this.owlClassHashMap
//				.get(this.owlInstHashMap.get(givenInst).getRdfType().getResource().substring(1));
//		OWLClass relatedClass = this.owlClassHashMap
//				.get(this.owlInstHashMap.get(existInst).getRdfType().getResource().substring(1));
//
//		// search for the potential semantic matching relationship
//
//		while (true) {
//			// Exact and PlugIn matching types
//			if (givenClass.getID().equals(relatedClass.getID())) {
//				return true;
//			}
//			if (givenClass.getSubClassOf() == null || givenClass.getSubClassOf().getResource().equals("")) {
//				break;
//			}
//			givenClass = this.owlClassHashMap.get(givenClass.getSubClassOf().getResource().substring(1));
//		}
//
//		return false;
//	}

	public ParamterConn searchSemanticMatchFromInst(String givenInst, String existInst) {

		OWLClass givenClass = this.owlClassHashMap
				.get(this.owlInstHashMap.get(givenInst).getRdfType().getResource().substring(1));
		OWLClass relatedClass = this.owlClassHashMap
				.get(this.owlInstHashMap.get(existInst).getRdfType().getResource().substring(1));

		// search for the potential semantic matching relationship
		ParamterConn pConn = new ParamterConn();

		while (true) {
			// Exact and PlugIn matching types
			if (givenClass.getID().equals(relatedClass.getID())) {
				pConn.setConsidered(true);
				return pConn;
			}
			if (givenClass.getSubClassOf() == null || givenClass.getSubClassOf().getResource().equals("")) {
				break;
			}
			givenClass = this.owlClassHashMap.get(givenClass.getSubClassOf().getResource().substring(1));
		}
		pConn.setConsidered(false);
		return pConn;
	}

	public ParamterConn searchSemanticMatchTypeFromInst(String givenInst, String existInst) {

		OWLClass givenClass = this.owlClassHashMap
				.get(this.owlInstHashMap.get(givenInst).getRdfType().getResource().substring(1));
		OWLClass relatedClass = this.owlClassHashMap
				.get(this.owlInstHashMap.get(existInst).getRdfType().getResource().substring(1));

		// search for the potential semantic matching relationship
		ParamterConn pConn = new ParamterConn();

		int i = 0;

		while (true) {
			// Exact and PlugIn matching types
			if (givenClass.getID().equals(relatedClass.getID())) {
				pConn.setConsidered(true);
				if(i==0 ){
					pConn.setMatchType(1);

				}else{
					pConn.setMatchType(0.5);
				}
				return pConn;
			}
			if (givenClass.getSubClassOf() == null || givenClass.getSubClassOf().getResource().equals("")) {
				break;
			}
			givenClass = this.owlClassHashMap.get(givenClass.getSubClassOf().getResource().substring(1));
			i++;
		}
		pConn.setConsidered(false);
		return pConn;
	}

	// /** Test data from unmarshalling process
	// * @param args
	// */
	// public static void main(String[] args) {
	// // TODO Auto-generated method stub
	// try {
	// SemanticsPool sp = SemanticsPool.createFromXML("Taxonomy.owl");
	// } catch (FileNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (JAXBException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }

}
