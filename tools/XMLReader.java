package tools;

import javax.xml.parsers.DocumentBuilderFactory;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import co.nstant.in.cbor.CborException;
import co.nstant.in.cbor.model.DataItem;
import tools.DataSource.topics;


import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class XMLReader {
	
	
	
	
	public static Iterator<DataSource.topics> iterTopics(final String filename)
	{

		return new Iterator<DataSource.topics>() {
			DataSource.topics next = lowLevelNext();
			@Override
			public boolean hasNext() {
				// TODO Auto-generated method stub
				return next!=null;
			}

			
			private DataSource.topics lowLevelNext() {
                
				DataSource.topics top = topicsfromXML(filename);
				return top;
 
            }
			@Override
			public DataSource.topics next() {
				// TODO Auto-generated method stub
				DataSource.topics curr = next;
                try {
                    next = lowLevelNext();
                } catch (Exception e) {
                    e.printStackTrace();
                    next=null;
                }
                return curr;
				
				
			}
			
			
		};
		
	}
	
	
	
	public static DataSource.topics topicsfromXML(final String filename)
	{

		String number = null, type = null, note = null, description = null, summary = null;
		ArrayList<String> numberlist = new ArrayList<>();
		ArrayList<String> typelist = new ArrayList<>();
		ArrayList<String> notelist = new ArrayList<>();
		ArrayList<String> descriptionlist = new ArrayList<>();
		ArrayList<String> summarylist = new ArrayList<>();
		try{
			File XmlFile = new File(filename);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(XmlFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("topic");
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
		
		return new DataSource.topics(numberlist, typelist, notelist, descriptionlist, summarylist);
		//return new DataSource.topics(number, type, note, description, summary);
		
	}
	public static Iterable<DataSource.topics> iterableTopics(final String filename) 
	{

		return new Iterable<DataSource.topics>() {
			
			@Override
			public Iterator<topics> iterator() {
				// TODO Auto-generated method stub
				return iterTopics(filename);
			}
		};
		
		
		
		
		
	}
	
	
	
	
	
	
	
//	//String query_file_name = "/Users/Nithin/Desktop/topcs2016.xml";
//			File fXmlFile = new File(query_file_name);
//			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//			
//			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//			return null;
	
	
	
}
