package wsc.wsdl.bean;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import wsc.data.pool.NamespaceManager;

@XmlRootElement(name="definitions")
public class Definitions {

	private SemExtention semExtension;

	@XmlElement(name="semExtension", namespace=NamespaceManager.MECE_NAMESPACE)
	public SemExtention getSemExtension() {
		return semExtension;
	}

	public void setSemExtension(SemExtention semExtension) {
		this.semExtension = semExtension;
	}

	public static Definitions parseXML(String filePath) throws JAXBException{
		JAXBContext jc = JAXBContext.newInstance(Definitions.class);
		return (Definitions)jc.createUnmarshaller().unmarshal(new File(filePath));
	}
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		try {
//			Definitions def = Definitions.parseXML("Services.wsdl");
//			for (SemMessageExt sme : def.getSemExtension().getSemMessageExtList()){
//				System.out.println(sme.getId());
//				for (SemExt se: sme.getSemExt()){
//					System.out.println(se.getId());
//				}
//			}
//		} catch (JAXBException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

}
