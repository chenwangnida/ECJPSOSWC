package wsc.wsdl.bean;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import wsc.data.pool.NamespaceManager;

public class SemExtention {

	private List<SemMessageExt> semMessageExtList = new ArrayList<SemMessageExt>();

	@XmlElement(name="semMessageExt", namespace=NamespaceManager.MECE_NAMESPACE)
	public List<SemMessageExt> getSemMessageExtList() {
		return semMessageExtList;
	}

}
