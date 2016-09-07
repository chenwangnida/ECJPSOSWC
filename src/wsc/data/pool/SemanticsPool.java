package wsc.data.pool;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

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
	public boolean searchSemanticMatchFromInst(String givenInst, String existInst) {

		OWLClass givenClass = this.owlClassHashMap
				.get(this.owlInstHashMap.get(givenInst).getRdfType().getResource().substring(1));
		OWLClass relatedClass = this.owlClassHashMap
				.get(this.owlInstHashMap.get(existInst).getRdfType().getResource().substring(1));

		// search for the potential semantic matching relationship

		while (true) {
			int i = 0;
			// Exact and PlugIn matching types
			if (givenClass.getID().equals(relatedClass.getID())) {
				return true;
			}
			if (givenClass.getSubClassOf() == null || givenClass.getSubClassOf().getResource().equals("")) {
				break;
			}
			givenClass = this.owlClassHashMap.get(givenClass.getSubClassOf().getResource().substring(1));
			i++;
		}

		return false;
	}

	public Map<Double, Boolean> searchSemanticMatchTypeFromInst(String givenInst, String existInst) {

		OWLClass givenClass = this.owlClassHashMap
				.get(this.owlInstHashMap.get(givenInst).getRdfType().getResource().substring(1));
		OWLClass relatedClass = this.owlClassHashMap
				.get(this.owlInstHashMap.get(existInst).getRdfType().getResource().substring(1));

		// search for the potential semantic matching relationship
		Map<Double, Boolean> matchType = new HashMap<Double, Boolean>();
		// no match
		matchType.put(0.00, false);
		// exact match
		matchType.put(1.00, false);
		// plugin match
		matchType.put(0.75, false);
		// subsume match
		matchType.put(0.50, false);
		// intersection match
		matchType.put(0.25, false);

		while (true) {
			int i = 0;
			// Exact and PlugIn matching types
			if (givenClass.getID().equals(relatedClass.getID())) {
				if (i == 0) {
					matchType.put(0.00, false);
					// exact match
					matchType.put(1.00, true);
					// plugin match
					matchType.put(0.75, false);
					// subsume match
					matchType.put(0.50, false);
					// intersection match
					matchType.put(0.25, false);

				} else {
					matchType.put(0.00, false);
					// exact match
					matchType.put(1.00, false);
					// plugin match
					matchType.put(0.75, false);
					// subsume match
					matchType.put(0.50, true);
					// intersection match
					matchType.put(0.25, false);				}
				return matchType;
			}
			if (givenClass.getSubClassOf() == null || givenClass.getSubClassOf().getResource().equals("")) {
				break;
			}
			givenClass = this.owlClassHashMap.get(givenClass.getSubClassOf().getResource().substring(1));
			i++;
		}

		return matchType;
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
