package wsc.wsdl.bean;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import wsc.data.pool.NamespaceManager;

public class SemExt {

	private String id;
	private String ontologyRef;

	@XmlAttribute(name="id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlElement(namespace=NamespaceManager.MECE_NAMESPACE)
	public String getOntologyRef() {
		return ontologyRef;
	}

	public void setOntologyRef(String ontologyRef) {
		this.ontologyRef = ontologyRef;
	}

}
