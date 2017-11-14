import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import co.nstant.in.cbor.CborException;
import tools.DataSource;
import tools.XMLReader;

import index.*;

public class Main {

	// Query variables
	static String number;
	static String type;
	static String note;
	static String description;
	static String summary;

	static ArrayList<String> numberlist = new ArrayList<>();
	static ArrayList<String> typelist = new ArrayList<>();
	static ArrayList<String> notelist = new ArrayList<>();
	static ArrayList<String> descriptionlist = new ArrayList<>();
	static ArrayList<String> summarylist = new ArrayList<>();

	static void read_Query_details_from_XMLFile(String filename) {
		try {
			File XmlFile = new File(filename);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(XmlFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("topic");
			System.out.println("----------------------------");

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;
					number = eElement.getAttribute("number");
					type = eElement.getAttribute("type");
					note = eElement.getElementsByTagName("note").item(0).getTextContent();
					description = eElement.getElementsByTagName("description").item(0).getTextContent();
					summary = eElement.getElementsByTagName("summary").item(0).getTextContent();

					numberlist.add(number);
					typelist.add(type);
					notelist.add(note);
					descriptionlist.add(description);
					summarylist.add(summary);

				}
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws FileNotFoundException, CborException {
		// TODO Auto-generated method stub

		String filename = "/Users/Nithin/Desktop/topics2016.xml";
		String dir_to_Index = "/Users/Nithin/Desktop/PubMedDatas";
		String INDEX_DIR = "/Users/Nithin/Desktop/ClinicalIndex";

		read_Query_details_from_XMLFile(filename);
		
		//Indexer oneTimeIndex = new Indexer(dir_to_Index, INDEX_DIR);
		


	}
}
